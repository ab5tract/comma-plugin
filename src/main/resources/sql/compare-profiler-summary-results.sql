-- CREATE TABLE types(id INTEGER PRIMARY KEY ASC, name TEXT, extra_info JSON, type_links JSON);
-- CREATE TABLE gcs(time INT, retained_bytes INT, promoted_bytes INT, gen2_roots INT, stolen_gen2_roots INT, full INT, responsible INT, cleared_bytes INT, start_time INT, sequence_num INT, thread_id INT, PRIMARY KEY(sequence_num, thread_id));
-- CREATE TABLE profile(total_time INT, spesh_time INT, thread_id INT, parent_thread_id INT, root_node INT, first_entry_time INT, FOREIGN KEY(root_node) REFERENCES calls(id));
-- CREATE TABLE allocations(call_id INT, type_id INT, spesh INT, jit INT, count INT, replaced INT, PRIMARY KEY(call_id, type_id), FOREIGN KEY(call_id) REFERENCES calls(id), FOREIGN KEY(type_id) REFERENCES types(id));
-- CREATE TABLE deallocations(gc_seq_num INT, gc_thread_id INT, type_id INT, nursery_fresh INT, nursery_seen INT, gen2 INT, PRIMARY KEY(gc_seq_num, gc_thread_id, type_id), FOREIGN KEY(gc_seq_num, gc_thread_id) REFERENCES gcs(sequence_num, thread_id), FOREIGN KEY(type_id) REFERENCES types(id));

-- Group the GC entries by their sequence number and compute some statistics
WITH
gc_sequences AS (
  SELECT sequence_num, full, time,
         min(start_time) AS earliest,
         max(start_time + time) AS latest
  FROM %DB%.gcs
  GROUP BY 1
),

-- Compute GC sequence statistics
gc_summary AS (
  SELECT sum(time) AS gc_total_time,
         count(*) AS gc_count,
         count(*) filter (where full = 1) AS gc_full_count,
         avg(latest - earliest) filter (where full = 0) AS gc_avg_minor_time,
         min(latest - earliest) filter (where full = 0) AS gc_min_minor_time,
         max(latest - earliest) filter (where full = 0) AS gc_max_minor_time,
         avg(latest - earliest) filter (where full = 1) AS gc_avg_major_time,
         min(latest - earliest) filter (where full = 1) AS gc_min_major_time,
         max(latest - earliest) filter (where full = 1) AS gc_max_major_time
         --, sum()
  FROM gc_sequences
),

-- Compute profile statistics
profile_summary AS (
  SELECT sum(total_time)/1000.0 AS profile_total_time,
         sum(spesh_time)/1000.0 AS profile_total_spesh_time,
         max(spesh_time)/1000.0 AS profile_highest_spesh_time,
         max(first_entry_time + total_time)/1000.0 AS profile_run_time
  FROM %DB%.profile
),

-- Compute call statistics
calls_summary AS (
  SELECT sum(entries) as calls_entries,
         sum(spesh_entries) as calls_spesh_entries,
         sum(jit_entries) as calls_jit_entries,
         sum(inlined_entries) as calls_inlined_entries,
         sum(deopt_one) as calls_deopt_one,
         sum(deopt_all) as calls_deopt_all
  FROM %DB%.calls
),

-- Compute allocation statistics
allocations_summary AS (
  SELECT sum(jit + spesh + count) as allocations_total,
         sum(replaced) as allocations_replaced
  FROM %DB%.allocations
)

-- Aggregate all statistics
SELECT gc.*, profile.*, calls.*, allocations.*
FROM gc_summary gc
LEFT JOIN profile_summary profile ON 1
LEFT JOIN calls_summary calls ON 1
LEFT JOIN allocations_summary allocations ON 1