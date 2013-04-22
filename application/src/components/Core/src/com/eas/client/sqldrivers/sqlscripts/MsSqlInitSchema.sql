CREATE TABLE MTD_ENTITIES(
	MDENT_ID varchar(200) NOT NULL,
	MDENT_NAME varchar(200) NULL,
	MDENT_TYPE numeric(18, 0) NOT NULL,
	MDENT_CONTENT_TXT text NULL,
	MDENT_PARENT_ID varchar(200) NULL,
        MDENT_CONTENT_TXT_SIZE   numeric(18, 0) NULL,
        MDENT_CONTENT_TXT_CRC32  numeric(18, 0) NULL,
	MDENT_ORDER float NULL,
	MDENT_ORDER_SEQUENCE numeric(18, 0) IDENTITY(100000,100000) NOT NULL,
    CONSTRAINT MTD_ENTITIES_PK PRIMARY KEY(MDENT_ID ASC),
    CONSTRAINT MTD_ENTITIES_FK FOREIGN KEY(MDENT_PARENT_ID) REFERENCES MTD_ENTITIES(MDENT_ID) ON DELETE NO ACTION
)
#GO
CREATE TABLE MTD_USERS
   (
    USR_NAME VARCHAR(200) NOT NULL,
    USR_PASSWD VARCHAR(200),
    USR_FORM   VARCHAR(200) NULL,
    USR_CONTEXT VARCHAR(200),
    USR_ROLES VARCHAR(200),
    USR_PHONE VARCHAR(200),
    USR_EMAIL VARCHAR(200),
    CONSTRAINT MTD_USERS_PK PRIMARY KEY(USR_NAME)
   )
#GO
CREATE TABLE MTD_GROUPS
    (
        USR_NAME VARCHAR(200) NOT NULL,
        GROUP_NAME VARCHAR(200)
    )
#GO
ALTER TABLE MTD_GROUPS
    ADD CONSTRAINT MTD_GROUPS_USERS_FK FOREIGN KEY(USR_NAME) REFERENCES MTD_USERS(USR_NAME) ON DELETE CASCADE ON UPDATE CASCADE 
#GO
CREATE TABLE MTD_VERSION
   (
    VERSION_VALUE numeric(18, 0) NOT NULL,
    CONSTRAINT MTD_VERSION_PK PRIMARY KEY(VERSION_VALUE)
    )
#GO
INSERT INTO MTD_VERSION (VERSION_VALUE) VALUES (0)
#GO
CREATE TABLE DUMMYTABLE
   (
    DUMMY NUMERIC
   )
#GO
insert into MTD_USERS (USR_NAME, USR_PASSWD)
    VALUES ('admin', 'abe6db4c9f5484fae8d79f2e868a673c')
#GO
CREATE FUNCTION buildMtdEntitiesParents
(
	@aElement numeric(18,0)
)
RETURNS @result TABLE (mdent_id numeric(18, 0), mdent_parent_id numeric(18, 0))
AS
begin
	declare @currParent numeric(18,0)
	set @currParent = @aElement
	while(not @currParent is null)
	begin
	  insert into @result select mdent_id, mdent_parent_id from mtd_entities where mtd_entities.mdent_id = @currParent
	  select @currParent = mdent_parent_id from mtd_entities where mtd_entities.mdent_id = @currParent
	end
	return
end
#GO
CREATE FUNCTION buildMtdEntitiesChildrenList
(
	@parentId numeric(18, 0)
)
RETURNS @result TABLE (mdent_id numeric(18, 0), mdent_parent_id numeric(18, 0))
AS
begin
	declare @parents table (mdent_id numeric(18, 0), mdent_parent_id numeric(18, 0));
	declare @level table (mdent_id numeric(18, 0), mdent_parent_id numeric(18, 0));
	declare @childrenCount numeric(18,0)

	/*roots*/
	if(@parentId is null)
		insert into @result select mdent_id, mdent_parent_id from mtd_entities where mdent_parent_id is null
	else
		insert into @result select mdent_id, mdent_parent_id from mtd_entities where mdent_id = @parentId

    select @childrenCount = count(*) from @result;
	insert into @parents select * from @result;

	/*children*/
	while (@childrenCount > 0)
	begin
		insert into @level select mdent_id, mdent_parent_id from mtd_entities where mdent_parent_id in (select mdent_id from @parents)
		select @childrenCount = count(*) from @level;
		insert into @result select * from @level;
		delete from @parents
		insert into @parents select * from @level
		delete from @level
	end

    return
end
#GO