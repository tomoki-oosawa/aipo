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
ALTER TABLE TURBINE_USER ADD CODE VARCHAR(255) DEFAULT NULL;

ALTER TABLE EIP_T_MESSAGE_ROOM_MEMBER ADD DESKTOP_NOTIFICATION VARCHAR(1) DEFAULT 'A';
ALTER TABLE EIP_T_MESSAGE_ROOM_MEMBER ADD MOBILE_NOTIFICATION VARCHAR(1) DEFAULT 'A';

ALTER TABLE EIP_T_MESSAGE_ROOM_MEMBER ADD HISTORY_LAST_MESSAGE_ID INTEGER NOT NULL DEFAULT 0;

ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD OVERTIME_TYPE VARCHAR(8) DEFAULT 'O';
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET OVERTIME_TYPE = 'O';
-- 20160328
