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

-- 20110905
-- modify table struct
ALTER TABLE `eip_t_schedule` ADD `mail_flag` CHAR(1) ;
ALTER TABLE `eip_t_cabinet_file` ADD `counter` INTEGER ;

-- update data
UPDATE `eip_t_schedule` SET `mail_flag` = 'A' ;
UPDATE `eip_m_addressbook_company` SET `company_name` = '', `company_name_kana` = '' WHERE `company_id` = 1;
-- 20110905

-- 20110909
-- create new table
CREATE TABLE eip_m_facility_group
(
    `group_id` int(11) NOT NULL AUTO_INCREMENT,
    `group_name` varchar (64) COLLATE utf8_unicode_ci,
    PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE eip_m_facility_group_map
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `facility_id` int(11),
    `group_id` int(11),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
-- 20110909

-- 20110913
ALTER TABLE `eip_m_facility` ADD `sort` INTEGER ;
-- 20110913

--- 20111007
CREATE INDEX eip_t_schedule_map_schedule_id_index ON eip_t_schedule_map (schedule_id);
CREATE INDEX eip_t_schedule_map_schedule_id_user_id_index ON eip_t_schedule_map (schedule_id, user_id);
DROP INDEX schedule_id ON eip_t_schedule_map;
--- 20111007