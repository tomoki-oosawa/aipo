--
-- Aipo is a groupware program developed by Aimluck,Inc.
-- Copyright (C) 2004-2011 Aimluck,Inc.
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

CREATE SEQUENCE pk_aipo_license INCREMENT 20;
SELECT setval('pk_aipo_license',(SELECT max(license_id) FROM aipo_license));

CREATE SEQUENCE pk_eip_facility_group INCREMENT 20;
SELECT setval('pk_eip_facility_group',(SELECT max(id) FROM eip_facility_group));

CREATE SEQUENCE pk_eip_m_address_group INCREMENT 20;
SELECT setval('pk_eip_m_address_group',(SELECT max(group_id) FROM eip_m_address_group));

CREATE SEQUENCE pk_eip_m_addressbook INCREMENT 20;
SELECT setval('pk_eip_m_addressbook',(SELECT max(address_id) FROM eip_m_addressbook));

CREATE SEQUENCE pk_eip_m_addressbook_company INCREMENT 20;
SELECT setval('pk_eip_m_addressbook_company',(SELECT max(company_id) FROM eip_m_addressbook_company));

CREATE SEQUENCE pk_eip_m_company INCREMENT 20;
SELECT setval('pk_eip_m_company',(SELECT max(company_id) FROM eip_m_company));

CREATE SEQUENCE pk_eip_m_facility INCREMENT 20;
SELECT setval('pk_eip_m_facility',(SELECT max(facility_id) FROM eip_m_facility));

CREATE SEQUENCE pk_eip_m_mail_account INCREMENT 20;
SELECT setval('eip_m_mail_account_account_id_seq',(SELECT max(account_id) FROM eip_m_mail_account));

CREATE SEQUENCE pk_eip_m_mail_notify_conf INCREMENT 20;
SELECT setval('eip_m_mail_notify_conf_notify_id_seq',(SELECT max(notify_id) FROM eip_m_mail_notify_conf));

CREATE SEQUENCE pk_eip_m_mybox INCREMENT 20;
SELECT setval('eip_m_mybox_mybox_id_seq',(SELECT max(mybox_id) FROM eip_m_mybox));

CREATE SEQUENCE pk_eip_m_position INCREMENT 20;
SELECT setval('eip_m_position_position_id_seq',(SELECT max(position_id) FROM eip_m_position));

CREATE SEQUENCE pk_eip_m_post INCREMENT 20;
SELECT setval('eip_m_post_post_id_seq',(SELECT max(post_id) FROM eip_m_post));

CREATE SEQUENCE pk_eip_m_user_position INCREMENT 20;
SELECT setval('eip_m_user_position_id_seq',(SELECT max(id) FROM eip_m_user_position));

CREATE SEQUENCE pk_eip_t_acl_portlet_feature INCREMENT 20;
SELECT setval('eip_t_acl_portlet_feature_feature_id_seq',(SELECT max(feature_id) FROM eip_t_acl_portlet_feature));

CREATE SEQUENCE pk_eip_t_acl_role INCREMENT 20;
SELECT setval('eip_t_acl_role_role_id_seq',(SELECT max(role_id) FROM eip_t_acl_role));

CREATE SEQUENCE pk_eip_t_acl_user_role_map INCREMENT 20;
SELECT setval('eip_t_acl_user_role_map_id_seq',(SELECT max(id) FROM eip_t_acl_user_role_map));

CREATE SEQUENCE pk_eip_t_addressbook_group_map INCREMENT 20;
SELECT setval('eip_t_addressbook_group_map_id_seq',(SELECT max(id) FROM eip_t_addressbook_group_map));

CREATE SEQUENCE pk_eip_t_blog INCREMENT 20;
SELECT setval('eip_t_blog_blog_id_seq',(SELECT max(blog_id) FROM eip_t_blog));

CREATE SEQUENCE pk_eip_t_blog_comment INCREMENT 20;
SELECT setval('eip_t_blog_comment_comment_id_seq',(SELECT max(comment_id) FROM eip_t_blog_comment));

CREATE SEQUENCE pk_eip_t_blog_entry INCREMENT 20;
SELECT setval('pk_eip_t_blog_entry',(SELECT max(entry_id) FROM eip_t_blog_entry));

