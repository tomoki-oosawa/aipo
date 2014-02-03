
--プロジェクト情報
DROP TABLE IF EXISTS eip_t_project;
CREATE TABLE eip_t_project (
      project_id         integer                        not null    --プロジェクトID
    , project_name       text                           not null    --プロジェクト名
    , explanation        text                                       --説明
    , admin_user_id      integer                        not null    --管理者
    , progress_flg       character varying(1)           not null    --進捗率入力フラグ 1:進捗率を入力する 0:タスクを元に自動計算する
    , progress_rate      integer                                    --進捗率
    , create_user_id     integer                        not null    --登録ユーザーID
    , update_user_id     integer                        not null    --更新ユーザーID
    , create_date        timestamp without time zone    not null    --登録日
    , update_date        timestamp without time zone    not null    --更新日
    , primary key (project_id)
);

--プロジェクトメンバー
DROP TABLE IF EXISTS eip_t_project_member;
CREATE TABLE eip_t_project_member (
      id          integer                        not null    --ID
    , project_id  integer                        not null    --プロジェクトID
    , user_id     integer                        not null    --ユーザーID
    , primary key (id)
);


--タスク情報
DROP TABLE IF EXISTS eip_t_project_task;
CREATE TABLE eip_t_project_task (
      task_id                integer                        not null    --タスクID
    , parent_task_id         integer                                    --親タスクID
    , project_id             integer                        not null    --プロジェクトID
    , tracker                text                           not null    --トラッカー 1:機能 2:バグ 3:サポート
    , task_name              text                           not null    --タスク名
    , explanation            text                                       --説明
    , status                 text                           not null    --ステータス 1:新規 2:進行中 3:解決 4:フィードバック 5:終了 6:却下
    , priority               text                           not null    --優先度 1:高 2:中 3:低
    , start_plan_date        date                                       --開始予定日
    , end_plan_date          date                                       --完了予定日
    , start_date             date                                       --開始実績日
    , end_date               date                                       --完了実績日
    , plan_workload          decimal                                    --計画工数（時間）
    , progress_rate          integer                                    --進捗率
    , order_no               integer                                    --表示順
    , create_user_id         integer                        not null    --登録ユーザーID
    , update_user_id         integer                        not null    --更新ユーザーID
    , create_date            timestamp without time zone    not null    --登録日
    , update_date            timestamp without time zone    not null    --更新日
    , primary key (task_id)
);

--タスクメンバー
DROP TABLE IF EXISTS eip_t_project_task_member;
CREATE TABLE eip_t_project_task_member (
      id              integer                not null    --ID
    , task_id         integer                not null    --タスクID
    , user_id         integer                not null    --ユーザーID
    , workload        decimal                not null    --工数
    , primary key (id)
);

--タスクコメント
DROP TABLE IF EXISTS eip_t_project_task_comment;
CREATE TABLE eip_t_project_task_comment (
      comment_id            integer                        not null    --コメントID
    , task_id               integer                        not null    --タスクID
    , comment               text                           not null    --コメント
    , create_user_id        integer                        not null    --登録ユーザーID
    , create_date           timestamp without time zone    not null    --登録日
    , update_date           timestamp without time zone    not null    --更新日
    , primary key (comment_id)
);

--プロジェクト情報添付ファイル
DROP TABLE IF EXISTS eip_t_project_file;
CREATE TABLE eip_t_project_file (
      file_id           integer                not null
    , owner_id          integer
    , project_id        integer
    , file_name         character varying(128) not null
    , file_path         text                   not null
    , file_thumbnail    bytea
    , create_date       date
    , update_date       timestamp without time zone
    , primary key (file_id)
);

--タスク情報添付ファイル
DROP TABLE IF EXISTS eip_t_project_task_file;
CREATE TABLE eip_t_project_task_file (
      file_id         integer                not null
    , owner_id        integer
    , task_id         integer
    , file_name       character varying(128) not null
    , file_path       text                   not null
    , file_thumbnail  bytea
    , create_date     date
    , update_date     timestamp without time zone
    , primary key (file_id)
);

