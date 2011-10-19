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
ALTER TABLE eip_t_todo ADD create_user_id INTEGER NOT NULL DEFAULT 0;
UPDATE eip_t_todo SET create_user_id = user_id;

-- add new ACL setting
INSERT INTO eip_t_acl_portlet_feature VALUES(null,'todo_category_other','ToDo（他ユーザのカテゴリ）操作',27);
INSERT INTO eip_t_acl_role VALUES(null, 'ToDo（他ユーザのカテゴリ）管理者', (SELECT feature_id from eip_t_acl_portlet_feature WHERE feature_name = 'todo_category_other' limit 1),27,'＊詳細表示、編集、削除は一覧表示の権限を持っていないと使用できません');
-- 20111019