CREATE SEQUENCE pk_eip_t_blog_file INCREMENT 20;
SELECT setval('pk_eip_t_blog_file',(SELECT max(file_id) FROM eip_t_blog_file));

CREATE SEQUENCE pk_eip_t_blog_footmark_map INCREMENT 20;
SELECT setval('pk_eip_t_blog_footmark_map',(SELECT max(id) FROM eip_t_blog_footmark_map));

CREATE SEQUENCE pk_eip_t_blog_thema INCREMENT 20;
SELECT setval('pk_eip_t_blog_thema',(SELECT max(thema_id) FROM eip_t_blog_thema));

CREATE SEQUENCE pk_eip_t_cabinet_file INCREMENT 20;
SELECT setval('pk_eip_t_cabinet_file',(SELECT max(file_id) FROM eip_t_cabinet_file));

CREATE SEQUENCE pk_eip_t_cabinet_folder INCREMENT 20;
SELECT setval('pk_eip_t_cabinet_folder',(SELECT max(folder_id) FROM eip_t_cabinet_folder));

CREATE SEQUENCE pk_eip_t_cabinet_folder_map INCREMENT 20;
SELECT setval('pk_eip_t_cabinet_folder_map',(SELECT max(id) FROM eip_t_cabinet_folder_map));

CREATE SEQUENCE pk_eip_t_common_category INCREMENT 20;
SELECT setval('eip_t_common_category_common_category_id_seq',(SELECT max(common_category_id) FROM eip_t_common_category));

CREATE SEQUENCE pk_eip_t_eventlog INCREMENT 20;
SELECT setval('pk_eip_t_eventlog',(SELECT max(eventlog_id) FROM eip_t_eventlog));

CREATE SEQUENCE pk_eip_t_ext_timecard INCREMENT 20;
SELECT setval('pk_eip_t_ext_timecard',(SELECT max(timecard_id) FROM eip_t_ext_timecard));

CREATE SEQUENCE pk_eip_t_ext_timecard_system INCREMENT 20;
SELECT setval('pk_eip_t_ext_timecard_system',(SELECT max(system_id) FROM eip_t_ext_timecard_system));

CREATE SEQUENCE pk_eip_t_ext_timecard_system_map INCREMENT 20;
SELECT setval('pk_eip_t_ext_timecard_system_map',(SELECT max(system_map_id) FROM eip_t_ext_timecard_system_map));

CREATE SEQUENCE pk_eip_t_mail INCREMENT 20;
SELECT setval('pk_eip_t_mail',(SELECT max(mail_id) FROM eip_t_mail));

CREATE SEQUENCE pk_eip_t_mail_filter INCREMENT 20;
SELECT setval('pk_eip_t_mail_filter',(SELECT max(filter_id) FROM eip_t_mail_filter));

CREATE SEQUENCE pk_eip_t_mail_folder INCREMENT 20;
SELECT setval('pk_eip_t_mail_folder',(SELECT max(folder_id) FROM eip_t_mail_folder));

CREATE SEQUENCE pk_eip_t_memo INCREMENT 20;
SELECT setval('pk_eip_t_memo',(SELECT max(memo_id) FROM eip_t_memo));

CREATE SEQUENCE pk_eip_t_msgboard_category INCREMENT 20;
SELECT setval('pk_eip_t_msgboard_category',(SELECT max(category_id) FROM eip_t_msgboard_category));

CREATE SEQUENCE pk_eip_t_msgboard_category_map INCREMENT 20;
SELECT setval('pk_eip_t_msgboard_category_map',(SELECT max(id) FROM eip_t_msgboard_category_map));

CREATE SEQUENCE pk_eip_t_msgboard_file INCREMENT 20;
SELECT setval('pk_eip_t_msgboard_file',(SELECT max(file_id) FROM eip_t_msgboard_file));

CREATE SEQUENCE pk_eip_t_msgboard_topic INCREMENT 20;
SELECT setval('pk_eip_t_msgboard_topic',(SELECT max(topic_id) FROM eip_t_msgboard_topic));

CREATE SEQUENCE pk_eip_t_note INCREMENT 20;
SELECT setval('pk_eip_t_note',(SELECT max(note_id) FROM eip_t_note));

