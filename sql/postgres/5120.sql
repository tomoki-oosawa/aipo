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

-----------------------------------------------------------------------------
-- AIPO_LICENSE
-----------------------------------------------------------------------------

CREATE TABLE AIPO_LICENSE
(
   LICENSE_ID serial,
   LICENSE VARCHAR (99) NOT NULL,
   LIMIT_USERS INTEGER,
   PRIMARY KEY(LICENSE_ID)
);

-----------------------------------------------------------------------------
-- TURBINE_PERMISSION
-----------------------------------------------------------------------------

CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID serial,
    PERMISSION_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA bytea,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);

-----------------------------------------------------------------------------
-- TURBINE_ROLE
-----------------------------------------------------------------------------

CREATE TABLE TURBINE_ROLE
(
    ROLE_ID serial,
    ROLE_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA bytea,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);

-----------------------------------------------------------------------------
-- TURBINE_GROUP
-----------------------------------------------------------------------------

CREATE TABLE TURBINE_GROUP
(
    GROUP_ID serial,
    GROUP_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA bytea,
    OWNER_ID INTEGER,
    GROUP_ALIAS_NAME VARCHAR (99),
    PUBLIC_FLAG CHAR,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME),
    UNIQUE (OWNER_ID, GROUP_ALIAS_NAME)
);

-----------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-----------------------------------------------------------------------------

CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES TURBINE_ROLE (ROLE_ID),
    FOREIGN KEY (PERMISSION_ID) REFERENCES TURBINE_PERMISSION (PERMISSION_ID)
);

CREATE UNIQUE INDEX ROLE_PERMISSION_INDEX
  ON TURBINE_ROLE_PERMISSION(ROLE_ID,PERMISSION_ID);

-----------------------------------------------------------------------------
-- TURBINE_USER
-----------------------------------------------------------------------------

CREATE TABLE TURBINE_USER
(
    USER_ID serial,
    LOGIN_NAME VARCHAR (32) NOT NULL,
    PASSWORD_VALUE VARCHAR (200) NOT NULL,
    FIRST_NAME VARCHAR (99) NOT NULL,
    LAST_NAME VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99),
    CONFIRM_VALUE VARCHAR (99),
    MODIFIED TIMESTAMP,
    CREATED TIMESTAMP,
    LAST_LOGIN TIMESTAMP,
    DISABLED CHAR,
    OBJECTDATA bytea,
    PASSWORD_CHANGED TIMESTAMP,
    COMPANY_ID INTEGER,
    POSITION_ID INTEGER,
    IN_TELEPHONE VARCHAR (15),
    OUT_TELEPHONE VARCHAR (15),
    CELLULAR_PHONE VARCHAR (15),
    CELLULAR_MAIL VARCHAR (99),
    CELLULAR_UID VARCHAR (99),
    FIRST_NAME_KANA VARCHAR (99),
    LAST_NAME_KANA VARCHAR(99),
    PHOTO bytea,
    CREATED_USER_ID INTEGER,
    UPDATED_USER_ID INTEGER,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);

-----------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-----------------------------------------------------------------------------

CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    ID serial,
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY(ID),
    FOREIGN KEY (USER_ID) REFERENCES TURBINE_USER (USER_ID),
    FOREIGN KEY (GROUP_ID) REFERENCES TURBINE_GROUP (GROUP_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES TURBINE_ROLE (ROLE_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_COMPANY
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_COMPANY
(
    COMPANY_ID serial,
    COMPANY_NAME VARCHAR (64) NOT NULL,
    COMPANY_NAME_KANA VARCHAR (64),
    ZIPCODE VARCHAR (8),
    ADDRESS VARCHAR (64),
    TELEPHONE VARCHAR (15),
    FAX_NUMBER VARCHAR (15),
    URL VARCHAR (99),
    IPADDRESS VARCHAR (99),
    PORT INTEGER,
    IPADDRESS_INTERNAL VARCHAR (99),
    PORT_INTERNAL INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(COMPANY_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_MYBOX
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_MYBOX
(
    MYBOX_ID serial,
    COMPANY_ID INTEGER NOT NULL,
    AIPO_ID VARCHAR (99),
    AIPO_PASSWD VARCHAR (99),
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(MYBOX_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_POST
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_POST
(
    POST_ID serial,
    COMPANY_ID INTEGER NOT NULL,
    POST_NAME VARCHAR (64) NOT NULL,
    ZIPCODE VARCHAR (8) ,
    ADDRESS VARCHAR (64),
    IN_TELEPHONE VARCHAR (15),
    OUT_TELEPHONE VARCHAR (15),
    FAX_NUMBER VARCHAR (15),
    GROUP_NAME VARCHAR (99),
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(POST_ID),
    UNIQUE (GROUP_NAME)
);


-----------------------------------------------------------------------------
-- EIP_M_POSITION
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_POSITION
(
    POSITION_ID serial,
    POSITION_NAME VARCHAR (64) NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(POSITION_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_USER_POSITION
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_USER_POSITION
(
    ID serial,
    USER_ID INTEGER,
    POSITION INTEGER,
    PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_COMMON_CATEGORY
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_COMMON_CATEGORY
(
  COMMON_CATEGORY_ID serial,
  NAME VARCHAR (64) NOT NULL,
  NOTE VARCHAR,
  CREATE_USER_ID INTEGER NOT NULL,
  UPDATE_USER_ID INTEGER NOT NULL,
  CREATE_DATE DATE,
  UPDATE_DATE TIMESTAMP,
  PRIMARY KEY(COMMON_CATEGORY_ID)
);

INSERT INTO EIP_T_COMMON_CATEGORY VALUES(1,'未分類','',0,0,NULL,NULL);
SELECT setval('eip_t_common_category_common_category_id_seq',1);

-----------------------------------------------------------------------------
-- EIP_T_SCHEDULE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_SCHEDULE
(
    SCHEDULE_ID serial,
    PARENT_ID INTEGER,
    OWNER_ID INTEGER,
    REPEAT_PATTERN VARCHAR (10),
    START_DATE TIMESTAMP,
    END_DATE TIMESTAMP,
    NAME VARCHAR (99),
    PLACE VARCHAR (99),
    NOTE VARCHAR,
    PUBLIC_FLAG VARCHAR (1),
    EDIT_FLAG VARCHAR (1),
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(SCHEDULE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_SCHEDULE_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_SCHEDULE_MAP
(
   ID serial,
   SCHEDULE_ID INTEGER NOT NULL,
   USER_ID INTEGER NOT NULL,
   STATUS VARCHAR (1),
   TYPE VARCHAR (1),
   COMMON_CATEGORY_ID INTEGER NOT NULL,
   FOREIGN KEY (SCHEDULE_ID) REFERENCES EIP_T_SCHEDULE (SCHEDULE_ID) ON DELETE CASCADE,
   FOREIGN KEY (COMMON_CATEGORY_ID) REFERENCES EIP_T_COMMON_CATEGORY (COMMON_CATEGORY_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_CATEGORY
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TODO_CATEGORY
(
    CATEGORY_ID serial,
    USER_ID INTEGER NOT NULL,
    CATEGORY_NAME VARCHAR (64) NOT NULL,
    NOTE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(CATEGORY_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_TODO
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TODO
(
    TODO_ID serial,
    USER_ID INTEGER NOT NULL,
    TODO_NAME VARCHAR (64) NOT NULL,
    CATEGORY_ID INTEGER,
    PRIORITY smallint,
    STATE smallint,
    NOTE VARCHAR,
    START_DATE DATE,
    END_DATE DATE,
    PUBLIC_FLAG VARCHAR (1) NOT NULL,
    ADDON_SCHEDULE_FLG VARCHAR (1) NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (CATEGORY_ID) REFERENCES EIP_T_TODO_CATEGORY (CATEGORY_ID) ON DELETE CASCADE,
    PRIMARY KEY(TODO_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_MAIL_ACCOUNT
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_MAIL_ACCOUNT
(
    ACCOUNT_ID serial,
    USER_ID INTEGER NOT NULL,
    ACCOUNT_NAME VARCHAR (20) NOT NULL,
    ACCOUNT_TYPE VARCHAR (1),
    SMTPSERVER_NAME VARCHAR (64) NOT NULL,
    POP3SERVER_NAME VARCHAR (64) NOT NULL,
    POP3USER_NAME VARCHAR (64) NOT NULL,
    POP3PASSWORD bytea NOT NULL,
    MAIL_USER_NAME VARCHAR (64),
    MAIL_ADDRESS VARCHAR (64) NOT NULL,
    SMTP_PORT VARCHAR (5) NOT NULL,
    SMTP_ENCRYPTION_FLG smallint,
    POP3_PORT VARCHAR (5) NOT NULL,
    POP3_ENCRYPTION_FLG smallint,
    AUTH_SEND_FLG smallint,
    AUTH_SEND_USER_ID VARCHAR (64),
    AUTH_SEND_USER_PASSWD bytea,
    AUTH_RECEIVE_FLG smallint,
    DEL_AT_POP3_FLG VARCHAR (1),
    DEL_AT_POP3_BEFORE_DAYS_FLG VARCHAR (1),
    DEL_AT_POP3_BEFORE_DAYS INTEGER,
    NON_RECEIVED_FLG VARCHAR (1),
    DEFAULT_FOLDER_ID INTEGER,
    LAST_RECEIVED_DATE TIMESTAMP,
    SIGNATURE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(ACCOUNT_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_MAIL_NOTIFY_CONF
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_MAIL_NOTIFY_CONF
(
    NOTIFY_ID serial,
    USER_ID INTEGER NOT NULL,
    NOTIFY_TYPE INTEGER NOT NULL,
    NOTIFY_FLG INTEGER NOT NULL,
    NOTIFY_TIME TIME,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(NOTIFY_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_MAIL
-----------------------------------------------------------------------------
CREATE TABLE EIP_T_MAIL
(
    MAIL_ID serial,
    USER_ID INTEGER NOT NULL,
    ACCOUNT_ID INTEGER NOT NULL,
    FOLDER_ID INTEGER NOT NULL,
    TYPE char (1),
    READ_FLG char (1),
    SUBJECT varchar,
    PERSON varchar,
    EVENT_DATE TIMESTAMP,
    FILE_VOLUME INTEGER,
    HAS_FILES char (1),
    FILE_PATH varchar,
    MAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (MAIL_ID)
);

CREATE INDEX eip_t_mail_user_id_index ON EIP_T_MAIL (USER_ID);

-----------------------------------------------------------------------------
-- EIP_T_MAIL_FOLDER
-----------------------------------------------------------------------------
CREATE TABLE EIP_T_MAIL_FOLDER
(
    FOLDER_ID serial,
    ACCOUNT_ID INTEGER,
    FOLDER_NAME varchar,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (FOLDER_ID)
);


-----------------------------------------------------------------------------
-- EIP_T_MAIL_FILTER
-----------------------------------------------------------------------------
CREATE TABLE EIP_T_MAIL_FILTER
(
    FILTER_ID serial,
    ACCOUNT_ID INTEGER,
    DST_FOLDER_ID INTEGER,
    FILTER_NAME varchar,
    FILTER_STRING varchar,
    FILTER_TYPE char(1),
    SORT_ORDER INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (FILTER_ID)
);


-----------------------------------------------------------------------------
-- EIP_M_ADDRESSBOOK
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_ADDRESSBOOK
(
    ADDRESS_ID serial,
    OWNER_ID INTEGER,
    FIRST_NAME VARCHAR(99),
    LAST_NAME VARCHAR(99),
    FIRST_NAME_KANA VARCHAR(99),
    LAST_NAME_KANA VARCHAR(99),
    EMAIL VARCHAR(99),
    TELEPHONE VARCHAR(15),
    CELLULAR_PHONE VARCHAR(15),
    CELLULAR_MAIL VARCHAR(99),
    COMPANY_ID INTEGER,
    POSITION_NAME VARCHAR(64),
    PUBLIC_FLAG VARCHAR(1),
    NOTE VARCHAR,
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(ADDRESS_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_ADDRESS_GROUP
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_ADDRESS_GROUP
(
    GROUP_ID serial,
    GROUP_NAME VARCHAR(99) NOT NULL,
    OWNER_ID INTEGER,
    PUBLIC_FLAG VARCHAR(1),
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(GROUP_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_ADDRESSBOOK_COMPANY
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_ADDRESSBOOK_COMPANY
(
    COMPANY_ID serial,
    COMPANY_NAME VARCHAR(64) NOT NULL,
    COMPANY_NAME_KANA VARCHAR(64),
    POST_NAME VARCHAR(64),
    ZIPCODE VARCHAR(8),
    ADDRESS VARCHAR(64),
    TELEPHONE VARCHAR(15),
    FAX_NUMBER VARCHAR(15),
    URL VARCHAR(99),
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(COMPANY_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_ADDRESSBOOK_GROUP_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_ADDRESSBOOK_GROUP_MAP
(
    ID serial,
    ADDRESS_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_NOTE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_NOTE
(
    NOTE_ID serial,
    OWNER_ID VARCHAR (99),
    CLIENT_NAME VARCHAR (99),
    COMPANY_NAME VARCHAR (99),
    TELEPHONE VARCHAR (24),
    EMAIL_ADDRESS VARCHAR (99),
    ADD_DEST_TYPE VARCHAR (1),
    SUBJECT_TYPE VARCHAR (1),
    CUSTOM_SUBJECT VARCHAR (99),
    MESSAGE VARCHAR,
    ACCEPT_DATE TIMESTAMP,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(NOTE_ID)
);


-----------------------------------------------------------------------------
-- EIP_T_NOTE_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_NOTE_MAP
(
   ID serial,
   NOTE_ID INTEGER NOT NULL,
   USER_ID VARCHAR (99) NOT NULL,
   DEL_FLG VARCHAR (1),
   NOTE_STAT VARCHAR (1),
   CONFIRM_DATE TIMESTAMP,
   FOREIGN KEY (NOTE_ID) REFERENCES EIP_T_NOTE (NOTE_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_MSGBOARD_CATEGORY
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MSGBOARD_CATEGORY
(
    CATEGORY_ID serial,
    OWNER_ID INTEGER,
    CATEGORY_NAME VARCHAR (99),
    NOTE VARCHAR,
    PUBLIC_FLAG VARCHAR (1),
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(CATEGORY_ID)
);


-----------------------------------------------------------------------------
-- EIP_T_MSGBOARD_CATEGORY_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MSGBOARD_CATEGORY_MAP
(
   ID serial,
   CATEGORY_ID INTEGER,
   USER_ID INTEGER,
   STATUS VARCHAR (1),
   FOREIGN KEY (CATEGORY_ID) REFERENCES EIP_T_MSGBOARD_CATEGORY (CATEGORY_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_MSGBOARD_TOPIC
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MSGBOARD_TOPIC
(
    TOPIC_ID serial,
    PARENT_ID INTEGER,
    OWNER_ID INTEGER,
    TOPIC_NAME VARCHAR (64) NOT NULL,
    NOTE VARCHAR,
    CATEGORY_ID INTEGER,
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (CATEGORY_ID) REFERENCES EIP_T_MSGBOARD_CATEGORY (CATEGORY_ID) ON DELETE CASCADE,
    PRIMARY KEY(TOPIC_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_MSGBOARD_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MSGBOARD_FILE
(
    FILE_ID serial,
    OWNER_ID INTEGER,
    TOPIC_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH varchar NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (TOPIC_ID) REFERENCES EIP_T_MSGBOARD_TOPIC (TOPIC_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_BLOG
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_BLOG
(
    BLOG_ID serial,
    OWNER_ID INTEGER NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (BLOG_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_BLOG_THEMA
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_BLOG_THEMA
(
    THEMA_ID serial,
    THEMA_NAME varchar (64) NOT NULL,
    DESCRIPTION VARCHAR,
    CREATE_USER_ID INTEGER NOT NULL,
    UPDATE_USER_ID INTEGER NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (THEMA_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_BLOG_ENTRY
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_BLOG_ENTRY
(
    ENTRY_ID serial,
    OWNER_ID INTEGER NOT NULL,
    TITLE varchar (99) NOT NULL,
    NOTE VARCHAR,
    BLOG_ID INTEGER NOT NULL,
    THEMA_ID INTEGER,
    ALLOW_COMMENTS varchar (1),
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (BLOG_ID) REFERENCES EIP_T_BLOG (BLOG_ID) ON DELETE CASCADE,
    FOREIGN KEY (THEMA_ID) REFERENCES EIP_T_BLOG_THEMA (THEMA_ID),
    PRIMARY KEY (ENTRY_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_BLOG_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_BLOG_FILE
(
    FILE_ID serial,
    OWNER_ID INTEGER NOT NULL,
    TITLE varchar (99) NOT NULL,
    FILE_PATH varchar NOT NULL,
    FILE_THUMBNAIL bytea,
    ENTRY_ID INTEGER NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (ENTRY_ID) REFERENCES EIP_T_BLOG_ENTRY (ENTRY_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_BLOG_COMMENT
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_BLOG_COMMENT
(
    COMMENT_ID serial,
    OWNER_ID INTEGER NOT NULL,
    COMMENT VARCHAR,
    ENTRY_ID INTEGER NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (ENTRY_ID) REFERENCES EIP_T_BLOG_ENTRY (ENTRY_ID) ON DELETE CASCADE,
    PRIMARY KEY (COMMENT_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_BLOG_FOOTMARK_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_BLOG_FOOTMARK_MAP
(
    ID serial,
    BLOG_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    CREATE_DATE DATE NOT NULL,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (BLOG_ID) REFERENCES EIP_T_BLOG (BLOG_ID) ON DELETE CASCADE,
    PRIMARY KEY (ID)
);

-----------------------------------------------------------------------------
-- EIP_T_CABINET_FOLDER
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_CABINET_FOLDER
(
    FOLDER_ID serial,
    PARENT_ID INTEGER NOT NULL,
    FOLDER_NAME VARCHAR (128) NOT NULL,
    NOTE VARCHAR,
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    PUBLIC_FLAG VARCHAR (1),
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (FOLDER_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_CABINET_FOLDER_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_CABINET_FOLDER_MAP
(
   ID serial,
   FOLDER_ID INTEGER,
   USER_ID INTEGER,
   STATUS VARCHAR (1),
   FOREIGN KEY (FOLDER_ID) REFERENCES EIP_T_CABINET_FOLDER (FOLDER_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_CABINET_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_CABINET_FILE
(
    FILE_ID serial,
    FOLDER_ID BIGINT NOT NULL,
    FILE_TITLE VARCHAR (128) NOT NULL,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_SIZE BIGINT,
    FILE_PATH varchar NOT NULL,
    NOTE VARCHAR,
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (FOLDER_ID) REFERENCES EIP_T_CABINET_FOLDER (FOLDER_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_FACILITY
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_FACILITY
(
    FACILITY_ID serial,
    USER_ID INTEGER NOT NULL,
    FACILITY_NAME VARCHAR (64) NOT NULL,
    NOTE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(FACILITY_ID)
);

-----------------------------------------------------------------------------
-- EIP_FACILITY_GROUP
-----------------------------------------------------------------------------

CREATE TABLE EIP_FACILITY_GROUP
(
    ID serial,
    FACILITY_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    PRIMARY KEY(ID),
    FOREIGN KEY (FACILITY_ID) REFERENCES EIP_M_FACILITY (FACILITY_ID) ON DELETE CASCADE,
    FOREIGN KEY (GROUP_ID) REFERENCES TURBINE_GROUP (GROUP_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_TIMECARD
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_TIMECARD
(
    TIMECARD_ID serial,
    USER_ID INTEGER NOT NULL,
    WORK_DATE TIMESTAMP,
    WORK_FLAG VARCHAR (1) NOT NULL,
    REASON VARCHAR,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(TIMECARD_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_TIMECARD_SETTINGS
-----------------------------------------------------------------------------
CREATE TABLE EIP_T_TIMECARD_SETTINGS(
  TIMECARD_SETTINGS_ID serial,
  USER_ID INTEGER NOT NULL,
  START_HOUR int4,
  START_MINUTE int4,
  END_HOUR int4,
  END_MINUTE int4,
  WORKTIME_IN int4,
  RESTTIME_IN int4,
  WORKTIME_OUT int4,
  RESTTIME_OUT int4,
  CREATE_DATE TIMESTAMP,
  UPDATE_DATE TIMESTAMP,
  PRIMARY KEY(TIMECARD_SETTINGS_ID)
);


-----------------------------------------------------------------------------
-- EIP_T_EXT_TIMECARD
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_EXT_TIMECARD
(
    TIMECARD_ID serial,
    USER_ID INTEGER NOT NULL,
    PUNCH_DATE DATE,
    TYPE VARCHAR (1),
    CLOCK_IN_TIME TIMESTAMP,
    CLOCK_OUT_TIME TIMESTAMP,
    REASON VARCHAR,
    OUTGOING_TIME1 TIMESTAMP,
    COMEBACK_TIME1 TIMESTAMP,
    OUTGOING_TIME2 TIMESTAMP,
    COMEBACK_TIME2 TIMESTAMP,
    OUTGOING_TIME3 TIMESTAMP,
    COMEBACK_TIME3 TIMESTAMP,
    OUTGOING_TIME4 TIMESTAMP,
    COMEBACK_TIME4 TIMESTAMP,
    OUTGOING_TIME5 TIMESTAMP,
    COMEBACK_TIME5 TIMESTAMP,
    REMARKS VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(TIMECARD_ID)
);


-----------------------------------------------------------------------------
-- EIP_T_EXT_TIMECARD_SYSTEM
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_EXT_TIMECARD_SYSTEM
(
    SYSTEM_ID serial,
    USER_ID INTEGER NOT NULL,
    SYSTEM_NAME VARCHAR (64),
    START_HOUR INTEGER,
    START_MINUTE INTEGER,
    END_HOUR INTEGER,
    END_MINUTE INTEGER,
    WORKTIME_IN INTEGER,
    RESTTIME_IN INTEGER,
    WORKTIME_OUT INTEGER,
    RESTTIME_OUT INTEGER,
    CHANGE_HOUR INTEGER,
    OUTGOING_ADD_FLAG VARCHAR (1),
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(SYSTEM_ID)
);


-----------------------------------------------------------------------------
-- EIP_T_EXT_TIMECARD_SYSTEM_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_EXT_TIMECARD_SYSTEM_MAP
(
    SYSTEM_MAP_ID serial,
    USER_ID INTEGER NOT NULL,
    SYSTEM_ID INTEGER NOT NULL,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (SYSTEM_ID) REFERENCES EIP_T_EXT_TIMECARD_SYSTEM (SYSTEM_ID) ON DELETE CASCADE,
    PRIMARY KEY(SYSTEM_MAP_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WORKFLOW_ROUTE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WORKFLOW_ROUTE
(
    ROUTE_ID serial,
    ROUTE_NAME VARCHAR (64) NOT NULL,
    NOTE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    ROUTE VARCHAR,
    PRIMARY KEY(ROUTE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WORKFLOW_CATEGORY
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WORKFLOW_CATEGORY
(
    CATEGORY_ID serial,
    USER_ID INTEGER NOT NULL,
    CATEGORY_NAME VARCHAR (64) NOT NULL,
    NOTE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    TEMPLATE VARCHAR,
    ROUTE_ID INTEGER,
    FOREIGN KEY (ROUTE_ID) REFERENCES EIP_T_WORKFLOW_ROUTE (ROUTE_ID),
    PRIMARY KEY(CATEGORY_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WORKFLOW_REQUEST
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WORKFLOW_REQUEST
(
    REQUEST_ID serial,
    PARENT_ID INTEGER,
    USER_ID INTEGER NOT NULL,
    REQUEST_NAME VARCHAR (64),
    CATEGORY_ID INTEGER,
    PRIORITY smallint,
    PROGRESS VARCHAR (1),
    NOTE VARCHAR,
    PRICE bigint,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    ROUTE_ID INTEGER,
    FOREIGN KEY (CATEGORY_ID) REFERENCES EIP_T_WORKFLOW_CATEGORY (CATEGORY_ID) ON DELETE CASCADE,
    FOREIGN KEY (ROUTE_ID) REFERENCES EIP_T_WORKFLOW_ROUTE (ROUTE_ID),
    PRIMARY KEY(REQUEST_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WORKFLOW_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WORKFLOW_FILE
(
    FILE_ID serial,
    OWNER_ID INTEGER,
    REQUEST_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH varchar NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (REQUEST_ID) REFERENCES EIP_T_WORKFLOW_REQUEST (REQUEST_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WORKFLOW_REQUEST_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WORKFLOW_REQUEST_MAP
(
   ID serial,
   REQUEST_ID INTEGER NOT NULL,
   USER_ID INTEGER NOT NULL,
   STATUS VARCHAR (1),
   ORDER_INDEX INTEGER NOT NULL,
   NOTE VARCHAR,
   CREATE_DATE DATE,
   UPDATE_DATE TIMESTAMP,
   FOREIGN KEY (REQUEST_ID) REFERENCES EIP_T_WORKFLOW_REQUEST (REQUEST_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_MEMO
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MEMO
(
    MEMO_ID serial,
    OWNER_ID INTEGER NOT NULL,
    MEMO_NAME VARCHAR (64) NOT NULL,
    NOTE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(MEMO_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WHATSNEW
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WHATSNEW
(
    WHATSNEW_ID serial,
    USER_ID INTEGER,
    PORTLET_TYPE INTEGER,
    PARENT_ID INTEGER,
    ENTITY_ID INTEGER,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(WHATSNEW_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_EVENTLOG
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_EVENTLOG
(
    EVENTLOG_ID serial,
    USER_ID INTEGER,
    EVENT_DATE TIMESTAMP,
    EVENT_TYPE INTEGER,
    PORTLET_TYPE INTEGER,
    ENTITY_ID INTEGER,
    IP_ADDR VARCHAR,
    NOTE VARCHAR,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(EVENTLOG_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_ACL_ROLE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_ACL_ROLE
(
    ROLE_ID serial,
    ROLE_NAME VARCHAR (99) NOT NULL,
    FEATURE_ID INTEGER NOT NULL,
    ACL_TYPE INTEGER,
    NOTE VARCHAR,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(ROLE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_ACL_PORTLET_FEATURE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_ACL_PORTLET_FEATURE
(
  FEATURE_ID serial,
  FEATURE_NAME VARCHAR,
  FEATURE_ALIAS_NAME VARCHAR,
  ACL_TYPE INTEGER,
  PRIMARY KEY(FEATURE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_ACL_USER_ROLE_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_ACL_USER_ROLE_MAP
(
    ID serial,
    USER_ID INT4 NOT NULL,
    ROLE_ID INT4 NOT NULL,
    PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-----------------------------------------------------------------------------
-- Default Data Insert
-----------------------------------------------------------------------------
-----------------------------------------------------------------------------
INSERT INTO TURBINE_PERMISSION VALUES(1,'view',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(2,'customize',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(3,'maximize',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(4,'minimize',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(5,'personalize',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(6,'info',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(7,'close',NULL);
INSERT INTO TURBINE_PERMISSION VALUES(8,'detach',NULL);
SELECT setval('turbine_permission_permission_id_seq',8);

INSERT INTO TURBINE_ROLE VALUES(1,'user',NULL);
INSERT INTO TURBINE_ROLE VALUES(2,'admin',NULL);
INSERT INTO TURBINE_ROLE VALUES(3,'guest',NULL);
SELECT setval('turbine_role_role_id_seq',3);

INSERT INTO TURBINE_GROUP VALUES(1,'Jetspeed',NULL,NULL,NULL,NULL);
INSERT INTO TURBINE_GROUP VALUES(2,'LoginUser',NULL,NULL,NULL,NULL);
INSERT INTO TURBINE_GROUP VALUES(3,'Facility',NULL,NULL,NULL,NULL);
SELECT setval('turbine_group_group_id_seq',3);

INSERT INTO TURBINE_USER VALUES(1,'admin','0DPiKuNIrrVmD8IUCuw1hQxNqZc=',' ','Admin','','CONFIRMED',now(),now(),now(),'F',NULL,now(),0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO TURBINE_USER VALUES(2,'template','MibsvmUCE6Sc0DrmcUB1Dk80AIM=','Aimluck','Template','','CONFIRMED',now(),now(),now(),'T',NULL, now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO TURBINE_USER VALUES(3,'anon','YVGPsXFatNaYrKMqeECsey5QfT4=','Anonymous','User','','CONFIRMED',now(),now(),now(),'F',NULL, now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
SELECT setval('turbine_user_user_id_seq',3);

INSERT INTO TURBINE_ROLE_PERMISSION VALUES(1,1);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(1,2);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(1,3);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(1,4);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(1,5);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(1,6);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,1);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,2);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,3);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,4);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,5);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,6);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(2,7);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(3,1);
INSERT INTO TURBINE_ROLE_PERMISSION VALUES(3,6);

INSERT INTO TURBINE_USER_GROUP_ROLE VALUES(1,2,1,1);
INSERT INTO TURBINE_USER_GROUP_ROLE VALUES(2,1,1,1);
INSERT INTO TURBINE_USER_GROUP_ROLE VALUES(3,1,1,2);
INSERT INTO TURBINE_USER_GROUP_ROLE VALUES(4,3,1,3);
SELECT setval('turbine_user_group_role_id_seq',4);

INSERT INTO EIP_T_TODO_CATEGORY VALUES(1,0,'未分類','',NULL ,NULL);
SELECT setval('eip_t_todo_category_category_id_seq',1);

INSERT INTO EIP_M_COMPANY VALUES (1, '', '', '', '', '', '', '', '', 80, '', 80, now(), now());
SELECT setval('eip_m_company_company_id_seq',1);

INSERT INTO EIP_T_MSGBOARD_CATEGORY VALUES(1,0,'その他','','T',NULL,NULL);
SELECT setval('eip_t_msgboard_category_category_id_seq',1);

INSERT INTO EIP_T_MSGBOARD_CATEGORY_MAP VALUES(1,1,0,'A');
SELECT setval('eip_t_msgboard_category_map_id_seq',1);

INSERT INTO EIP_M_ADDRESSBOOK_COMPANY (COMPANY_NAME,COMPANY_NAME_KANA,POST_NAME,ZIPCODE,ADDRESS,TELEPHONE,FAX_NUMBER,URL,CREATE_USER_ID,UPDATE_USER_ID,CREATE_DATE,UPDATE_DATE) VALUES ('未分類','ミブンルイ','','','','','','',1,1,NULL,NULL);

INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(1,1,1,3,'07:00',now(),now());
INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(2,1,21,3,NULL,now(),now());
INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(3,1,22,3,NULL,now(),now());
INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(4,1,23,3,NULL,now(),now());
INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(5,1,24,3,NULL,now(),now());
SELECT setval('eip_m_mail_notify_conf_notify_id_seq',5);

INSERT INTO EIP_T_TIMECARD_SETTINGS VALUES(1,1,9,0,18,0,360,60,360,60);
SELECT setval('eip_t_timecard_settings_timecard_settings_id_seq',1);

INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(111,'schedule_self','スケジュール（自分の予定）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(112,'schedule_other','スケジュール（他ユーザーの予定）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(113,'schedule_facility','スケジュール（施設の予約）操作',12);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(121,'blog_entry_self','ブログ（自分の記事）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(122,'blog_entry_other','ブログ（他ユーザーの記事）操作',3);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(123,'blog_entry_reply','ブログ（記事へのコメント）操作',20);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(124,'blog_theme','ブログ（テーマ）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(131,'msgboard_topic','掲示板（トピック）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(132,'msgboard_topic_reply','掲示板（トピック返信）操作',20);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(133,'msgboard_category','掲示板（自分のカテゴリ）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(134,'msgboard_category_other','掲示板（他ユーザーのカテゴリ）操作',27);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(135,'msgboard_topic_other','掲示板（他ユーザーのトピック）操作',24);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(141,'todo_todo_self','ToDo（自分のToDo）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(142,'todo_todo_other','ToDo（他ユーザーのToDo）操作',3);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(143,'todo_category_self','ToDo（カテゴリ）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(151,'workflow_request_self','ワークフロー（自分の依頼）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(152,'workflow_request_other','ワークフロー（他ユーザーの依頼）操作',3);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(161,'addressbook_address_inside','アドレス帳（社内アドレス）操作',3);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(162,'addressbook_address_outside','アドレス帳（社外アドレス）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(163,'addressbook_company','アドレス帳（会社情報）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(164,'addressbook_company_group','アドレス帳（社外グループ）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(171,'timecard_timecard_self','タイムカード（自分のタイムカード）操作',47);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(172,'timecard_timecard_other','タイムカード（他人のタイムカード）操作',33);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(181,'cabinet_file','共有フォルダ（ファイル）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(182,'cabinet_folder','共有フォルダ（フォルダ）操作',30);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(191,'manhour_summary_self','プロジェクト管理（自分の工数）操作',1);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(192,'manhour_summary_other','プロジェクト管理（他ユーザーの工数）操作',1);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(193,'manhour_common_category','プロジェクト管理（自分の共有カテゴリ）操作',31);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(194,'manhour_common_category_other','プロジェクト管理（他ユーザーの共有カテゴリ）操作',27);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(201,'portlet_customize','ポートレット操作',29);

SELECT setval('eip_t_acl_portlet_feature_feature_id_seq',210);

-- schedule
INSERT INTO EIP_T_ACL_ROLE VALUES(1,'スケジュール（自分の予定）管理者',111,31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(2,'スケジュール（他ユーザーの予定）',112,3,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(3,'スケジュール（施設の予約）管理者',113,12,NULL);

-- blog
INSERT INTO EIP_T_ACL_ROLE VALUES(4,'ブログ（自分の記事）管理者',121,31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(5,'ブログ（他ユーザーの記事）管理者',122,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(6,'ブログ（記事へのコメント）管理者',123,20,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(7,'ブログ（テーマ）管理者',124,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません');

-- msgboard
INSERT INTO EIP_T_ACL_ROLE VALUES(8,'掲示板（トピック）管理者',131,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(9,'掲示板（トピック返信）管理者',132,20,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(10,'掲示板（自分のカテゴリ）管理者',133,31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません');

-- todo
INSERT INTO EIP_T_ACL_ROLE VALUES(12,'ToDo（自分のToDo）管理者',141,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(13,'ToDo（他ユーザーのToDo）管理者',142,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(14,'ToDo（カテゴリ）管理者',143,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません');

-- workflow
INSERT INTO EIP_T_ACL_ROLE VALUES(15,'ワークフロー（自分の依頼）管理者',151,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません ＊承認、再申請や差し戻しは編集の権限が必要です');
INSERT INTO EIP_T_ACL_ROLE VALUES(16,'ワークフロー（他ユーザーの依頼）管理者',152,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません');

-- addressbook
INSERT INTO EIP_T_ACL_ROLE VALUES(17,'アドレス帳（社内アドレス）管理者',161,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(18,'アドレス帳（社外アドレス）管理者',162,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(19,'アドレス帳（会社情報）管理者',163,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(20,'アドレス帳（社外グループ）管理者',164,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません');

-- timecard
INSERT INTO EIP_T_ACL_ROLE VALUES(21,'タイムカード（自分のタイムカード）管理者',171,47,'＊追加、編集、外部出力は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(22,'タイムカード（他人のタイムカード）管理者',172,33,'＊自分のタイムカード一覧表示の権限を持っていないと使用できません\n＊外部出力は一覧表示の権限を持っていないと使用できません');

-- cabinet
INSERT INTO EIP_T_ACL_ROLE VALUES(23,'共有フォルダ（ファイル）管理者',181,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(24,'共有フォルダ（フォルダ）管理者',182,30,'＊編集、削除は詳細表示の権限を持っていないと使用できません');

-- manhour
INSERT INTO EIP_T_ACL_ROLE VALUES(25,'プロジェクト管理（自分の工数）管理者',191,1,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(26,'プロジェクト管理（他ユーザーの工数）管理者',192,1,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(27,'プロジェクト管理（自分の共有カテゴリ）管理者',193,31,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(28,'プロジェクト管理（他ユーザーの共有カテゴリ）管理者',194,3,NULL);

--portlet
INSERT INTO EIP_T_ACL_ROLE VALUES(29,'ポートレット管理者',201,29,NULL);

SELECT setval('eip_t_acl_role_role_id_seq',31);

INSERT INTO EIP_T_BLOG_THEMA VALUES(1,'未分類','',0,0,NULL ,NULL);
SELECT setval('eip_t_blog_thema_thema_id_seq',1);

INSERT INTO EIP_T_CABINET_FOLDER VALUES(1,0,'ルートフォルダ','',0,0,0,NULL,NULL);
SELECT setval('eip_t_cabinet_folder_folder_id_seq',1);

INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(1,0,'未分類','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(2,0,'有給休暇届','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(3,0,'稟議書','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(4,0,'結婚休暇届','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(5,0,'産前産後休暇届','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(6,0,'育児休暇届','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(7,0,'育児時間届','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(8,0,'特別有給休暇届（業務上負傷等）','',NULL,NULL);
INSERT INTO EIP_T_WORKFLOW_CATEGORY VALUES(9,0,'忌引き休暇届','',NULL,NULL);
SELECT setval('eip_t_workflow_category_category_id_seq',9);

SELECT setval('eip_t_workflow_route_route_id_seq',1);


SELECT setval('eip_t_ext_timecard_timecard_id_seq',1);

INSERT INTO EIP_T_EXT_TIMECARD_SYSTEM VALUES(1, 0, '通常', 9, 0, 18, 0, 360, 60, 360, 60, 4, 'T',now(), now());
SELECT setval('eip_t_ext_timecard_system_system_id_seq',2);

SELECT setval('eip_t_ext_timecard_system_map_system_map_id_seq',1);