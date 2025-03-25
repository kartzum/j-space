CREATE TABLE IF NOT EXISTS property (group text,name text,date timestamp,value_string text,PRIMARY KEY((group,name),date)) WITH CLUSTERING ORDER BY (date DESC);
