-- CREATE TABLE types(id INTEGER PRIMARY KEY ASC, name TEXT, extra_info JSON, type_links JSON);
-- CREATE TABLE gcs(time INT, retained_bytes INT, promoted_bytes INT, gen2_roots INT, stolen_gen2_roots INT, full INT, responsible INT, cleared_bytes INT, start_time INT, sequence_num INT, thread_id INT, PRIMARY KEY(sequence_num, thread_id));
-- CREATE TABLE profile(total_time INT, spesh_time INT, thread_id INT, parent_thread_id INT, root_node INT, first_entry_time INT, FOREIGN KEY(root_node) REFERENCES calls(id));
-- CREATE TABLE allocations(call_id INT, type_id INT, spesh INT, jit INT, count INT, replaced INT, PRIMARY KEY(call_id, type_id), FOREIGN KEY(call_id) REFERENCES calls(id), FOREIGN KEY(type_id) REFERENCES types(id));
-- CREATE TABLE deallocations(gc_seq_num INT, gc_thread_id INT, type_id INT, nursery_fresh INT, nursery_seen INT, gen2 INT, PRIMARY KEY(gc_seq_num, gc_thread_id, type_id), FOREIGN KEY(gc_seq_num, gc_thread_id) REFERENCES gcs(sequence_num, thread_id), FOREIGN KEY(type_id) REFERENCES types(id));

-- Union all the types together so we have an easier time grading them
WITH
all_types AS (
  SELECT 1 AS db, * FROM db1.types
UNION
  SELECT 2 AS db, * FROM db2.types
),

-- Grade the types by the number of homographs they have
graded AS (
  SELECT db, id, name,
         COUNT(*) OVER by_name AS count_by_name
  FROM all_types
  WINDOW
  by_name AS
    (PARTITION BY db, name)
),

-- Matches types that are unique by name. Types that aren't are discarded.
matches AS (
  SELECT lft, rgt, name FROM (
  SELECT lft.id AS lft, rgt.id AS rgt, lft.name AS name
  FROM graded lft
  LEFT JOIN graded rgt
    ON rgt.db = 2
    AND rgt.count_by_name = 1
    AND lft.name = rgt.name
  WHERE lft.db = 1
  AND lft.count_by_name = 1
  UNION ALL
  SELECT lft.id AS lft, rgt.id AS rgt, rgt.name AS name
  FROM graded rgt
  LEFT JOIN graded lft
    ON lft.db = 1
    AND rgt.count_by_name = 1
	AND lft.name = NULL
  WHERE rgt.db = 2
  AND rgt.count_by_name = 1
  ) GROUP BY name
),

-- Union all the allocations together so we have an easier time computing statistics
all_allocations AS (
  SELECT 1 AS db, * FROM db1.allocations
UNION
  SELECT 2 AS db, * FROM db2.allocations
),

-- Compute statistics for our allocations based on their type_id
allocations_by_type AS (
  SELECT db, type_id,
         sum(spesh) spesh,
         sum(jit) jit,
         sum(count) count,
         sum(replaced) replaced
  FROM all_allocations
  GROUP BY 1, 2
),

-- Union all the deallocations together so we have an easier time computing statistics
all_deallocations AS (
  SELECT 1 as db, * FROM db1.deallocations
UNION
  SELECT 2 as db, * FROM db2.deallocations
),

deallocations_by_type AS (
  SELECT db, type_id,
         count(gc_seq_num) num_gcs, -- count(*) really
         -- sum(gc_thread_id) gc_thread_id, -- ???
         sum(nursery_fresh) nursery_fresh,
         sum(nursery_seen) nursery_seen,
         sum(gen2) gen2
  FROM all_deallocations
  GROUP BY 1, 2
)

SELECT m.*,
       a1.spesh AS c1_spesh, a1.jit AS c1_jit, a1.count AS c1_count, a1.replaced AS c1_replaced,
       coalesce(a2.spesh, 0) AS c2_spesh, coalesce(a2.jit, 0) AS c2_jit, coalesce(a2.count, 0) AS c2_count, coalesce(a2.replaced, 0) AS c2_replaced,
       d1.num_gcs AS c1_num_gcs, d1.nursery_fresh AS c1_nursery_fresh, d1.nursery_seen AS c1_nursery_seen, d1.gen2 AS c1_gen2,
       coalesce(d2.num_gcs, 0) AS c2_num_gcs, coalesce(d2.nursery_fresh, 0) AS c2_nursery_fresh, coalesce(d2.nursery_seen, 0) AS c2_nursery_seen, coalesce(d2.gen2, 0) AS c2_gen2
FROM matches m
-- The left side's allocations
LEFT JOIN allocations_by_type a1
  ON a1.db = 1
  AND a1.type_id = m.lft
-- The right side's allocations
LEFT JOIN allocations_by_type a2
  ON a2.db = 2
  AND a2.type_id = m.rgt
-- The left side's deallocations
LEFT JOIN deallocations_by_type d1
  ON d1.db = 1
  AND d1.type_id = m.lft
-- The right side's deallocations
LEFT JOIN deallocations_by_type d2
  ON d2.db = 2
  AND d2.type_id = m.lft