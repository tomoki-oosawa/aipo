--- modify table struct
ALTER TABLE eip_t_schedule ADD COLUMN mail_flag CHAR(1) AFTER edit_flag;
ALTER TABLE eip_t_cabinet_file ADD COLUMN counter INTEGER(11) AFTER note;

--- update data
UPDATE eip_t_schedule SET mail_flag = 'A' ;
UPDATE eip_m_addressbook_company SET company_name = '', company_name_kana = '' WHERE company_id = 1;

