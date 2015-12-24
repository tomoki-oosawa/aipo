--
-- Aipo is a groupware program developed by TOWN, Inc.
-- Copyright (C) 2004-2015 TOWN, Inc.
-- http://www.aipo.com
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

--20120214
-----------------------------------------------------------------------------
-- EIP_T_TIMELINE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TIMELINE
(
    TIMELINE_ID INTEGER NOT NULL,
    PARENT_ID INTEGER NOT NULL DEFAULT 0,
    OWNER_ID INTEGER,
    NOTE TEXT,
    CREATE_DATE TIMESTAMP DEFAULT now(),
    UPDATE_DATE TIMESTAMP DEFAULT now(),
    FOREIGN KEY (TIMELINE_ID) REFERENCES EIP_T_TIMELINE (TIMELINE_ID) ON DELETE CASCADE,
    PRIMARY KEY(TIMELINE_ID)
);

-----------------------------------------------------------------------------
-- CREATE SEQUENCE
-----------------------------------------------------------------------------

CREATE SEQUENCE pk_eip_t_timeline INCREMENT 20;

-----------------------------------------------------------------------------
-- ALTER SEQUENCE
-----------------------------------------------------------------------------

ALTER SEQUENCE pk_eip_t_timeline OWNED BY EIP_T_TIMELINE.TIMELINE_ID;
--20120214

--20120229
-----------------------------------------------------------------------------
-- EIP_T_TIMELINE_LIKE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TIMELINE_LIKE
(
    TIMELINE_LIKE_ID INTEGER NOT NULL,
    TIMELINE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    FOREIGN KEY (TIMELINE_LIKE_ID) REFERENCES EIP_T_TIMELINE_LIKE (TIMELINE_LIKE_ID) ON DELETE CASCADE,
    PRIMARY KEY(TIMELINE_LIKE_ID),
    UNIQUE (TIMELINE_ID, OWNER_ID)
);

-----------------------------------------------------------------------------
-- CREATE SEQUENCE
-----------------------------------------------------------------------------

CREATE SEQUENCE pk_eip_t_timeline_like INCREMENT 20;

-----------------------------------------------------------------------------
-- ALTER SEQUENCE
-----------------------------------------------------------------------------

ALTER SEQUENCE pk_eip_t_timeline_like OWNED BY EIP_T_TIMELINE_LIKE.TIMELINE_LIKE_ID;
--20120229

--20120307
-----------------------------------------------------------------------------
-- ALTER TABLE
-----------------------------------------------------------------------------
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD COLUMN START_DAY SMALLINT;
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET START_DAY=1;

