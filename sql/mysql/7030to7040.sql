-- 20130930
ALTER TABLE `turbine_user` ADD `migrate_version` int(11) NOT NULL DEFAULT 0 AFTER `tutorial_forbid`;
UPDATE `turbine_user` SET `migrate_version` = 0 ;
-- 20130930