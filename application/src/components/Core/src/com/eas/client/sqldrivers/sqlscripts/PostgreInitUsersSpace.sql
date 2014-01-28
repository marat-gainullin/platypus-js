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