CREATE SEQUENCE pk_eip_t_note_map INCREMENT 20;
SELECT setval('pk_eip_t_note_map',(SELECT max(id) FROM eip_t_note_map));

CREATE SEQUENCE pk_eip_t_schedule INCREMENT 20;
SELECT setval('pk_eip_t_schedule',(SELECT max(schedule_id) FROM eip_t_schedule));

CREATE SEQUENCE pk_eip_t_schedule_map INCREMENT 20;
SELECT setval('pk_eip_t_schedule_map',(SELECT max(id) FROM eip_t_schedule_map));

CREATE SEQUENCE pk_eip_t_timecard INCREMENT 20;
SELECT setval('pk_eip_t_timecard',(SELECT max(timecard_id) FROM eip_t_timecard));

CREATE SEQUENCE pk_eip_t_timecard_settings INCREMENT 20;
SELECT setval('pk_eip_t_timecard_settings',(SELECT max(timecard_settings_id) FROM eip_t_timecard_settings));

CREATE SEQUENCE pk_eip_t_todo INCREMENT 20;
SELECT setval('pk_eip_t_todo',(SELECT max(todo_id) FROM eip_t_todo));

CREATE SEQUENCE pk_eip_t_todo_category INCREMENT 20;
SELECT setval('pk_eip_t_todo_category',(SELECT max(category_id) FROM eip_t_todo_category));

CREATE SEQUENCE pk_eip_t_whatsnew INCREMENT 20;
SELECT setval('pk_eip_t_whatsnew',(SELECT max(whatsnew_id) FROM eip_t_whatsnew));

CREATE SEQUENCE pk_eip_t_workflow_category INCREMENT 20;
SELECT setval('pk_eip_t_workflow_category',(SELECT max(category_id) FROM eip_t_workflow_category));

CREATE SEQUENCE pk_eip_t_workflow_file INCREMENT 20;
SELECT setval('pk_eip_t_workflow_file',(SELECT max(file_id) FROM eip_t_workflow_file));

CREATE SEQUENCE pk_eip_t_workflow_request INCREMENT 20;
SELECT setval('pk_eip_t_workflow_request',(SELECT max(request_id) FROM eip_t_workflow_request));

CREATE SEQUENCE pk_eip_t_workflow_request_map INCREMENT 20;
SELECT setval('pk_eip_t_workflow_request_map',(SELECT max(id) FROM eip_t_workflow_request_map));

CREATE SEQUENCE pk_eip_t_workflow_route INCREMENT 20;
SELECT setval('pk_eip_t_workflow_route',(SELECT max(route_id) FROM eip_t_workflow_route));

CREATE SEQUENCE pk_turbine_group INCREMENT 20;
SELECT setval('pk_turbine_group',(SELECT max(group_id) FROM turbine_group));

CREATE SEQUENCE pk_turbine_permission INCREMENT 20;
SELECT setval('pk_turbine_permission',(SELECT max(permission_id) FROM turbine_permission));

CREATE SEQUENCE pk_turbine_role INCREMENT 20;
SELECT setval('pk_turbine_role',(SELECT max(role_id) FROM turbine_role));

CREATE SEQUENCE pk_turbine_user INCREMENT 20;
SELECT setval('pk_turbine_user',(SELECT max(user_id) FROM turbine_user));

CREATE SEQUENCE pk_turbine_user_group_role INCREMENT 20;
SELECT setval('pk_turbine_user_group_role',(SELECT max(id) FROM turbine_user_group_role));

ALTER TABLE aipo_license ALTER license_id DROP DEFAULT;
ALTER TABLE aipo_license ALTER license_id DROP DEFAULT;
ALTER TABLE eip_m_address_group ALTER group_id DROP DEFAULT;
ALTER TABLE eip_m_addressbook_company ALTER company_id DROP DEFAULT;
ALTER TABLE eip_m_company ALTER company_id DROP DEFAULT;
ALTER TABLE eip_m_mail_account ALTER account_id DROP DEFAULT;
ALTER TABLE eip_m_mail_notify_conf ALTER notify_id DROP DEFAULT;
ALTER TABLE eip_m_mybox ALTER mybox_id DROP DEFAULT;
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
    ID integer NOT NULL,
    LOGIN_NAME varchar(32) NOT NULL,
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
