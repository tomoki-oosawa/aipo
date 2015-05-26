--
-- Aipo is a groupware program developed by Aimluck,Inc.
-- Copyright (C) 2004-2015 Aimluck,Inc.
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

ALTER TABLE EIP_T_ACL_PORTLET_FEATURE ALTER FEATURE_ALIAS_NAME TYPE VARCHAR(99)
;

ALTER TABLE EIP_T_ACL_PORTLET_FEATURE ALTER FEATURE_NAME TYPE VARCHAR(99)
;

ALTER TABLE EIP_T_MAIL_FILTER ALTER FILTER_NAME TYPE VARCHAR(255)
;

ALTER TABLE EIP_T_MAIL_FILTER ALTER FILTER_STRING TYPE VARCHAR(255)
;

ALTER TABLE EIP_T_MAIL_FOLDER ALTER FOLDER_NAME TYPE VARCHAR(128)
;

ALTER TABLE EIP_T_COMMON_CATEGORY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_SCHEDULE ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_TODO_CATEGORY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_TODO ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_M_MAIL_ACCOUNT ALTER SIGNATURE TYPE TEXT
;

ALTER TABLE EIP_T_MAIL ALTER SUBJECT TYPE TEXT
;

ALTER TABLE EIP_T_MAIL ALTER PERSON TYPE TEXT
;

ALTER TABLE EIP_T_MAIL ALTER FILE_PATH TYPE TEXT
;

ALTER TABLE EIP_M_ADDRESSBOOK ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_NOTE ALTER MESSAGE TYPE TEXT
;

ALTER TABLE EIP_T_MSGBOARD_CATEGORY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_MSGBOARD_TOPIC ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_MSGBOARD_FILE ALTER FILE_PATH TYPE TEXT
;

ALTER TABLE EIP_T_BLOG_THEMA ALTER DESCRIPTION TYPE TEXT
;

ALTER TABLE EIP_T_BLOG_ENTRY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_BLOG_COMMENT ALTER COMMENT TYPE TEXT
;

ALTER TABLE EIP_T_BLOG_FILE ALTER FILE_PATH TYPE TEXT
;

ALTER TABLE EIP_T_CABINET_FOLDER ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_CABINET_FILE ALTER FILE_PATH TYPE TEXT
;

ALTER TABLE EIP_T_CABINET_FILE ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_CABINET_FILE ALTER FOLDER_ID TYPE INT
;

ALTER TABLE EIP_M_FACILITY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_M_FACILITY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_TIMECARD ALTER REASON TYPE TEXT
;

ALTER TABLE EIP_T_EXT_TIMECARD ALTER REASON TYPE TEXT
;

ALTER TABLE EIP_T_EXT_TIMECARD ALTER REMARKS TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_ROUTE ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_ROUTE ALTER ROUTE TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_CATEGORY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_CATEGORY ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_CATEGORY ALTER TEMPLATE TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_REQUEST ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_FILE ALTER FILE_PATH TYPE TEXT
;

ALTER TABLE EIP_T_WORKFLOW_REQUEST_MAP ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_MEMO ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_EVENTLOG ALTER IP_ADDR TYPE TEXT
;

ALTER TABLE EIP_T_EVENTLOG ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_ACL_ROLE ALTER NOTE TYPE TEXT
;

ALTER TABLE EIP_T_ACL_ROLE ALTER NOTE TYPE TEXT
;

