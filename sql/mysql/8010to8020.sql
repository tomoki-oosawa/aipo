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

-- 20150609
DELETE FROM eip_t_timeline_like WHERE timeline_id NOT IN( SELECT timeline_id FROM eip_t_timeline );
ALTER TABLE eip_t_timeline_like ADD FOREIGN KEY (`timeline_id`) REFERENCES `eip_t_timeline` (`timeline_id`) ON DELETE CASCADE;
-- 20150609
