-- 20130930
ALTER TABLE `turbine_user` ADD `migrate_version` int(11) NOT NULL DEFAULT 0 AFTER `tutorial_forbid`;
UPDATE `turbine_user` SET `migrate_version` = 0 ;
ALTER TABLE `turbine_user` CHANGE COLUMN `tutorial_forbid` `tutorial_forbid` varchar(64) COLLATE utf8_unicode_ci DEFAULT 'F';
-- 20130930
