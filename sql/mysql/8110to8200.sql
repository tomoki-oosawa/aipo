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

ALTER TABLE `eip_t_ext_timecard_system` ADD `holiday_of_week` varchar(32) DEFAULT 'A' AFTER `overtime_type`;
-- 20160328

-- 20160815
UPDATE `eip_t_acl_portlet_feature` SET `acl_type` = 27 WHERE `feature_name` = 'report_other' AND `feature_alias_name` = '報告書（他ユーザーの報告書）操作';
UPDATE `eip_t_acl_role` SET `acl_type` = 27 WHERE feature_id IN (SELECT feature_id FROM eip_t_acl_portlet_feature WHERE feature_name = 'report_other') AND `role_name` = '報告書（他ユーザーの報告書）管理者';
-- 20160815
