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
-----------------------------------------------------------------------------
-- EIP_T_SCHEDULE_FILE
-----------------------------------------------------------------------------

CREATE TABLE EIP_T_SCHEDULE_FILE
(
    FILE_ID INTEGER NOT NULL,
    OWNER_ID INTEGER,
    SCHEDULE_ID INTEGER,
    FILE_NAME VARCHAR (128) NOT NULL,
    FILE_PATH TEXT NOT NULL,
    FILE_THUMBNAIL bytea,
    CREATE_DATE DATE,
    UPDATE_DATE TIMESTAMP,
    FOREIGN KEY (SCHEDULE_ID) REFERENCES EIP_T_SCHEDULE (SCHEDULE_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILE_ID)
);

CREATE INDEX eip_t_file_schedule_id_index ON EIP_T_SCHEDULE_FILE (SCHEDULE_ID);

CREATE SEQUENCE pk_eip_t_schedule_file INCREMENT 20;

ALTER SEQUENCE pk_eip_t_schedule_file OWNED BY EIP_T_SCHEDULE_FILE.FILE_ID;