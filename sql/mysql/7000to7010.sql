-- 20120530
ALTER TABLE `turbine_user` ADD COLUMN `tutorial_forbid` VARCHAR (1) DEFAULT 'F';
UPDATE `turbine_user` SET `tutorial_forbid` = 'T' ;
-- 20120530
