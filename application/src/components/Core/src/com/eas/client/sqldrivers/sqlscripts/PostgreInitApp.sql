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
CREATE TABLE MTD_VERSION
(
    VERSION_VALUE numeric NOT NULL,
    CONSTRAINT mtd_version_pk PRIMARY KEY(VERSION_VALUE)
)
#GO
INSERT INTO MTD_VERSION (VERSION_VALUE) VALUES (0)
#GO
CREATE OR REPLACE VIEW dummytable AS SELECT character varying(100) '' as dummy 
#GO

CREATE TABLE mtd_users
(
  usr_name character varying(200) NOT NULL,
  usr_passwd character varying(200),
  usr_form character varying(200),
  usr_context character varying(200),
  usr_roles character varying(200),
  usr_phone character varying(200),
  usr_email character varying(200),
  CONSTRAINT mtd_users_pk PRIMARY KEY (usr_name)
)
#GO
CREATE TABLE mtd_groups
(
    usr_name VARCHAR(200) NOT NULL,
    group_name VARCHAR(200) NOT NULL
)
#GO
ALTER TABLE mtd_groups
    ADD CONSTRAINT MTD_GROUPS_USERS_FK FOREIGN KEY(USR_NAME) REFERENCES MTD_USERS(USR_NAME) ON DELETE CASCADE ON UPDATE CASCADE 
#GO
insert into mtd_users (USR_NAME, USR_PASSWD)
    VALUES ('admin', 'abe6db4c9f5484fae8d79f2e868a673c')
#GO
insert into mtd_groups (USR_NAME, GROUP_NAME)
    VALUES ('admin', 'admin')
#GO