ALTER SEQUENCE aipo_license_license_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_facility_group_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_address_group_group_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_addressbook_address_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_addressbook_company_company_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_company_company_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_facility_facility_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_mail_account_account_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_mail_notify_conf_notify_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_position_position_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_post_post_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_m_user_position_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_acl_portlet_feature_feature_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_acl_role_role_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_acl_user_role_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_addressbook_group_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_blog_blog_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_blog_comment_comment_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_blog_entry_entry_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_blog_file_file_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_blog_footmark_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_blog_thema_thema_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_cabinet_file_file_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_cabinet_folder_folder_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_cabinet_folder_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_common_category_common_category_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_eventlog_eventlog_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_ext_timecard_timecard_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_ext_timecard_system_system_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_ext_timecard_system_map_system_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_mail_mail_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_mail_filter_filter_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_mail_folder_folder_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_memo_memo_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_msgboard_category_category_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_msgboard_category_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_msgboard_file_file_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_msgboard_topic_topic_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_note_note_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_note_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_schedule_schedule_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_schedule_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_timecard_timecard_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_timecard_settings_timecard_settings_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_todo_todo_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_todo_category_category_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_whatsnew_whatsnew_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_workflow_category_category_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_workflow_file_file_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_workflow_request_request_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_workflow_request_map_id_seq INCREMENT BY 20;
ALTER SEQUENCE eip_t_workflow_route_route_id_seq INCREMENT BY 20;
ALTER SEQUENCE turbine_group_group_id_seq INCREMENT BY 20;
ALTER SEQUENCE turbine_permission_permission_id_seq INCREMENT BY 20;
ALTER SEQUENCE turbine_role_role_id_seq INCREMENT BY 20;
ALTER SEQUENCE turbine_user_group_role_id_seq INCREMENT BY 20;
ALTER SEQUENCE turbine_user_user_id_seq INCREMENT BY 20;

