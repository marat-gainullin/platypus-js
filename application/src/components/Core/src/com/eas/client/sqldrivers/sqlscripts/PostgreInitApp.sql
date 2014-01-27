CREATE TABLE mtd_entities
(
  mdent_id character varying(200) NOT NULL,
  mdent_name character varying(200),
  mdent_type numeric NOT NULL,
  mdent_content_txt text,
  mdent_parent_id character varying(200),
  mdent_order double precision,
  mdent_content_txt_size numeric,
  mdent_content_txt_crc32 numeric,
  CONSTRAINT mtd_entities_pk PRIMARY KEY (mdent_id),
  CONSTRAINT mtd_entities_fk61240574157171 FOREIGN KEY (mdent_parent_id)
      REFERENCES mtd_entities (mdent_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT mtd_names_unique UNIQUE (mdent_parent_id, mdent_name)
)
#GO
CREATE OR REPLACE VIEW dummytable AS SELECT character varying(100) '' as dummy 
#GO
