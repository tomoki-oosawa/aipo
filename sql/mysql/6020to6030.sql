-- 20111014
--- modify table struct
ALTER TABLE `eip_t_todo` ADD `create_user_id` INTEGER NOT NULL DEFAULT 0;
UPDATE `eip_t_todo` SET `create_user_id` = `user_id`;
-- 20111014

-- 20111019
-- change ACL settings
UPDATE eip_t_acl_portlet_feature SET acl_type = 31 WHERE feature_name = 'todo_todo_other';
UPDATE eip_t_acl_role SET acl_type = 31, note = '＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません' WHERE feature_id IN (SELECT feature_id FROM eip_t_acl_portlet_feature WHERE feature_name = 'todo_todo_other');

-- modify table struct
ALTER TABLE eip_t_todo_category ADD update_user_id INTEGER NOT NULL DEFAULT 0;
UPDATE eip_t_todo_category SET update_user_id = user_id;

-- add new ACL setting
INSERT INTO eip_t_acl_portlet_feature VALUES(null,'todo_category_other','ToDo（他ユーザのカテゴリ）操作',27);
INSERT INTO eip_t_acl_role VALUES(null, 'ToDo（他ユーザのカテゴリ）管理者', (SELECT feature_id from eip_t_acl_portlet_feature WHERE feature_name = 'todo_category_other' limit 1),27,'＊詳細表示、編集、削除は一覧表示の権限を持っていないと使用できません');
-- 20111019

-- 20111021
-- modify table struct
CREATE TABLE `eip_m_inactive_application` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
-- 20111021

-- 20111021
INSERT INTO eip_m_mail_notify_conf VALUES(null,1,25,3,NULL,now(),now());
-- 20111021