ALTER SEQUENCE aipo_license_license_id_seq RENAME TO pk_aipo_license ;
ALTER SEQUENCE eip_facility_group_id_seq RENAME TO pk_eip_facility_group ;
ALTER SEQUENCE eip_m_address_group_group_id_seq RENAME TO pk_eip_m_address_group ;
ALTER SEQUENCE eip_m_addressbook_address_id_seq RENAME TO pk_eip_m_addressbook ;
ALTER SEQUENCE eip_m_addressbook_company_company_id_seq RENAME TO pk_eip_m_addressbook_company ;
ALTER SEQUENCE eip_m_company_company_id_seq RENAME TO pk_eip_m_company ;
ALTER SEQUENCE eip_m_facility_facility_id_seq RENAME TO pk_eip_m_facility ;
ALTER SEQUENCE eip_m_mail_account_account_id_seq RENAME TO pk_eip_m_mail_account ;
ALTER SEQUENCE eip_m_mail_notify_conf_notify_id_seq RENAME TO pk_eip_m_mail_notify_conf ;
ALTER SEQUENCE eip_m_position_position_id_seq RENAME TO pk_eip_m_position ;
ALTER SEQUENCE eip_m_post_post_id_seq RENAME TO pk_eip_m_post ;
ALTER SEQUENCE eip_m_user_position_id_seq RENAME TO pk_eip_m_user_position ;
ALTER SEQUENCE eip_t_acl_portlet_feature_feature_id_seq RENAME TO pk_eip_t_acl_portlet_feature ;
ALTER SEQUENCE eip_t_acl_role_role_id_seq RENAME TO pk_eip_t_acl_role ;
ALTER SEQUENCE eip_t_acl_user_role_map_id_seq RENAME TO pk_eip_t_acl_user_role_map ;
ALTER SEQUENCE eip_t_addressbook_group_map_id_seq RENAME TO pk_eip_t_addressbook_group_map ;
ALTER SEQUENCE eip_t_blog_blog_id_seq RENAME TO pk_eip_t_blog ;
ALTER SEQUENCE eip_t_blog_comment_comment_id_seq RENAME TO pk_eip_t_blog_comment ;
ALTER SEQUENCE eip_t_blog_entry_entry_id_seq RENAME TO pk_eip_t_blog_entry ;
ALTER SEQUENCE eip_t_blog_file_file_id_seq RENAME TO pk_eip_t_blog_file ;
ALTER SEQUENCE eip_t_blog_footmark_map_id_seq RENAME TO pk_eip_t_blog_footmark_map ;
ALTER SEQUENCE eip_t_blog_thema_thema_id_seq RENAME TO pk_eip_t_blog_thema ;
ALTER SEQUENCE eip_t_cabinet_file_file_id_seq RENAME TO pk_eip_t_cabinet_file ;
ALTER SEQUENCE eip_t_cabinet_folder_folder_id_seq RENAME TO pk_eip_t_cabinet_folder ;
ALTER SEQUENCE eip_t_cabinet_folder_map_id_seq RENAME TO pk_eip_t_cabinet_folder_map ;
ALTER SEQUENCE eip_t_common_category_common_category_id_seq RENAME TO pk_eip_t_common_category ;
ALTER SEQUENCE eip_t_eventlog_eventlog_id_seq RENAME TO pk_eip_t_eventlog ;
ALTER SEQUENCE eip_t_ext_timecard_timecard_id_seq RENAME TO pk_eip_t_ext_timecard ;
ALTER SEQUENCE eip_t_ext_timecard_system_system_id_seq RENAME TO pk_eip_t_ext_timecard_system ;
ALTER SEQUENCE eip_t_ext_timecard_system_map_system_map_id_seq RENAME TO pk_eip_t_ext_timecard_system_map ;
ALTER SEQUENCE eip_t_mail_mail_id_seq RENAME TO pk_eip_t_mail ;
ALTER SEQUENCE eip_t_mail_filter_filter_id_seq RENAME TO pk_eip_t_mail_filter ;
ALTER SEQUENCE eip_t_mail_folder_folder_id_seq RENAME TO pk_eip_t_mail_folder ;
ALTER SEQUENCE eip_t_memo_memo_id_seq RENAME TO pk_eip_t_memo ;
ALTER SEQUENCE eip_t_msgboard_category_category_id_seq RENAME TO pk_eip_t_msgboard_category ;
ALTER SEQUENCE eip_t_msgboard_category_map_id_seq RENAME TO pk_eip_t_msgboard_category_map ;
ALTER SEQUENCE eip_t_msgboard_file_file_id_seq RENAME TO pk_eip_t_msgboard_file ;
ALTER SEQUENCE eip_t_msgboard_topic_topic_id_seq RENAME TO pk_eip_t_msgboard_topic ;
ALTER SEQUENCE eip_t_note_note_id_seq RENAME TO pk_eip_t_note ;
ALTER SEQUENCE eip_t_note_map_id_seq RENAME TO pk_eip_t_note_map ;
ALTER SEQUENCE eip_t_schedule_schedule_id_seq RENAME TO pk_eip_t_schedule ;
ALTER SEQUENCE eip_t_schedule_map_id_seq RENAME TO pk_eip_t_schedule_map ;
ALTER SEQUENCE eip_t_timecard_timecard_id_seq RENAME TO pk_eip_t_timecard ;
ALTER SEQUENCE eip_t_timecard_settings_timecard_settings_id_seq RENAME TO pk_eip_t_timecard_settings ;
ALTER SEQUENCE eip_t_todo_todo_id_seq RENAME TO pk_eip_t_todo ;
ALTER SEQUENCE eip_t_todo_category_category_id_seq RENAME TO pk_eip_t_todo_category ;
ALTER SEQUENCE eip_t_whatsnew_whatsnew_id_seq RENAME TO pk_eip_t_whatsnew ;
ALTER SEQUENCE eip_t_workflow_category_category_id_seq RENAME TO pk_eip_t_workflow_category ;
ALTER SEQUENCE eip_t_workflow_file_file_id_seq RENAME TO pk_eip_t_workflow_file ;
ALTER SEQUENCE eip_t_workflow_request_request_id_seq RENAME TO pk_eip_t_workflow_request ;
ALTER SEQUENCE eip_t_workflow_request_map_id_seq RENAME TO pk_eip_t_workflow_request_map ;
ALTER SEQUENCE eip_t_workflow_route_route_id_seq RENAME TO pk_eip_t_workflow_route ;
ALTER SEQUENCE turbine_group_group_id_seq RENAME TO pk_turbine_group ;
ALTER SEQUENCE turbine_permission_permission_id_seq RENAME TO pk_turbine_permission ;
ALTER SEQUENCE turbine_role_role_id_seq RENAME TO pk_turbine_role ;
ALTER SEQUENCE turbine_user_group_role_id_seq RENAME TO pk_turbine_user_group_role ;
ALTER SEQUENCE turbine_user_user_id_seq RENAME TO pk_turbine_user ;

