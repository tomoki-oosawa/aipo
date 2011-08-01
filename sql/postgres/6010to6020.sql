-- reload from psml files for admin and anon and template
DELETE FROM jetspeed_user_profile WHERE user_name = 'admin' or user_name = 'anon' or user_name = 'template';