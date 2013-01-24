-- 20130124
UPDATE `eip_t_acl_portlet_feature` SET `feature_alias_name` = 'ユーザー名簿操作' WHERE `feature_name` = 'addressbook_address_inside' AND `feature_alias_name` = 'アドレス帳（社内アドレス）操作';
UPDATE `eip_t_acl_role` SET `role_name` = 'ユーザー名簿管理者' WHERE feature_id IN (SELECT feature_id FROM eip_t_acl_portlet_feature WHERE feature_name = 'addressbook_address_inside') AND `role_name` = 'アドレス帳（社内アドレス）管理者';
-- 20130124