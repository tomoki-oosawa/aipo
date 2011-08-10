--- modify table struct
ALTER TABLE eip_t_schedule ADD COLUMN mail_flag CHAR(1) AFTER edit_flag;

--- update data
UPDATE eip_t_schedule SET mail_flag = 'A' ;
