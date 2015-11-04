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

--- 20110905
--- modify table struct
ALTER TABLE EIP_T_SCHEDULE ADD MAIL_FLAG CHAR(1) ;
ALTER TABLE EIP_T_CABINET_FILE ADD COUNTER INTEGER ;

--- update data
UPDATE EIP_T_SCHEDULE SET MAIL_FLAG = 'A' ;
UPDATE EIP_M_ADDRESSBOOK_COMPANY SET COMPANY_NAME = '', COMPANY_NAME_KANA = '' WHERE COMPANY_ID = 1;
--- 20110905

--- 20110909
--- create new table
CREATE TABLE EIP_M_FACILITY_GROUP
(
    GROUP_ID INTEGER NOT NULL,
    GROUP_NAME VARCHAR (64),
    PRIMARY KEY(GROUP_ID)
);

CREATE TABLE EIP_M_FACILITY_GROUP_MAP
(
    ID INTEGER NOT NULL,
    FACILITY_ID INTEGER,
    GROUP_ID INTEGER,
    PRIMARY KEY(ID)
);


--- create new sequence
CREATE SEQUENCE pk_eip_m_facility_group INCREMENT 20;
CREATE SEQUENCE pk_eip_m_facility_group_map INCREMENT 20;

--- alter sequence
ALTER SEQUENCE pk_eip_m_facility_group OWNED BY EIP_M_FACILITY_GROUP.GROUP_ID;
ALTER SEQUENCE pk_eip_m_facility_group_map OWNED BY EIP_M_FACILITY_GROUP_MAP.ID;
--- 20110909

--- 20110915
--- modify table struct
ALTER TABLE EIP_M_FACILITY ADD SORT INTEGER ;
--- 20110915

--- 20111007
CREATE INDEX eip_t_schedule_map_schedule_id_index ON EIP_T_SCHEDULE_MAP (SCHEDULE_ID);
CREATE INDEX eip_t_schedule_map_schedule_id_user_id_index ON EIP_T_SCHEDULE_MAP (SCHEDULE_ID, USER_ID);
--- 20111007