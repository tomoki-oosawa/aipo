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

-- 20130124
UPDATE EIP_T_ACL_PORTLET_FEATURE SET FEATURE_ALIAS_NAME = 'ユーザー名簿操作' WHERE FEATURE_NAME = 'addressbook_address_inside' AND FEATURE_ALIAS_NAME = 'アドレス帳（社内アドレス）操作';
UPDATE EIP_T_ACL_ROLE SET ROLE_NAME = 'ユーザー名簿管理者' WHERE  FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'addressbook_address_inside') AND ROLE_NAME = 'アドレス帳（社内アドレス）管理者';
-- 20130124

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

CREATE INDEX eip_t_gpdb_record_record_no_index ON EIP_T_GPDB_RECORD (RECORD_NO);

INSERT INTO EIP_M_GPDB_KUBUN VALUES (1, '都道府県', now(), now());
SELECT setval('pk_eip_m_gpdb_kubun',1);

INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (1, 1, '北海道', 1, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (2, 1, '青森県', 2, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (3, 1, '岩手県', 3, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (4, 1, '宮城県', 4, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (5, 1, '秋田県', 5, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (6, 1, '山形県', 6, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (7, 1, '福島県', 7, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (8, 1, '茨城県', 8, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (9, 1, '栃木県', 9, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (10, 1, '群馬県', 10, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (11, 1, '埼玉県', 11, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (12, 1, '千葉県', 12, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (13, 1, '東京都', 13, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (14, 1, '神奈川県', 14, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (15, 1, '新潟県', 15, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (16, 1, '富山県', 16, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (17, 1, '石川県', 17, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (18, 1, '福井県', 18, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (19, 1, '山梨県', 19, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (20, 1, '長野県', 20, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (21, 1, '岐阜県', 21, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (22, 1, '静岡県', 22, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (23, 1, '愛知県', 23, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (24, 1, '三重県', 24, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (25, 1, '滋賀県', 25, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (26, 1, '京都府', 26, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (27, 1, '大阪府', 27, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (28, 1, '兵庫県', 28, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (29, 1, '奈良県', 29, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (30, 1, '和歌山県', 30, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (31, 1, '鳥取県', 31, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (32, 1, '島根県', 32, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (33, 1, '岡山県', 33, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (34, 1, '広島県', 34, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (35, 1, '山口県', 35, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (36, 1, '徳島県', 36, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (37, 1, '香川県', 37, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (38, 1, '愛媛県', 38, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (39, 1, '高知県', 39, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (40, 1, '福岡県', 40, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (41, 1, '佐賀県', 41, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (42, 1, '長崎県', 42, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (43, 1, '熊本県', 43, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (44, 1, '大分県', 44, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (45, 1, '宮崎県', 45, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (46, 1, '鹿児島県', 46, now(), now());
INSERT INTO EIP_M_GPDB_KUBUN_VALUE VALUES (47, 1, '沖縄県', 47, now(), now());
SELECT setval('pk_eip_m_gpdb_kubun_value',47);

-----------------------------------------------------------------------------
-- EIP_T_WIKI
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WIKI
(
    WIKI_ID INTEGER NOT NULL,
    WIKI_NAME VARCHAR (64) NOT NULL,
    PARENT_ID INTEGER DEFAULT 0 NOT NULL,
    NOTE TEXT,
    CREATE_USER_ID INTEGER,
    UPDATE_USER_ID INTEGER,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY(WIKI_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_WIKI_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_WIKI_FILE
(
    FILE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    WIKI_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH TEXT NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (WIKI_ID) REFERENCES EIP_T_WIKI (WIKI_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

CREATE SEQUENCE pk_eip_t_wiki INCREMENT 20;
CREATE SEQUENCE pk_eip_t_wiki_file INCREMENT 20;

ALTER SEQUENCE pk_eip_t_wiki OWNED BY EIP_T_WIKI.WIKI_ID;
ALTER SEQUENCE pk_eip_t_wiki_file OWNED BY EIP_T_WIKI_FILE.FILE_ID;

CREATE INDEX eip_t_wiki_wiki_name_parent_id_index ON EIP_T_WIKI (WIKI_NAME, PARENT_ID);
CREATE INDEX eip_t_file_wiki_id_index ON EIP_T_WIKI_FILE (WIKI_ID);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT (
      PROJECT_ID         INTEGER                        NOT NULL    --プロジェクトID
    , PROJECT_NAME       TEXT                           NOT NULL    --プロジェクト名
    , EXPLANATION        TEXT                                       --説明
    , ADMIN_USER_ID      INTEGER                        NOT NULL    --管理者
    , PROGRESS_FLG       CHARACTER VARYING(1)           NOT NULL    --進捗率入力フラグ 1:進捗率を入力する 0:タスクを元に自動計算する
    , PROGRESS_RATE      INTEGER                                    --進捗率
    , CREATE_USER_ID     INTEGER                        NOT NULL    --登録ユーザーID
    , UPDATE_USER_ID     INTEGER                        NOT NULL    --更新ユーザーID
    , CREATE_DATE        TIMESTAMP                                                          --登録日
    , UPDATE_DATE        TIMESTAMP                                                          --更新日
    , PRIMARY KEY (PROJECT_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_MEMBER
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_MEMBER (
      ID          INTEGER                        NOT NULL    --ID
    , PROJECT_ID  INTEGER                        NOT NULL    --プロジェクトID
    , USER_ID     INTEGER                        NOT NULL    --ユーザーID
    , PRIMARY KEY (ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_TASK
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_TASK (
      TASK_ID                INTEGER                        NOT NULL    --タスクID
    , PARENT_TASK_ID         INTEGER                                    --親タスクID
    , PROJECT_ID             INTEGER                        NOT NULL    --プロジェクトID
    , TRACKER                TEXT                           NOT NULL    --トラッカー 1:機能 2:バグ 3:サポート
    , TASK_NAME              TEXT                           NOT NULL    --タスク名
    , EXPLANATION            TEXT                                       --説明
    , STATUS                 TEXT                           NOT NULL    --ステータス 1:新規 2:進行中 3:解決 4:フィードバック 5:終了 6:却下
    , PRIORITY               TEXT                           NOT NULL    --優先度 1:高 2:中 3:低
    , START_PLAN_DATE        DATE                                       --開始予定日
    , END_PLAN_DATE          DATE                                       --完了予定日
    , START_DATE             DATE                                       --開始実績日
    , END_DATE               DATE                                       --完了実績日
    , PLAN_WORKLOAD          DECIMAL(8,3)                               --計画工数（時間）
    , PROGRESS_RATE          INTEGER                                    --進捗率
    , ORDER_NO               INTEGER                                    --表示順
    , CREATE_USER_ID         INTEGER                        NOT NULL    --登録ユーザーID
    , UPDATE_USER_ID         INTEGER                        NOT NULL    --更新ユーザーID
    , CREATE_DATE            TIMESTAMP                                                          --登録日
    , UPDATE_DATE            TIMESTAMP                                                          --更新日
    , PRIMARY KEY (TASK_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_TASK_MEMBER
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_TASK_MEMBER (
      ID              INTEGER                NOT NULL    --ID
    , TASK_ID         INTEGER                NOT NULL    --タスクID
    , USER_ID         INTEGER                NOT NULL    --ユーザーID
    , WORKLOAD        DECIMAL(8,3)           NOT NULL    --工数
    , PRIMARY KEY (ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_TASK_COMMENT
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_TASK_COMMENT (
      COMMENT_ID            INTEGER                        NOT NULL    --コメントID
    , TASK_ID               INTEGER                        NOT NULL    --タスクID
    , COMMENT               TEXT                           NOT NULL    --コメント
    , CREATE_USER_ID        INTEGER                        NOT NULL    --登録ユーザーID
    , CREATE_DATE           TIMESTAMP                                                          --登録日
    , UPDATE_DATE           TIMESTAMP                                                          --更新日
    , PRIMARY KEY (COMMENT_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_FILE (
      FILE_ID           INTEGER                NOT NULL
    , OWNER_ID          INTEGER
    , PROJECT_ID        INTEGER
    , FILE_NAME         VARCHAR (128) NOT NULL
    , FILE_PATH         TEXT                   NOT NULL
    , FILE_THUMBNAIL    BYTEA
    , CREATE_DATE       DATE
    , UPDATE_DATE       TIMESTAMP
    , PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_TASK_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_TASK_FILE (
      FILE_ID         INTEGER                NOT NULL
    , OWNER_ID        INTEGER
    , TASK_ID         INTEGER
    , FILE_NAME       VARCHAR (128) NOT NULL
    , FILE_PATH       TEXT                   NOT NULL
    , FILE_THUMBNAIL  BYTEA
    , CREATE_DATE     DATE
    , UPDATE_DATE     TIMESTAMP
    , PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_T_PROJECT_TASK_COMMENT_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_PROJECT_TASK_COMMENT_FILE (
      FILE_ID         INTEGER                NOT NULL
    , OWNER_ID        INTEGER
    , COMMENT_ID      INTEGER
    , FILE_NAME       VARCHAR (128) NOT NULL
    , FILE_PATH       TEXT                   NOT NULL
    , FILE_THUMBNAIL  BYTEA
    , CREATE_DATE     DATE
    , UPDATE_DATE     TIMESTAMP
    , PRIMARY KEY (FILE_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_PROJECT_KUBUN
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_PROJECT_KUBUN (
      PROJECT_KUBUN_ID     INTEGER                        NOT NULL    --区分ID
    , PROJECT_KUBUN_CD     TEXT                           NOT NULL    --区分コード
    , PROJECT_KUBUN_NAME   TEXT                           NOT NULL    --区分名
    , CREATE_DATE          TIMESTAMP                --登録日
    , UPDATE_DATE          TIMESTAMP                --更新日
    , PRIMARY KEY (PROJECT_KUBUN_ID)
);

-----------------------------------------------------------------------------
-- EIP_M_PROJECT_KUBUN_VALUE
-----------------------------------------------------------------------------

CREATE TABLE EIP_M_PROJECT_KUBUN_VALUE (
      PROJECT_KUBUN_VALUE_ID  INTEGER                        NOT NULL    --区分値ID
    , PROJECT_KUBUN_ID        INTEGER                        NOT NULL    --区分ID
    , PROJECT_KUBUN_VALUE_CD  TEXT                           NOT NULL    --区分値コード
    , PROJECT_KUBUN_VALUE     TEXT                           NOT NULL    --区分値
    , ORDER_NO                INTEGER                        NOT NULL    --表示順
    , CREATE_DATE             TIMESTAMP               --登録日
    , UPDATE_DATE             TIMESTAMP                --更新日
    , PRIMARY KEY (PROJECT_KUBUN_VALUE_ID)
);

CREATE SEQUENCE pk_eip_t_project INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_comment INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_member INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_member INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_file INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_file INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_comment_file INCREMENT 20;
CREATE SEQUENCE pk_eip_m_project_kubun INCREMENT 20;
CREATE SEQUENCE pk_eip_m_project_kubun_value INCREMENT 20;

ALTER SEQUENCE pk_eip_t_project OWNED BY EIP_T_PROJECT.PROJECT_ID;
ALTER SEQUENCE pk_eip_t_project_task OWNED BY EIP_T_PROJECT_TASK.TASK_ID;
ALTER SEQUENCE pk_eip_t_project_task_comment OWNED BY EIP_T_PROJECT_TASK_COMMENT.TASK_ID;
ALTER SEQUENCE pk_eip_t_project_member OWNED BY EIP_T_PROJECT_TASK_MEMBER.ID;
ALTER SEQUENCE pk_eip_t_project_task_member OWNED BY EIP_T_PROJECT_TASK_MEMBER.ID;
ALTER SEQUENCE pk_eip_t_project_file OWNED BY EIP_T_PROJECT_FILE.FILE_ID;
ALTER SEQUENCE pk_eip_t_project_task_file OWNED BY EIP_T_PROJECT_TASK_FILE.FILE_ID;
ALTER SEQUENCE pk_eip_t_project_task_comment_file OWNED BY EIP_T_PROJECT_TASK_COMMENT_FILE.FILE_ID;
ALTER SEQUENCE pk_eip_m_project_kubun OWNED BY EIP_M_PROJECT_KUBUN.PROJECT_KUBUN_ID;
ALTER SEQUENCE pk_eip_m_project_kubun_value OWNED BY EIP_M_PROJECT_KUBUN_VALUE.PROJECT_KUBUN_VALUE_ID;

CREATE INDEX eip_t_project_member_project_id_user_id_index ON EIP_T_PROJECT_MEMBER (PROJECT_ID, USER_ID);
CREATE INDEX eip_t_project_task_project_id_index ON EIP_T_PROJECT_TASK (PROJECT_ID);
CREATE INDEX eip_t_project_task_parent_task_id_index ON EIP_T_PROJECT_TASK (PARENT_TASK_ID);
CREATE INDEX eip_t_project_task_member_task_id_user_id_index ON EIP_T_PROJECT_TASK_MEMBER (TASK_ID, USER_ID);
CREATE INDEX eip_t_project_task_comment_task_id_index ON EIP_T_PROJECT_TASK_COMMENT (TASK_ID);
CREATE INDEX eip_t_project_file_project_id_index ON EIP_T_PROJECT_FILE (PROJECT_ID);
CREATE INDEX eip_t_project_task_file_task_id_index ON EIP_T_PROJECT_TASK_FILE (TASK_ID);
CREATE INDEX eip_t_project_task_comment_file_comment_id_index ON EIP_T_PROJECT_TASK_COMMENT_FILE (COMMENT_ID);

INSERT INTO EIP_M_PROJECT_KUBUN VALUES (1,'tracker','トラッカー', now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN VALUES (2,'status','ステータス', now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN VALUES (3,'priority','優先度', now(), now());
SELECT setval('pk_eip_m_project_kubun',3);

INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(1,1,'1','機能',1, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(2,1,'2','バグ',2, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(3,1,'3','サポート',3, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(4,2,'1','新規',1, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(5,2,'2','進行中',2, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(6,2,'3','フィードバック',3, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(7,2,'4','完了',4, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(8,2,'5','却下',5, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(9,2,'6','停止',6, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(10,3,'1','高',1, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(11,3,'2','中',2, now(), now());
INSERT INTO EIP_M_PROJECT_KUBUN_VALUE VALUES(12,3,'3','低',3, now(), now());
SELECT setval('pk_eip_m_project_kubun_value',12);

-- 20140801
CREATE INDEX eip_t_schedule_date_index ON EIP_T_SCHEDULE (START_DATE, END_DATE, UPDATE_DATE);
-- 20140801

-- 20140901
-----------------------------------------------------------------------------
-- EIP_T_MESSAGE_ROOM
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MESSAGE_ROOM
(
    ROOM_ID INTEGER NOT NULL,
    NAME VARCHAR (255),
    ROOM_TYPE VARCHAR(1) DEFAULT 'G',
    AUTO_NAME VARCHAR(1) DEFAULT 'F',
    LAST_MESSAGE TEXT,
    LAST_UPDATE_DATE TIMESTAMP DEFAULT NULL,
    CREATE_USER_ID INTEGER NOT NULL,
    PHOTO bytea,
    PHOTO_SMARTPHONE bytea,
    PHOTO_MODIFIED TIMESTAMP,
    HAS_PHOTO VARCHAR (1) DEFAULT 'F',
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    PRIMARY KEY (ROOM_ID)
);

CREATE SEQUENCE pk_eip_t_message_room INCREMENT 20;
ALTER SEQUENCE pk_eip_t_message_room OWNED BY EIP_T_MESSAGE_ROOM.ROOM_ID;

-----------------------------------------------------------------------------
-- EIP_T_MESSAGE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MESSAGE
(
    MESSAGE_ID INTEGER NOT NULL,
    ROOM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    MESSAGE TEXT,
    MEMBER_COUNT INTEGER NOT NULL,
    CREATE_DATE TIMESTAMP,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (ROOM_ID) REFERENCES EIP_T_MESSAGE_ROOM (ROOM_ID) ON DELETE CASCADE,
    PRIMARY KEY (MESSAGE_ID)
);

CREATE SEQUENCE pk_eip_t_message INCREMENT 20;
ALTER SEQUENCE pk_eip_t_message OWNED BY EIP_T_MESSAGE.MESSAGE_ID;

create index eip_t_message_room_id_create_date ON EIP_T_MESSAGE(ROOM_ID,CREATE_DATE);

-----------------------------------------------------------------------------
-- EIP_T_MESSAGE_ROOM_MEMBER
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MESSAGE_ROOM_MEMBER
(
    ID INTEGER NOT NULL,
    ROOM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    LOGIN_NAME VARCHAR (32) NOT NULL,
    TARGET_USER_ID INTEGER NOT NULL,
    FOREIGN KEY (ROOM_ID) REFERENCES EIP_T_MESSAGE_ROOM (ROOM_ID) ON DELETE CASCADE,
    PRIMARY KEY (ID)
);

CREATE SEQUENCE pk_eip_t_message_room_member INCREMENT 20;
ALTER SEQUENCE pk_eip_t_message_room_member OWNED BY EIP_T_MESSAGE_ROOM_MEMBER.ID;

create index eip_t_message_room_member_target_user_id ON EIP_T_MESSAGE_ROOM_MEMBER(TARGET_USER_ID);
create index eip_t_message_room_member_user_id_target_user_id ON EIP_T_MESSAGE_ROOM_MEMBER(USER_ID,TARGET_USER_ID);

-----------------------------------------------------------------------------
-- EIP_T_MESSAGE_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MESSAGE_FILE
(
    FILE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    MESSAGE_ID INTEGER,
    ROOM_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH TEXT NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (MESSAGE_ID) REFERENCES EIP_T_MESSAGE (MESSAGE_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

CREATE SEQUENCE pk_eip_t_message_file INCREMENT 20;
ALTER SEQUENCE pk_eip_t_message_file OWNED BY EIP_T_MESSAGE_FILE.FILE_ID;

-----------------------------------------------------------------------------
-- EIP_T_MESSAGE_READ
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_MESSAGE_READ
(
    ID INTEGER NOT NULL,
    MESSAGE_ID INTEGER NOT NULL,
    ROOM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    IS_READ VARCHAR(1) DEFAULT 'F',
    FOREIGN KEY (MESSAGE_ID) REFERENCES EIP_T_MESSAGE (MESSAGE_ID) ON DELETE CASCADE,
    PRIMARY KEY (ID)
);

CREATE SEQUENCE pk_eip_t_message_read INCREMENT 20;
ALTER SEQUENCE pk_eip_t_message_read OWNED BY EIP_T_MESSAGE_READ.ID;

create index eip_t_message_read_index1 ON eip_t_message_read(ROOM_ID,USER_ID,IS_READ);
create index eip_t_message_read_index2 ON eip_t_message_read(ROOM_ID,MESSAGE_ID,IS_READ);
-- 20140901

-- 20140911
CREATE INDEX eip_t_ext_timecard_user_id_index ON EIP_T_EXT_TIMECARD(USER_ID);
CREATE INDEX eip_t_note_map_user_id_index ON EIP_T_NOTE_MAP(USER_ID);
CREATE INDEX eip_t_eventlog_event_type_index ON EIP_T_EVENTLOG(EVENT_TYPE);
CREATE INDEX eip_t_eventlog_user_id_index ON EIP_T_EVENTLOG(USER_ID);
CREATE INDEX eip_t_msgboard_topic_category_id_index ON EIP_T_MSGBOARD_TOPIC(CATEGORY_ID);
CREATE INDEX eip_t_acl_user_role_map_role_id_index ON EIP_T_ACL_USER_ROLE_MAP(ROLE_ID);
CREATE INDEX eip_t_acl_role_acl_type_index ON EIP_T_ACL_ROLE(ACL_TYPE);
CREATE INDEX eip_m_user_position_index ON EIP_M_USER_POSITION(POSITION);
ALTER TABLE EIP_M_POSITION ADD SORT INTEGER ;
ALTER TABLE EIP_M_POST ADD SORT INTEGER ;
ALTER TABLE EIP_FACILITY_GROUP ADD SORT INTEGER ;
ALTER TABLE EIP_M_FACILITY_GROUP ADD SORT INTEGER ;
CREATE INDEX eip_t_gpdb_record_gpdb_id_index ON EIP_T_GPDB_RECORD (GPDB_ID);
