-- CREATE TABLE gcs(time INT, retained_bytes INT, promoted_bytes INT, gen2_roots INT, stolen_gen2_roots INT, full INT, responsible INT, cleared_bytes INT, start_time INT, sequence_num INT, thread_id INT, PRIMARY KEY(sequence_num, thread_id));

-- XXX I wanted to avoid creating a "all_gcs" table to gain some time

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

SELECT *
FROM gc_data
ORDER BY db