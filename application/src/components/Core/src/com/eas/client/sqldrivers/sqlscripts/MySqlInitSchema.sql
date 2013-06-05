CREATE TABLE MTD_ENTITIES
(
    MDENT_ID VARCHAR(200) NOT NULL,
    MDENT_NAME VARCHAR(200),
    MDENT_TYPE DECIMAL(18,0) NOT NULL,
    MDENT_CONTENT_TXT LONGTEXT,
    MDENT_PARENT_ID VARCHAR(200),
    MDENT_ORDER DOUBLE,
    MDENT_CONTENT_TXT_SIZE   DECIMAL(18,0),
    MDENT_CONTENT_TXT_CRC32  DECIMAL(18,0),
    CONSTRAINT PRIMARY KEY(MDENT_ID),
    CONSTRAINT MTD_ENTITIES_FK61240574157171 FOREIGN KEY(MDENT_PARENT_ID) REFERENCES MTD_ENTITIES(MDENT_ID) ON DELETE CASCADE ON UPDATE CASCADE 
)
#GO
ALTER TABLE MTD_ENTITIES
    ADD (CONSTRAINT MTD_NAMES_UNIQUE UNIQUE(MDENT_PARENT_ID, MDENT_NAME))
#GO
CREATE TABLE MTD_USERS
(
    USR_NAME VARCHAR(200) NOT NULL,
    USR_PASSWD VARCHAR(200),
    USR_FORM   VARCHAR(200),
    USR_CONTEXT VARCHAR(200),
    USR_ROLES VARCHAR(200),
    USR_PHONE VARCHAR(200),
    USR_EMAIL VARCHAR(200),
    CONSTRAINT PRIMARY KEY(USR_NAME)
)
#GO
CREATE TABLE MTD_GROUPS
(
    USR_NAME VARCHAR(200) NOT NULL,
    GROUP_NAME VARCHAR(200) NOT NULL
)
#GO
ALTER TABLE MTD_GROUPS
    ADD CONSTRAINT MTD_GROUPS_USERS_FK FOREIGN KEY(USR_NAME) REFERENCES MTD_USERS(USR_NAME) ON DELETE CASCADE ON UPDATE CASCADE 
#GO
CREATE TABLE MTD_VERSION
(
    VERSION_VALUE DECIMAL(18, 0) NOT NULL,
    CONSTRAINT MTD_VERSION_PK PRIMARY KEY(VERSION_VALUE)
)
#GO
INSERT INTO MTD_VERSION (VERSION_VALUE) VALUES (0)
#GO
CREATE TABLE DUMMYTABLE
(
    DUMMY DECIMAL(18, 0)
)
#GO
insert into MTD_USERS (USR_NAME, USR_PASSWD)
    VALUES ('admin', 'abe6db4c9f5484fae8d79f2e868a673c')
#GO
insert into MTD_GROUPS (USR_NAME, GROUP_NAME)
    VALUES ('admin', 'admin')
#GO
CREATE PROCEDURE fetch_children(
    IN name_table VARCHAR(64),
    IN name_outputfields VARCHAR(300),
    IN name_id VARCHAR(64),
    IN name_parent VARCHAR(64),
    IN base INT UNSIGNED,
    IN max_levels INT,
    IN result_in_var BOOLEAN,
    OUT result_ids MEDIUMTEXT
)
BEGIN
    DECLARE ids MEDIUMTEXT DEFAULT '';
    DECLARE currlevel INT DEFAULT 0;

    SET @parents = base;

    IF result_in_var THEN
        SET result_ids = '';
    END IF;

    SET currlevel = IF(max_levels, 1, -100000);

    REPEAT
        IF result_in_var THEN
            SET result_ids = CONCAT(result_ids, IF(LENGTH(result_ids), ',', ''), @parents);
        ELSE
            SET ids = CONCAT(ids, IF(LENGTH(ids), ',', ''), @parents);
        END IF;

        SET @stm = CONCAT(
            'SELECT GROUP_CONCAT(', name_id, ') INTO @parents FROM ', name_table,
            ' WHERE ', name_parent, ' IN (', @parents, ')'
        );

        PREPARE fetch_childs FROM @stm;
        EXECUTE fetch_childs;
        DROP PREPARE fetch_childs;

        SET currlevel = currlevel + 1;
    UNTIL (@parents IS NULL OR currlevel > max_levels) END REPEAT;

    IF NOT result_in_var THEN
        SET @stm := CONCAT(
            'SELECT ', name_outputfields, ' FROM ', name_table,
            ' WHERE ', name_id, ' IN (', ids, ')'
        );

        PREPARE fetch_childs FROM @stm;
        EXECUTE fetch_childs;
        DROP PREPARE fetch_childs;
    END IF;
END;
#GO
CREATE PROCEDURE fetch_parent(
    IN name_table VARCHAR(64),
    IN name_outputfields VARCHAR(300),
    IN name_id VARCHAR(64),
    IN name_parent VARCHAR(64),
    IN base INT UNSIGNED,
    IN max_levels INT,
    IN result_in_var BOOLEAN,
    OUT result_ids MEDIUMTEXT
)
BEGIN
    DECLARE currlevel INT;
    DECLARE ids MEDIUMTEXT DEFAULT '';

    IF result_in_var THEN
        SET result_ids = '';
    END IF;

    SET @parent = base;

    SET currlevel = IF(max_levels, 1, -100000);

    set @flag_exit = false;
    set @last_res = base;

    SET @stm = CONCAT(
        'SELECT IFNULL(', name_parent, ',',name_id,') INTO @parent FROM ', name_table, ' WHERE ', name_id, ' = ?'
    );
    PREPARE fetch_parent FROM @stm;

    REPEAT
        IF result_in_var THEN
            SET result_ids = CONCAT(result_ids, IF(LENGTH(result_ids), ',', ''), @parent);
        ELSE
            SET ids = CONCAT(ids, IF(LENGTH(ids), ',', ''), @parent);
        END IF;

        EXECUTE fetch_parent USING @parent;

        if @parent = @last_res then
           set @flag_exit = true;
        end if;

        set @last_res = @parent;

        SET currlevel = currlevel + 1;
   UNTIL (@flag_exit OR NOT @parent OR currlevel > max_levels) END REPEAT;

    DROP PREPARE fetch_parent;

    IF NOT result_in_var THEN
        SET @stm = CONCAT(
            'SELECT ',name_outputfields, ' FROM ', name_table,
            ' WHERE ', name_id, ' IN (', ids, ')'
        );

        PREPARE fetch_parents FROM @stm;
        EXECUTE fetch_parents;
        DROP PREPARE fetch_parents;
    END IF;
END
#GO
