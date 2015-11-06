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

-- 20111014
-- modify table struct
ALTER TABLE EIP_T_TODO ADD CREATE_USER_ID INTEGER NOT NULL DEFAULT 0;
UPDATE EIP_T_TODO SET CREATE_USER_ID = USER_ID;
-- 20111014

-- 20111019
-- change ACL settings
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 31 WHERE FEATURE_NAME = 'todo_todo_other';
UPDATE EIP_T_ACL_ROLE SET ACL_TYPE = 31, NOTE = '＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません' WHERE FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'todo_todo_other');

-- modify table struct
ALTER TABLE EIP_T_TODO_CATEGORY ADD UPDATE_USER_ID INTEGER NOT NULL DEFAULT 0;
UPDATE EIP_T_TODO_CATEGORY SET UPDATE_USER_ID = USER_ID;

-- add new ACL setting
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'todo_category_other','ToDo（他ユーザのカテゴリ）操作',27);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'), 'ToDo（他ユーザのカテゴリ）管理者', (SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'todo_category_other' LIMIT 1),27,'＊詳細表示、編集、削除は一覧表示の権限を持っていないと使用できません');
-- 20111019

-- 20111021
-- create table struct
CREATE TABLE eip_m_inactive_application (
    ID INTEGER NOT NULL,
    NAME varchar(128) NULL,
    PRIMARY KEY (ID)
)
;
CREATE SEQUENCE pk_eip_m_inactive_application INCREMENT 20 START 200;

INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(NEXTVAL('pk_eip_m_mail_notify_conf'),1,25,3,NULL,now(),now());

-- 20111021

-- 20111025
INSERT INTO EIP_T_CABINET_FOLDER_MAP VALUES(NEXTVAL('pk_eip_t_cabinet_folder_map'),1,0,null);
-- 20111025

-- 20111028
-- UPDATE EIP_T_CABINET_FILE SET COUNTER = 0 WHERE COUNTER = '';
UPDATE EIP_T_CABINET_FILE SET COUNTER = 0 WHERE COUNTER IS NULL;
-- 20111028

-- 20111116
UPDATE EIP_T_ACL_PORTLET_FEATURE SET FEATURE_ALIAS_NAME = 'スケジュール（設備の予約）操作' WHERE FEATURE_NAME = 'schedule_facility' AND FEATURE_ALIAS_NAME = 'スケジュール（施設の予約）操作';
UPDATE EIP_T_ACL_ROLE SET ROLE_NAME = 'スケジュール（設備の予約）管理者' WHERE  FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'schedule_facility') AND ROLE_NAME = 'スケジュール（施設の予約）管理者';
-- 20111116

-- 20111116
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 27 WHERE FEATURE_NAME = 'blog_entry_other';
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'blog_entry_other_reply','ブログ（他ユーザーの記事へのコメント）操作',16);
UPDATE EIP_T_ACL_ROLE SET NOTE = '＊詳細表示、編集、削除は一覧表示の権限を持っていないと使用できません' WHERE FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'blog_entry_other');

-- 20111116

-- 20111124
ALTER TABLE EIP_T_TODO ALTER COLUMN CREATE_USER_ID SET NOT NULL;
ALTER TABLE EIP_T_TODO ALTER COLUMN CREATE_USER_ID SET DEFAULT NULL;

ALTER TABLE EIP_T_TODO_CATEGORY ALTER COLUMN UPDATE_USER_ID SET NOT NULL;
ALTER TABLE EIP_T_TODO_CATEGORY ALTER COLUMN UPDATE_USER_ID SET DEFAULT NULL;
-- 20111124