ALTER TABLE aipo_license ALTER license_id DROP DEFAULT;
ALTER TABLE aipo_license ALTER license_id DROP DEFAULT;
ALTER TABLE eip_m_address_group ALTER group_id DROP DEFAULT;
ALTER TABLE eip_m_addressbook_company ALTER company_id DROP DEFAULT;
ALTER TABLE eip_m_company ALTER company_id DROP DEFAULT;
ALTER TABLE eip_m_mail_account ALTER account_id DROP DEFAULT;
ALTER TABLE eip_m_mail_notify_conf ALTER notify_id DROP DEFAULT;
ALTER TABLE eip_m_position ALTER position_id DROP DEFAULT;
ALTER TABLE eip_m_post ALTER post_id DROP DEFAULT;
ALTER TABLE eip_m_user_position ALTER id DROP DEFAULT;
ALTER TABLE eip_m_addressbook ALTER address_id DROP DEFAULT;
ALTER TABLE eip_t_acl_user_role_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_addressbook_group_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_acl_portlet_feature ALTER feature_id DROP DEFAULT;
ALTER TABLE eip_t_acl_role ALTER role_id DROP DEFAULT;
ALTER TABLE eip_t_mail_filter ALTER filter_id DROP DEFAULT;
ALTER TABLE eip_t_mail_folder ALTER folder_id DROP DEFAULT;
ALTER TABLE eip_t_mail ALTER mail_id DROP DEFAULT;
ALTER TABLE eip_t_ext_timecard ALTER timecard_id DROP DEFAULT;
ALTER TABLE eip_t_memo ALTER memo_id DROP DEFAULT;
ALTER TABLE eip_t_timecard_settings ALTER timecard_settings_id DROP DEFAULT;
ALTER TABLE eip_t_timecard ALTER timecard_id DROP DEFAULT;
ALTER TABLE eip_t_whatsnew ALTER whatsnew_id DROP DEFAULT;
ALTER TABLE eip_facility_group ALTER id DROP DEFAULT;
ALTER TABLE eip_t_blog_thema ALTER thema_id DROP DEFAULT;
ALTER TABLE eip_t_blog_comment ALTER comment_id DROP DEFAULT;
ALTER TABLE eip_t_blog_file ALTER file_id DROP DEFAULT;
ALTER TABLE eip_m_facility ALTER facility_id DROP DEFAULT;
ALTER TABLE eip_t_blog_footmark_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_blog ALTER blog_id DROP DEFAULT;
ALTER TABLE eip_t_common_category ALTER common_category_id DROP DEFAULT;
ALTER TABLE eip_t_cabinet_folder_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_schedule ALTER schedule_id DROP DEFAULT;
ALTER TABLE eip_t_ext_timecard_system_map ALTER system_map_id DROP DEFAULT;
ALTER TABLE eip_t_ext_timecard_system ALTER system_id DROP DEFAULT;
ALTER TABLE eip_t_msgboard_category_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_todo_category ALTER category_id DROP DEFAULT;
ALTER TABLE eip_t_todo ALTER todo_id DROP DEFAULT;
ALTER TABLE eip_t_note ALTER note_id DROP DEFAULT;
ALTER TABLE eip_t_msgboard_category ALTER category_id DROP DEFAULT;
ALTER TABLE eip_t_note_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_msgboard_topic ALTER topic_id DROP DEFAULT;
ALTER TABLE eip_t_msgboard_file ALTER file_id DROP DEFAULT;
ALTER TABLE eip_t_schedule_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_blog_entry ALTER entry_id DROP DEFAULT;
ALTER TABLE eip_t_cabinet_folder ALTER folder_id DROP DEFAULT;
ALTER TABLE eip_t_cabinet_file ALTER file_id DROP DEFAULT;
ALTER TABLE eip_t_workflow_file ALTER file_id DROP DEFAULT;
ALTER TABLE turbine_permission ALTER permission_id DROP DEFAULT;
ALTER TABLE turbine_role_permission ALTER permission_id DROP DEFAULT;
ALTER TABLE turbine_role_permission ALTER role_id DROP DEFAULT;
ALTER TABLE turbine_group ALTER group_id DROP DEFAULT;
ALTER TABLE turbine_role ALTER role_id DROP DEFAULT;
ALTER TABLE turbine_user_group_role ALTER id DROP DEFAULT;
ALTER TABLE turbine_user ALTER user_id DROP DEFAULT;
ALTER TABLE eip_t_workflow_route ALTER route_id DROP DEFAULT;
ALTER TABLE eip_t_workflow_category ALTER category_id DROP DEFAULT;
ALTER TABLE eip_t_workflow_request ALTER request_id DROP DEFAULT;
ALTER TABLE eip_t_workflow_request_map ALTER id DROP DEFAULT;
ALTER TABLE eip_t_eventlog ALTER eventlog_id DROP DEFAULT;

