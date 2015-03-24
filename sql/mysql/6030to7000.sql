--
-- Aipo is a groupware program developed by Aimluck,Inc.
-- Copyright (C) 2004-2015 Aimluck,Inc.
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

--20120214
CREATE TABLE `eip_t_timeline` (
   `timeline_id` int(11) NOT NULL AUTO_INCREMENT,
   `parent_id` int(11) NOT NULL DEFAULT 0,
   `owner_id` int(11),
   `note` text,
   `create_date` datetime DEFAULT NULL,
   `update_date` datetime DEFAULT NULL,
   PRIMARY KEY(`timeline_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
ALTER TABLE `eip_t_timeline` ADD FOREIGN KEY (  `owner_id` ) REFERENCES  `turbine_user` (`user_id`);
--20120214

--20120229
CREATE TABLE `eip_t_timeline_like` (
  `timeline_like_id` int(11) NOT NULL AUTO_INCREMENT,
  `timeline_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  PRIMARY KEY (`timeline_like_id`),
  UNIQUE KEY `eip_t_timeline_timelineid_ownerid_key` (`timeline_id`, `owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
--20120229

--20120307
ALTER TABLE `eip_t_ext_timecard_system` ADD COLUMN `start_day` smallint;
UPDATE `eip_t_ext_timecard_system` SET start_day=1;

CREATE TABLE `eip_t_timeline_file`
(
    `file_id` int(11) NOT NULL AUTO_INCREMENT,
    `owner_id` int(11),
    `timeline_id` int(11),
    `file_name` varchar(128) NOT NULL,
    `file_path` text NOT NULL,
    `file_thumbnail` blob,
    `create_date` date DEFAULT NULL,
    `update_date` datetime DEFAULT NULL,
    FOREIGN KEY (`timeline_id`) REFERENCES `eip_t_timeline` (`timeline_id`) ON DELETE CASCADE,
    PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--20120307

-- 20120314
UPDATE `eip_t_acl_portlet_feature` SET `feature_alias_name` = 'アプリ配置' WHERE `feature_name` = 'portlet_customize' AND `feature_alias_name` = 'ポートレット操作';
UPDATE `eip_t_acl_role` SET `role_name` = 'アプリ配置管理者' WHERE feature_id IN (SELECT feature_id FROM eip_t_acl_portlet_feature WHERE feature_name = 'portlet_customize') AND `role_name` = 'ポートレット管理者';
-- 20120314

-- 20120321
CREATE TABLE `eip_t_timeline_url`
(
    `url_id` int(11) NOT NULL AUTO_INCREMENT,
    `timeline_id` int(11),
    `url` varchar(128) NOT NULL,
    `body` text,
    `title` varchar(128),
    `thumbnail` varchar(128),
    FOREIGN KEY (`timeline_id`) REFERENCES `eip_t_timeline` (`timeline_id`) ON DELETE CASCADE,
    PRIMARY KEY (`url_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 20120321

-- 20120322
alter table `eip_t_timeline` add column `timeline_type` varchar (2);
alter table `eip_t_timeline` add column `params` varchar (99);

update eip_t_timeline set timeline_type='T' where (coalesce(timeline_type,'')='');
-- 20120322

-- 20120326

alter table `eip_t_timeline_url` drop `thumbnail`;
alter table `eip_t_timeline_url` add column `thumbnail` blob;

-- 20120326

-- 20120328

alter table `eip_t_timeline` add column `app_id` varchar(255) COLLATE utf8_unicode_ci;
alter table `eip_t_timeline` add column `external_id` varchar(99) COLLATE utf8_unicode_ci;

alter table `eip_t_timeline_like` add column `create_date` datetime DEFAULT NULL;
-- 20120328

-- 20120411

alter table eip_m_mail_account change column account_name account_name varchar(200) NOT NULL;
ALTER TABLE eip_t_timeline ADD COLUMN num_on_day INTEGER NOT NULL DEFAULT 0;

CREATE TABLE `eip_t_acl_map` (
  `acl_id` int(11) NOT NULL AUTO_INCREMENT,
  `target_id` int(11) NOT NULL,
  `target_type` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id` int(11) NOT NULL,
  `type` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`acl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 20120411

-- 20120418
ALTER TABLE  `eip_t_timeline` ADD INDEX (  `parent_id` );
-- 20120418

-- 20120423
CREATE TABLE `eip_t_timeline_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timeline_id` int(11) DEFAULT NULL,
  `is_read` int(11) DEFAULT NULL,
  `login_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE `eip_t_timeline_map` ADD FOREIGN KEY (`timeline_id`) REFERENCES  `eip_t_timeline` (`timeline_id`) ON DELETE CASCADE ;
-- 20120423

-- 20120518
ALTER TABLE `eip_t_timeline_url` modify `url` text not null;
ALTER TABLE `eip_m_config` ADD INDEX (`name`);
ALTER TABLE `container_config` ADD INDEX (`name`);
-- 20120518

-- 20120524
ALTER TABLE `turbine_user` ADD COLUMN `has_photo` varchar (1) DEFAULT 'F';
ALTER TABLE `turbine_user` ADD COLUMN `photo_modified` datetime DEFAULT NULL;
UPDATE `turbine_user` SET `photo_modified` = NOW();
UPDATE `turbine_user` SET `has_photo` = 'T' WHERE `photo` IS NOT NULL;
-- 20120524