-- 20111214
-----------------------------------------------------------------------------
-- EIP_T_REPORT
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_REPORT
(
    REPORT_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    REPORT_NAME VARCHAR (64),
    NOTE TEXT,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(REPORT_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_REPORT_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_REPORT_FILE
(
    FILE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    REPORT_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH TEXT NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (REPORT_ID) REFERENCES EIP_T_REPORT (REPORT_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_REPORT_MEMBER_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_REPORT_MEMBER_MAP
(
   ID INTEGER NOT NULL,
   REPORT_ID INTEGER NOT NULL,
   USER_ID INTEGER NOT NULL,
   FOREIGN KEY (REPORT_ID) REFERENCES EIP_T_REPORT (REPORT_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- EIP_T_REPORT_MAP
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_REPORT_MAP
(
   ID INTEGER NOT NULL,
   REPORT_ID INTEGER NOT NULL,
   USER_ID INTEGER NOT NULL,
   STATUS VARCHAR (1),
   CREATE_DATE DATE,
   UPDATE_DATE TIMESTAMP,
   FOREIGN KEY (REPORT_ID) REFERENCES EIP_T_REPORT (REPORT_ID) ON DELETE CASCADE,
   PRIMARY KEY(ID)
);

-----------------------------------------------------------------------------
-- CREATE SEQUENCE
-----------------------------------------------------------------------------

CREATE SEQUENCE pk_eip_t_report INCREMENT 20;
CREATE SEQUENCE pk_eip_t_report_file INCREMENT 20;
CREATE SEQUENCE pk_eip_t_report_member_map INCREMENT 20;
CREATE SEQUENCE pk_eip_t_report_map INCREMENT 20;


-----------------------------------------------------------------------------
-- ALTER SEQUENCE
-----------------------------------------------------------------------------

INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(NEXTVAL('pk_eip_m_mail_notify_conf'),1,26,3,NULL,now(),now());


-----------------------------------------------------------------------------
-- ALTER SEQUENCE
-----------------------------------------------------------------------------

ALTER SEQUENCE pk_eip_t_report OWNED BY EIP_T_REPORT.REPORT_ID;
ALTER SEQUENCE pk_eip_t_report_file OWNED BY EIP_T_REPORT_FILE.FILE_ID;
ALTER SEQUENCE pk_eip_t_report_member_map OWNED BY EIP_T_REPORT_MEMBER_MAP.ID;
ALTER SEQUENCE pk_eip_t_report_map OWNED BY EIP_T_REPORT_MAP.ID;

UPDATE eip_t_acl_user_role_map SET role_id = role_id + 10000
WHERE role_id IN (SELECT role_id FROM eip_t_acl_role WHERE create_date IS NOT NULL and role_id < 10000);
UPDATE eip_t_acl_role SET role_id = role_id + 10000 WHERE create_date IS NOT NULL and role_id < 10000;
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'report_self','報告書（自分の報告書）操作',31);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'), '報告書（自分の報告書）管理者', (SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'report_self' LIMIT 1),31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'report_other','報告書（他ユーザーの報告書）操作',3);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'),'報告書（他ユーザーの報告書）管理者', (SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'report_other' LIMIT 1),3,'＊詳細表示は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'report_reply','報告書（返信）操作',3);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'),'報告書（返信）管理者', (SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'report_reply' LIMIT 1),3,NULL);
SELECT setval('pk_eip_t_acl_role', 11000);
-- 20111214
-- 20111219
ALTER TABLE EIP_T_REPORT ADD PARENT_ID INTEGER NOT NULL DEFAULT 0;
-- 20111219

-- 20111219
ALTER TABLE EIP_T_REPORT ADD START_DATE TIMESTAMP DEFAULT now();
ALTER TABLE EIP_T_REPORT ADD END_DATE TIMESTAMP DEFAULT now();
-- 20111219

-- 20120113
UPDATE EIP_T_ACL_ROLE SET ACL_TYPE=20, role_name='報告書（報告書への返信）管理者' WHERE ROLE_NAME = '報告書（返信）管理者';
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE=20, FEATURE_ALIAS_NAME='報告書（報告書への返信）操作' WHERE FEATURE_ALIAS_NAME = '報告書（返信）操作';
-- 20120113

-- 20120113
UPDATE EIP_T_SCHEDULE SET MAIL_FLAG = 'N' WHERE MAIL_FLAG = 'I';
-- 20120113

--20120120
INSERT INTO EIP_M_MAIL_NOTIFY_CONF VALUES(8,1,27,3,NULL,now(),now());
--20120120
