-- CREATE TABLE gcs(time INT, retained_bytes INT, promoted_bytes INT, gen2_roots INT, stolen_gen2_roots INT, full INT, responsible INT, cleared_bytes INT, start_time INT, sequence_num INT, thread_id INT, PRIMARY KEY(sequence_num, thread_id));

-- Compute GC statistics.
-- While we could aggregated all `gcs` entries into a single table,
-- this would be wasteful as the number of entries is very high, and also because the statistics we have to compute aren't too complex.

WITH gc_data AS (
  SELECT 1 AS db,
    sum(time) AS total_time,
    avg(time) AS avg_time,
    sum(retained_bytes) AS retained_bytes,
    sum(promoted_bytes) AS promoted_bytes
  FROM db1.gcs

  UNION

  SELECT 2 AS db,
      sum(time) AS total_time,
      avg(time) AS avg_time,
      sum(retained_bytes) AS retained_bytes,
      sum(promoted_bytes) AS promoted_bytes
  FROM db2.gcs
)

-- We need that outer query for the `ORDER BY` here
SELECT *
FROM gc_data
ORDER BY db