CREATE TABLE jetspeed_group_profile (
    COUNTRY varchar(2) NULL,
    GROUP_NAME varchar(99) NULL,
    LANGUAGE varchar(2) NULL,
    MEDIA_TYPE varchar(99) NULL,
    PAGE varchar(99) NULL,
    PROFILE bytea NULL,
    PSML_ID integer NOT NULL,
    PRIMARY KEY (PSML_ID)
)
;

CREATE TABLE jetspeed_user_profile (
    COUNTRY varchar(2) NULL,
    LANGUAGE varchar(2) NULL,
    MEDIA_TYPE varchar(99) NULL,
    PAGE varchar(99) NULL,
    PROFILE bytea NULL,
    PSML_ID integer NOT NULL,
    USER_NAME varchar(32) NULL,
    PRIMARY KEY (PSML_ID)
)
;

CREATE TABLE jetspeed_role_profile (
    COUNTRY varchar(2) NULL,
    LANGUAGE varchar(2) NULL,
    MEDIA_TYPE varchar(99) NULL,
    PAGE varchar(99) NULL,
    PROFILE bytea NULL,
    PSML_ID integer NOT NULL,
    ROLE_NAME varchar(99) NULL,
    PRIMARY KEY (PSML_ID)
)
;

CREATE TABLE eip_m_config (
    ID integer NOT NULL,
    NAME varchar(64) NULL,
    VALUE varchar(255) NULL,
    PRIMARY KEY (ID)
)
;

CREATE SEQUENCE pk_eip_m_config INCREMENT 20 START 200
;

CREATE SEQUENCE pk_jetspeed_group_profile INCREMENT 20 START 200
;

CREATE SEQUENCE pk_jetspeed_role_profile INCREMENT 20 START 200
;

CREATE SEQUENCE pk_jetspeed_user_profile INCREMENT 20 START 200
;



