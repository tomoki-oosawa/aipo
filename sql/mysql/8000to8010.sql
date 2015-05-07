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

-- 20150507
CREATE TABLE `eip_t_schedule_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11),
  `schedule_id` int(11),
  `file_name` varchar(128) NOT NULL,
  `file_path` text NOT NULL,
  `file_thumbnail` blob,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  FOREIGN KEY (`schedule_id`) REFERENCES `eip_t_schedule` (`schedule_id`) ON DELETE CASCADE,
  PRIMARY KEY (`file_id`),
  KEY `eip_t_file_schedule_id_index` (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
