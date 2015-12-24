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

-- 20120706
UPDATE `eip_t_acl_portlet_feature` SET `acl_type` = 41 WHERE `feature_id` = 172 AND `feature_name` = 'timecard_timecard_other';
-- 20120706

-- 20120711
UPDATE `eip_t_acl_portlet_feature` SET `acl_type` = 45 WHERE `feature_id` = 172 AND `feature_name` = 'timecard_timecard_other';
-- 20120711

-- 20120724
ALTER TABLE activity CHANGE COLUMN title title varchar(255) NOT NULL;
-- 20120724

-- 20120807
ALTER TABLE `turbine_user`  ADD `photo_smartphone` blob;
ALTER TABLE `turbine_user`  ADD `has_photo_smartphone`  varchar(1) COLLATE utf8_unicode_ci DEFAULT 'F';
ALTER TABLE `turbine_user`  ADD `photo_modified_smartphone`  datetime DEFAULT NULL;
-- 20120807
