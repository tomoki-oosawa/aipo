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

-- 20120706
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 41 WHERE FEATURE_ID = 172 AND FEATURE_NAME = 'timecard_timecard_other';
-- 20120706

-- 20120711
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 45 WHERE FEATURE_ID = 172 AND FEATURE_NAME = 'timecard_timecard_other';
-- 20120711

-- 20120724
ALTER TABLE activity ALTER COLUMN title TYPE character varying(255) NOT NULL;
-- 20120724

-- 20120807
ALTER TABLE turbine_user  ADD PHOTO_SMARTPHONE bytea;
ALTER TABLE turbine_user  ADD HAS_PHOTO_SMARTPHONE  VARCHAR (1) DEFAULT 'F';
ALTER TABLE turbine_user  ADD PHOTO_MODIFIED_SMARTPHONE TIMESTAMP;
-- 20120807
