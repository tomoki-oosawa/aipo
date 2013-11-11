-- 20130930
ALTER TABLE TURBINE_USER ADD MIGRATE_VERSION INTEGER NOT NULL DEFAULT 0;
UPDATE TURBINE_USER SET MIGRATE_VERSION = 0 ;
ALTER TABLE TURBINE_USER ALTER COLUMN TUTORIAL_FORBID TYPE VARCHAR (64);
-- 20130930

-- 20131111
-----------------------------------------------------------------------------
-- EIP_T_GPDB
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_GPDB
(
    GPDB_ID INTEGER NOT NULL,
    GPDB_NAME TEXT NOT NULL,
    MAIL_FLG VARCHAR (1) NOT NULL,
    CREATE_USER_ID INTEGER NOT NULL,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (GPDB_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_GPDB
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_GPDB_ITEM
(
    GPDB_ITEM_ID INTEGER NOT NULL,
    GPDB_ID INTEGER NOT NULL,
    GPDB_ITEM_NAME TEXT NOT NULL,
    TITLE_FLG VARCHAR (1) NOT NULL,
    REQUIRED_FLG VARCHAR (1) NOT NULL,
    TYPE VARCHAR (2) NOT NULL,
    GPDB_KUBUN_ID INTEGER,
    LIST_FLG VARCHAR (1) NOT NULL,
    DETAIL_FLG VARCHAR (1) NOT NULL,
    SIZE_COL INTEGER,
    SIZE_ROW INTEGER,
    LINE INTEGER,
    ORDER_NO INTEGER NOT NULL,
    DEFAULT_SORT_FLG VARCHAR (1) NOT NULL,
    ASC_DESC VARCHAR (4),
    CREATE_USER_ID INTEGER NOT NULL,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (GPDB_ITEM_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_GPDB_RECORD
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_GPDB_RECORD
(
    GPDB_RECORD_ID INTEGER NOT NULL,
    GPDB_ID INTEGER NOT NULL,
    GPDB_ITEM_ID INTEGER NOT NULL,
    RECORD_NO INTEGER NOT NULL,
    VALUE TEXT,
    CREATE_USER_ID INTEGER NOT NULL,
    UPDATE_USER_ID INTEGER NOT NULL,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (GPDB_RECORD_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_GPDB_RECORD_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_GPDB_RECORD_FILE
(
    FILE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    GPDB_RECORD_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH TEXT NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_GPDB_KUBUN
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_GPDB_KUBUN
(
    GPDB_KUBUN_ID INTEGER NOT NULL,
    GPDB_KUBUN_NAME TEXT NOT NULL,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (GPDB_KUBUN_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_GPDB_KUBUN_VALUE
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_GPDB_KUBUN_VALUE
(
    GPDB_KUBUN_VALUE_ID INTEGER NOT NULL,
    GPDB_KUBUN_ID INTEGER NOT NULL,
    GPDB_KUBUN_VALUE TEXT NOT NULL,
    ORDER_NO INTEGER NOT NULL,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (GPDB_KUBUN_VALUE_ID)
);

CREATE SEQUENCE pk_eip_t_gpdb INCREMENT 20;
CREATE SEQUENCE pk_eip_t_gpdb_item INCREMENT 20;
CREATE SEQUENCE pk_eip_t_gpdb_record INCREMENT 20;
CREATE SEQUENCE pk_eip_t_gpdb_record_file INCREMENT 20;
CREATE SEQUENCE pk_eip_m_gpdb_kubun INCREMENT 20;
CREATE SEQUENCE pk_eip_m_gpdb_kubun_value INCREMENT 20;

ALTER SEQUENCE pk_eip_t_gpdb OWNED BY EIP_T_GPDB.GPDB_ID;
ALTER SEQUENCE pk_eip_t_gpdb_item OWNED BY EIP_T_GPDB_ITEM.GPDB_ITEM_ID;
ALTER SEQUENCE pk_eip_t_gpdb_record OWNED BY EIP_T_GPDB_RECORD.GPDB_RECORD_ID;
ALTER SEQUENCE pk_eip_t_gpdb_record_file OWNED BY EIP_T_GPDB_RECORD_FILE.FILE_ID;
ALTER SEQUENCE pk_eip_m_gpdb_kubun OWNED BY EIP_M_GPDB_KUBUN.GPDB_KUBUN_ID;
ALTER SEQUENCE pk_eip_m_gpdb_kubun_value OWNED BY EIP_M_GPDB_KUBUN_VALUE.GPDB_KUBUN_VALUE_ID;
-- 20131111