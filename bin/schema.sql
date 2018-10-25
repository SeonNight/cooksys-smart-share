-- SCHEMA: smartshare

-- DROP SCHEMA smartshare ;

CREATE SCHEMA smartshare
    AUTHORIZATION postgres;

-- Table: smartshare.files

-- DROP TABLE smartshare.files;

CREATE TABLE smartshare.files
(
    file_name text COLLATE pg_catalog."default" NOT NULL,
    id serial,
    file bytea,
    time_created timestamp without time zone NOT NULL,
    expiry_time timestamp without time zone NOT NULL,
    max_downloads integer NOT NULL,
    total_downloads integer NOT NULL,
    password text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT files_pkey PRIMARY KEY (id),
    CONSTRAINT file_name_key UNIQUE (file_name)
)
WITH (
    OIDS = FALSE
)

TABLESPACE pg_default;
GRANT USAGE ON SEQUENCE smartshare.files_id_seq TO postgres;

ALTER TABLE smartshare.files
    OWNER to postgres;