--タスクコメント添付ファイル
DROP TABLE IF EXISTS eip_t_project_task_comment_file;
CREATE TABLE eip_t_project_task_comment_file (
      file_id         integer                not null
    , owner_id        integer
    , comment_id      integer
    , file_name       character varying(128) not null
    , file_path       text                   not null
    , file_thumbnail  bytea
    , create_date     date
    , update_date     timestamp without time zone
    , primary key (file_id)
);


--区分
DROP TABLE IF EXISTS eip_m_project_kubun;
CREATE TABLE eip_m_project_kubun (
      project_kubun_id     integer                        not null    --区分ID
    , project_kubun_cd     text                           not null    --区分コード
    , project_kubun_name   text                           not null    --区分名
    , create_date          timestamp without time zone                --登録日
    , update_date          timestamp without time zone                --更新日
    , primary key (project_kubun_id)
);


--区分値
DROP TABLE IF EXISTS eip_m_project_kubun_value;
CREATE TABLE eip_m_project_kubun_value (
      project_kubun_value_id  integer                        not null    --区分値ID
    , project_kubun_id        integer                        not null    --区分ID
    , project_kubun_value_cd  text                           not null    --区分値コード
    , project_kubun_value     text                           not null    --区分値
    , order_no                integer                        not null    --表示順
    , create_date             timestamp without time zone                --登録日
    , update_date             timestamp without time zone                --更新日
    , primary key (project_kubun_value_id)
);

--シーケンス
DROP SEQUENCE IF EXISTS pk_eip_t_project;
DROP SEQUENCE IF EXISTS pk_eip_t_project_task;
DROP SEQUENCE IF EXISTS pk_eip_t_project_task_comment;
DROP SEQUENCE IF EXISTS pk_eip_t_project_member;
DROP SEQUENCE IF EXISTS pk_eip_t_project_task_member;
DROP SEQUENCE IF EXISTS pk_eip_t_project_file;
DROP SEQUENCE IF EXISTS pk_eip_t_project_task_file;
DROP SEQUENCE IF EXISTS pk_eip_t_project_task_comment_file;
--DROP SEQUENCE IF EXISTS pk_eip_m_project_kubun;
--DROP SEQUENCE IF EXISTS pk_eip_m_project_kubun_value;
CREATE SEQUENCE pk_eip_t_project INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_comment INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_member INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_member INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_file INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_file INCREMENT 20;
CREATE SEQUENCE pk_eip_t_project_task_comment_file INCREMENT 20;
--CREATE SEQUENCE pk_eip_m_project_kubun INCREMENT 20;
--CREATE SEQUENCE pk_eip_m_project_kubun_value INCREMENT 20;

--区分マスタデータ
INSERT INTO eip_m_project_kubun(project_kubun_id, project_kubun_cd, project_kubun_name) VALUES(1,'tracker','トラッカー');
INSERT INTO eip_m_project_kubun(project_kubun_id, project_kubun_cd, project_kubun_name) VALUES(2,'status','ステータス');
INSERT INTO eip_m_project_kubun(project_kubun_id, project_kubun_cd, project_kubun_name) VALUES(3,'priority','優先度');
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(1,1,'1','機能',1);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(2,1,'2','バグ',2);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(3,1,'3','サポート',3);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(4,2,'1','新規',1);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(5,2,'2','進行中',2);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(6,2,'3','解決',3);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(7,2,'4','フィードバック',4);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(8,2,'5','終了',5);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(9,2,'6','却下',6);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(10,3,'1','高',1);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(11,3,'2','中',2);
INSERT INTO eip_m_project_kubun_value(project_kubun_value_id, project_kubun_id, project_kubun_value_cd, project_kubun_value, order_no) VALUES(12,3,'3','低',3);
