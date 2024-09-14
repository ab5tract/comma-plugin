say $foo;
my $results = $db.query(q:to/STATEMENT/, $user, $dataset);
    SELECT data_var, data_val, data_instance,
           branches_data, branches_options,
           data_comment
    FROM data_new LEFT JOIN branches ON (data_id=branches_var)
    WHERE data_dataset=dataset_name2id($1,$2)
        AND data_var not like '%ignore'
    ORDER BY data_instance, branches_data, data_var, data_val
STATEMENT

