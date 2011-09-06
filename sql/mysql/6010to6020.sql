-- 20110905
-- modify table struct
ALTER TABLE `eip_t_schedule` ADD `mail_flag` CHAR(1) ;
ALTER TABLE `eip_t_cabinet_file` ADD `counter` INTEGER ;

-- update data
UPDATE `eip_t_schedule` SET `mail_flag` = 'A' ;
UPDATE `eip_m_addressbook_company` SET `company_name` = '', `company_name_kana` = '' WHERE `company_id` = 1;
-- 20110905