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
