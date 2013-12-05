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

ALTER TABLE `eip_t_gpdb_record` ADD INDEX (`record_no`);

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


INSERT INTO eip_m_gpdb_kubun VALUES (1, '都道府県', now(), now());

INSERT INTO eip_m_gpdb_kubun_value VALUES (1, 1, '北海道', 1, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (2, 1, '青森県', 2, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (3, 1, '岩手県', 3, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (4, 1, '宮城県', 4, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (5, 1, '秋田県', 5, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (6, 1, '山形県', 6, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (7, 1, '福島県', 7, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (8, 1, '茨城県', 8, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (9, 1, '栃木県', 9, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (10, 1, '群馬県', 10, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (11, 1, '埼玉県', 11, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (12, 1, '千葉県', 12, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (13, 1, '東京都', 13, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (14, 1, '神奈川県', 14, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (15, 1, '新潟県', 15, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (16, 1, '富山県', 16, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (17, 1, '石川県', 17, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (18, 1, '福井県', 18, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (19, 1, '山梨県', 19, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (20, 1, '長野県', 20, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (21, 1, '岐阜県', 21, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (22, 1, '静岡県', 22, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (23, 1, '愛知県', 23, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (24, 1, '三重県', 24, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (25, 1, '滋賀県', 25, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (26, 1, '京都府', 26, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (27, 1, '大阪府', 27, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (28, 1, '兵庫県', 28, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (29, 1, '奈良県', 29, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (30, 1, '和歌山県', 30, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (31, 1, '鳥取県', 31, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (32, 1, '島根県', 32, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (33, 1, '岡山県', 33, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (34, 1, '広島県', 34, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (35, 1, '山口県', 35, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (36, 1, '徳島県', 36, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (37, 1, '香川県', 37, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (38, 1, '愛媛県', 38, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (39, 1, '高知県', 39, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (40, 1, '福岡県', 40, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (41, 1, '佐賀県', 41, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (42, 1, '長崎県', 42, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (43, 1, '熊本県', 43, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (44, 1, '大分県', 44, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (45, 1, '宮崎県', 45, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (46, 1, '鹿児島県', 46, now(), now());
INSERT INTO eip_m_gpdb_kubun_value VALUES (47, 1, '沖縄県', 47, now(), now());