CREATE TABLE application (
    APP_ID varchar(255) NOT NULL,
    CONSUMER_KEY varchar(99) NULL,
    CONSUMER_SECRET varchar(99) NULL,
    CREATE_DATE date NULL,
    DESCRIPTION text NULL,
    ICON varchar(255) NULL,
    ICON64 varchar(255) NULL,
    ID integer NOT NULL,
    STATUS integer NULL,
    SUMMARY varchar(255) NULL,
    TITLE varchar(99) NULL,
    UPDATE_DATE timestamp with time zone NULL,
    URL varchar(255) NOT NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE activity (
    APP_ID varchar(255) NOT NULL,
    BODY text NULL,
    EXTERNAL_ID varchar(99) NULL,
    ICON varchar(255) NULL,
    ID integer NOT NULL,
    LOGIN_NAME varchar(32) NOT NULL,
    MODULE_ID integer NOT NULL,
    PORTLET_PARAMS varchar(99) NULL,
    PRIORITY float NULL,
    TITLE varchar(99) NOT NULL,
    UPDATE_DATE timestamp with time zone NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE oauth_token (
    ACCESS_TOKEN varchar(255) NULL,
    ID integer NOT NULL,
    SESSION_HANDLE varchar(255) NULL,
    TOKEN_EXPIRE_MILIS integer NULL,
    TOKEN_SECRET varchar(255) NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE oauth_entry (
    APP_ID varchar(255) NULL,
    AUTHORIZED integer NULL,
    CALLBACK_TOKEN varchar(255) NULL,
    CALLBACK_TOKEN_ATTEMPTS integer NULL,
    CALLBACK_URL varchar(255) NULL,
    CALLBACK_URL_SIGNED integer NULL,
    CONSUMER_KEY varchar(255) NULL,
    CONTAINER varchar(32) NULL,
    DOMAIN varchar(255) NULL,
    ID integer NOT NULL,
    ISSUE_TIME timestamp with time zone NULL,
    OAUTH_VERSION varchar(16) NULL,
    TOKEN varchar(255) NULL,
    TOKEN_SECRET varchar(255) NULL,
    TYPE varchar(32) NULL,
    USER_ID varchar(64) NULL,
    PRIMARY KEY (ID)
)
;



CREATE TABLE oauth_consumer (
    APP_ID integer NULL,
    CONSUMER_KEY varchar(255) NULL,
    CONSUMER_SECRET varchar(255) NULL,
    CREATE_DATE date NULL,
    ID integer NOT NULL,
    NAME varchar(99) NULL,
    TYPE varchar(99) NULL,
    UPDATE_DATE timestamp with time zone NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE container_config (
    ID integer NOT NULL,
    NAME varchar(64) NOT NULL,
    VALUE varchar(255) NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE activity_map (
    ACTIVITY_ID integer NULL,
    ID integer NOT NULL,
    IS_READ integer NULL,
    LOGIN_NAME varchar(32) NOT NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE app_data (
    APP_ID varchar(255) NOT NULL,
    ID integer NOT NULL,
    NAME varchar(99) NOT NULL,
    LOGIN_NAME varchar(32) NOT NULL,
    VALUE text NULL,
    PRIMARY KEY (ID)
)
;

CREATE TABLE module_id (
    ID integer NOT NULL,
    PRIMARY KEY (ID)
)
;

ALTER TABLE oauth_consumer ADD FOREIGN KEY (APP_ID) REFERENCES application (ID) ON DELETE CASCADE
;

ALTER TABLE activity_map ADD FOREIGN KEY (ACTIVITY_ID) REFERENCES activity (ID) ON DELETE CASCADE
;

CREATE SEQUENCE pk_activity INCREMENT 20 START 200
;

CREATE SEQUENCE pk_activity_map INCREMENT 20 START 200
;

CREATE SEQUENCE pk_app_data INCREMENT 20 START 200
;

CREATE SEQUENCE pk_application INCREMENT 20 START 200
;

CREATE SEQUENCE pk_container_config INCREMENT 20 START 200
;

CREATE SEQUENCE pk_module_id INCREMENT 20 START 200
;

CREATE SEQUENCE pk_oauth_consumer INCREMENT 20 START 200
;

CREATE SEQUENCE pk_oauth_token INCREMENT 20 START 200
;

CREATE SEQUENCE pk_oauth_entry INCREMENT 20 START 200
;

-----------------------------------------------------------------------------
-- DELETE EIP_M_MYBOX
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS EIP_M_MYBOX;