-----------------------------------------------------------------------------
-- EIP_T_TIMELINE_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TIMELINE_FILE
(
    FILE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    TIMELINE_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH TEXT NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (TIMELINE_ID) REFERENCES EIP_T_TIMELINE (TIMELINE_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- CREATE SEQUENCE
-----------------------------------------------------------------------------

CREATE SEQUENCE pk_eip_t_timeline_file INCREMENT 20;

-----------------------------------------------------------------------------
-- ALTER SEQUENCE
-----------------------------------------------------------------------------

ALTER SEQUENCE pk_eip_t_timeline_file OWNED BY EIP_T_TIMELINE_FILE.FILE_ID;

--20120307

-- 20120314
UPDATE EIP_T_ACL_PORTLET_FEATURE SET FEATURE_ALIAS_NAME = 'アプリ配置' WHERE FEATURE_NAME = 'portlet_customize' AND FEATURE_ALIAS_NAME = 'ポートレット操作';
UPDATE EIP_T_ACL_ROLE SET ROLE_NAME = 'アプリ配置管理者' WHERE  FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'portlet_customize') AND ROLE_NAME = 'ポートレット管理者';
-- 20120314

-- 20120321
-----------------------------------------------------------------------------
-- EIP_T_TIMELINE_URL
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TIMELINE_URL
(
    URL_ID INTEGER NOT NULL,
    TIMELINE_ID INTEGER,
    THUMBNAIL VARCHAR (128),
    TITLE VARCHAR (128),
    URL VARCHAR (128) NOT NULL,
    BODY TEXT,
    FOREIGN KEY (TIMELINE_ID) REFERENCES EIP_T_TIMELINE (TIMELINE_ID) ON DELETE CASCADE,
    PRIMARY KEY (URL_ID)
);

-----------------------------------------------------------------------------
-- CREATE SEQUENCE
-----------------------------------------------------------------------------

CREATE SEQUENCE pk_eip_t_timeline_url INCREMENT 20;

-----------------------------------------------------------------------------
-- ALTER SEQUENCE
-----------------------------------------------------------------------------

ALTER SEQUENCE pk_eip_t_timeline_url OWNED BY EIP_T_TIMELINE_URL.URL_ID;
-- 20120321

-- 20120322

ALTER TABLE EIP_T_TIMELINE ADD COLUMN TIMELINE_TYPE VARCHAR (2);
ALTER TABLE EIP_T_TIMELINE ADD COLUMN PARAMS VARCHAR (99);

UPDATE EIP_T_TIMELINE SET TIMELINE_TYPE='T' WHERE (coalesce(TIMELINE_TYPE,'')='');
-- 20120322

-- 20120326

ALTER TABLE EIP_T_TIMELINE_URL DROP THUMBNAIL;
ALTER TABLE EIP_T_TIMELINE_URL ADD COLUMN THUMBNAIL bytea;

-- 20120326

-- 20120328

ALTER TABLE EIP_T_TIMELINE ADD COLUMN APP_ID VARCHAR (255);
ALTER TABLE EIP_T_TIMELINE ADD COLUMN EXTERNAL_ID VARCHAR (99);

ALTER TABLE EIP_T_TIMELINE_LIKE ADD COLUMN CREATE_DATE TIMESTAMP DEFAULT now();

-- 20120328

-- 20120406
ALTER TABLE EIP_T_TIMELINE ADD COLUMN NUM_ON_DAY INTEGER DEFAULT 0;
-- 20120406

-- 20120411
ALTER TABLE EIP_M_MAIL_ACCOUNT ALTER ACCOUNT_NAME TYPE VARCHAR (200);


CREATE TABLE EIP_T_ACL_MAP
(
  ACL_ID INTEGER NOT NULL,
  TARGET_ID INTEGER NOT NULL,
  TARGET_TYPE CHARACTER VARYING(8),
  ID INTEGER NOT NULL,
  TYPE VARCHAR(8),
  FEATURE VARCHAR(64),
  LEVEL INTEGER NOT NULL,
  PRIMARY KEY (ACL_ID)
);

CREATE SEQUENCE pk_eip_t_acl_map INCREMENT 20;
ALTER SEQUENCE pk_eip_t_acl_map OWNED BY EIP_T_ACL_MAP.ACL_ID;

-- 20120411

-- 20120418
CREATE INDEX parent_id ON eip_t_timeline (parent_id);
-- 20120418

-- 20120423
CREATE TABLE EIP_T_TIMELINE_MAP
(
    ID INTEGER NOT NULL,
    TIMELINE_ID INTEGER NULL,
    IS_READ INTEGER NULL,
    LOGIN_NAME varchar(32) NOT NULL,
    PRIMARY KEY(ID)
);
ALTER TABLE EIP_T_TIMELINE_MAP ADD FOREIGN KEY (TIMELINE_ID) REFERENCES EIP_T_TIMELINE (TIMELINE_ID) ON DELETE CASCADE;
CREATE SEQUENCE pk_eip_t_timeline_map INCREMENT 20 START 200;
-- 20120423

-- 20120518
ALTER TABLE EIP_T_TIMELINE_URL ALTER COLUMN URL TYPE TEXT;
CREATE INDEX eip_m_config_name ON eip_m_config (NAME);
CREATE INDEX container_config_name ON container_config (NAME);
-- 20120518

-- 20120524
ALTER TABLE TURBINE_USER ADD COLUMN HAS_PHOTO VARCHAR (1) DEFAULT 'F';
ALTER TABLE TURBINE_USER ADD COLUMN PHOTO_MODIFIED TIMESTAMP;
UPDATE TURBINE_USER SET PHOTO_MODIFIED = NOW();
UPDATE TURBINE_USER SET HAS_PHOTO = 'T' WHERE PHOTO IS NOT NULL;
-- 20120524
