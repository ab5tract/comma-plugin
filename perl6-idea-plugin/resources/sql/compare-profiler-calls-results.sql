-- CREATE TABLE routines(id INTEGER PRIMARY KEY ASC, name TEXT, line INT, file TEXT);
-- CREATE TABLE calls(id INTEGER PRIMARY KEY ASC, parent_id INT, routine_id INT, osr INT, spesh_entries INT, jit_entries INT, inlined_entries INT, inclusive_time INT, exclusive_time INT, entries INT, deopt_one INT, deopt_all INT, rec_depth INT, first_entry_time INT, highest_child_id INT, FOREIGN KEY(routine_id) REFERENCES routines(id));

WITH
all_routines AS (
  SELECT 1 AS db, * FROM db1.routines
UNION
  SELECT 2 AS db, * FROM db2.routines
),

graded AS (
  SELECT db, id, file, line,
         CASE
             WHEN name = '' THEN NULL
             WHEN name = '<unit-outer>' THEN NULL
             ELSE name
             END                           AS name,
         COUNT(*) OVER by_name             AS count_by_name,
         COUNT(*) OVER by_file_by_name     AS count_by_file_by_name,
         COUNT(*) OVER by_file_by_line     AS count_by_file_by_line,
         ROW_NUMBER() OVER by_file_by_name AS nth_by_file_by_name,
         ROW_NUMBER() OVER by_file_by_line AS nth_by_file_by_line
  FROM all_routines
  WINDOW
  by_name AS
    (PARTITION BY db, name),
  by_file_by_name AS
    (PARTITION BY db, file, name ORDER BY line),
  by_file_by_line AS
    (PARTITION BY db, file, line)
),

lft AS (
  SELECT *
  FROM graded
  WHERE db = 1
),

rgt AS (
  SELECT *
  FROM graded
  WHERE db = 2
),

best_matches AS (
  SELECT lft.id, coalesce(c1.id, c2.id, c3.id, c4.id, c5.id) AS best_match_id
  FROM lft

  -- First, find a singular sub with that name
  LEFT JOIN rgt AS c1
    ON lft.count_by_name = 1
    AND c1.count_by_name = 1
    AND lft.name = c1.name

  -- Otherwise, a singular sub with that name in that file
  LEFT JOIN rgt AS c2
    ON lft.count_by_file_by_name = 1
    AND c1.count_by_file_by_name = 1
    AND lft.name = c2.name
    AND lft.file = c2.file

  -- Otherwise, the only sub on that same line
  LEFT JOIN rgt AS c3
    ON lft.name = c3.name
    AND lft.file = c3.file
    AND lft.line = c3.line
    AND lft.count_by_file_by_line = 1
    AND c3.count_by_file_by_line = 1

  -- Otherwise, the N-th sub on that line
  LEFT JOIN rgt AS c4
    ON lft.name = c4.name
    AND lft.file = c4.file
    AND lft.line = c4.line
    AND lft.nth_by_file_by_line = c4.nth_by_file_by_line

  -- Otherwise, the N-th sub in that file
  LEFT JOIN rgt AS c5
    ON lft.name = c5.name
    AND lft.file = c5.file
    AND lft.nth_by_file_by_name = c5.nth_by_file_by_name
),

aggregated AS (
  SELECT bm.id,
         first_value(bm.best_match_id) OVER (PARTITION BY bm.id) AS best_match_id
  FROM best_matches bm
  WHERE best_match_id IS NOT NULL
  GROUP BY 1
),

all_calls AS (
  SELECT 1 AS db, * FROM db1.calls
UNION
  SELECT 2 AS db, * FROM db2.calls
),

aggregated_calls AS (
    SELECT db, routine_id,
           sum(spesh_entries)   AS spesh_entries,
           sum(jit_entries)     AS jit_entries,
           sum(inlined_entries) AS inlined_entries,
           sum(inclusive_time)  AS inclusive_time,
           sum(exclusive_time)  AS exclusive_time,
           sum(entries)         AS entries,
           sum(deopt_one)       AS deopt_one,
           sum(deopt_all)       AS deopt_all
    FROM all_calls
    GROUP BY 1, 2
)

SELECT a.id, a.best_match_id,
       r1.name AS r1_name, r2.name AS r2_name,
       c1.spesh_entries AS c1_spesh_entries, c1.jit_entries AS c1_jit_entries, c1.inlined_entries AS c1_inlined_entries, c1.inclusive_time AS c1_inclusive_time, c1.exclusive_time AS c1_exclusive_time, c1.entries AS c1_entries, c1.deopt_one AS c1_deopt_one, c1.deopt_all AS c1_deopt_all,
       c2.spesh_entries AS c2_spesh_entries, c2.jit_entries AS c2_jit_entries, c2.inlined_entries AS c2_inlined_entries, c2.inclusive_time AS c2_inclusive_time, c2.exclusive_time AS c2_exclusive_time, c2.entries AS c2_entries, c2.deopt_one AS c2_deopt_one, c2.deopt_all AS c2_deopt_all
FROM aggregated a
LEFT JOIN db1.routines r1
  ON r1.id = a.id
INNER JOIN db2.routines r2
  ON r2.id = a.best_match_id
INNER JOIN aggregated_calls c1
  ON c1.routine_id = a.id AND c1.db = 1
INNER JOIN aggregated_calls c2
  ON c2.routine_id = a.best_match_id AND c2.db = 2
