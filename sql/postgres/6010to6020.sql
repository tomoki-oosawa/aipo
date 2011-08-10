--- modify table struct
ALTER TABLE eip_t_schedule ADD MAIL_FLAG CHAR(1) ;

--- update data
UPDATE eip_t_schedule SET MAIL_FLAG = 'A' ;
