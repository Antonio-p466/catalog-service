UPDATE book SET created_by = 'unknown' WHERE created_by IS NULL;
UPDATE book SET last_modified_by = 'unknown' WHERE last_modified_by IS NULL;
