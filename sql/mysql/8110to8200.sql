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

-- 20160328
ALTER TABLE `turbine_user` ADD `code` varchar(255) DEFAULT NULL AFTER `migrate_version`;

ALTER TABLE `eip_t_message_room_member` ADD `desktop_notification` varchar(1) DEFAULT 'A' AFTER `authority`;
ALTER TABLE `eip_t_message_room_member` ADD `mobile_notification` varchar(1) DEFAULT 'A' AFTER `desktop_notification`;

ALTER TABLE `eip_t_message_room_member` ADD `history_last_message_id` int(11) NOT NULL DEFAULT 0 AFTER `mobile_notification`;

ALTER TABLE `eip_t_ext_timecard_system` ADD `overtime_type` varchar(8) DEFAULT 'O' AFTER `outgoing_add_flag`;
UPDATE `eip_t_ext_timecard_system` SET `overtime_type` = 'O';
-- 20160328

-- 20170123
-- timeline
ALTER TABLE `eip_t_timeline` ADD `pinned` varchar(1) DEFAULT 'F' AFTER `params`;
INSERT INTO eip_t_acl_portlet_feature VALUES(NULL,'timeline_pin','タイムライン（固定化）操作',8);
INSERT INTO eip_t_acl_role VALUES(NULL, 'タイムライン（固定化）管理者',(SELECT feature_id from eip_t_acl_portlet_feature WHERE feature_name = 'timeline_pin' LIMIT 1),8,NULL);
-- migration
INSERT INTO EIP_T_ACL_USER_ROLE_MAP(id,user_id,role_id) SELECT user_id,(SELECT ROLE_ID from eip_t_acl_role WHERE ROLE_NAME = 'タイムライン（固定化）管理者' LIMIT 1) FROM TURBINE_USER WHERE disabled='F' and not (login_name='admin' or login_name='anon');
-- 20170123