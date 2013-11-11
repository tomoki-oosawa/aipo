-- 20130930
ALTER TABLE `turbine_user` ADD `migrate_version` int(11) NOT NULL DEFAULT 0 AFTER `tutorial_forbid`;
UPDATE `turbine_user` SET `migrate_version` = 0 ;
ALTER TABLE `turbine_user` CHANGE COLUMN `tutorial_forbid` `tutorial_forbid` varchar(64) COLLATE utf8_unicode_ci DEFAULT 'F';
-- 20130930

-- 20131111
CREATE TABLE `eip_m_gpdb_kubun` (
  `gpdb_kubun_id` int(11) NOT NULL AUTO_INCREMENT,
  `gpdb_kubun_name` text COLLATE utf8_unicode_ci NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`gpdb_kubun_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `eip_m_gpdb_kubun_value` (
  `gpdb_kubun_value_id` int(11) NOT NULL AUTO_INCREMENT,
  `gpdb_kubun_id` int(11) NOT NULL,
  `gpdb_kubun_value` text COLLATE utf8_unicode_ci NOT NULL,
  `order_no` int(11) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`gpdb_kubun_value_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `eip_t_gpdb` (
  `gpdb_id` int(11) NOT NULL AUTO_INCREMENT,
  `gpdb_name` text COLLATE utf8_unicode_ci NOT NULL,
  `mail_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `create_user_id` int(11) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`gpdb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `eip_t_gpdb_item` (
  `gpdb_item_id` int(11) NOT NULL AUTO_INCREMENT,
  `gpdb_id` int(11) NOT NULL,
  `gpdb_item_name` text COLLATE utf8_unicode_ci NOT NULL,
  `title_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `required_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(2) COLLATE utf8_unicode_ci NOT NULL,
  `gpdb_kubun_id` int(11) DEFAULT NULL,
  `list_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `detail_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `size_col` int(11) DEFAULT NULL,
  `size_row` int(11) DEFAULT NULL,
  `line` int(11) DEFAULT NULL,
  `order_no` int(11) NOT NULL,
  `default_sort_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `asc_desc` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_user_id` int(11) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`gpdb_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 20131111
CREATE TABLE `eip_t_gpdb_record` (
  `gpdb_record_id` int(11) NOT NULL AUTO_INCREMENT,
  `gpdb_id` int(11) NOT NULL,
  `gpdb_item_id` int(11) NOT NULL,
  `record_no` int(11) NOT NULL,
  `value` text COLLATE utf8_unicode_ci,
  `create_user_id` int(11) NOT NULL,
  `update_user_id` int(11) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`gpdb_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `eip_t_gpdb_record_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) DEFAULT NULL,
  `gpdb_record_id` int(11) DEFAULT NULL,
  `file_name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `file_path` text COLLATE utf8_unicode_ci NOT NULL,
  `file_thumbnail` blob,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;