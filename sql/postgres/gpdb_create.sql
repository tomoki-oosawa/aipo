
--汎用DB
DROP TABLE IF EXISTS eip_t_gpdb;
CREATE TABLE eip_t_gpdb (
      gpdb_id            integer                        not null    --汎用DBID
    , gpdb_name          text                           not null    --汎用DB名
    , mail_flg           character varying(1)           not null    --メール配信フラグ 1:配信する 0:配信しない
    , create_user_id     integer                        not null    --登録ユーザーID
    , create_date        timestamp without time zone    not null    --登録日
    , update_date        timestamp without time zone    not null    --更新日
    , primary key (gpdb_id)
);


--汎用DB項目定義
DROP TABLE IF EXISTS eip_t_gpdb_item;
CREATE TABLE eip_t_gpdb_item (
      gpdb_item_id          integer                        not null    --汎用DB項目定義ID
    , gpdb_id               integer                        not null    --汎用DBID
    , gpdb_item_name        text                           not null    --汎用DB項目名
    , title_flg             character varying(1)           not null    --タイトルフラグ 1:タイトル 0:タイトルでない
    , required_flg          character varying(1)           not null    --必須フラグ 1:必須 0:必須でない
    , type                  character varying(2)           not null    --形式 01:テキスト
                                                                       --     02:テキストエリア
                                                                       --     03:リンク
                                                                       --     04:選択式（択一）
                                                                       --     05:選択式（複数）
                                                                       --     06:ファイル
                                                                       --     07:画像
                                                                       --     08:自動採番
                                                                       --     09:メール
                                                                       --     10:日付
                                                                       --     11:登録日時
                                                                       --     12:更新日時
                                                                       --     13:登録者
                                                                       --     14:更新者
    , gpdb_kubun_id         integer                                    --区分ID 選択式の場合に使用
    , list_flg              character varying(1)           not null    --一覧表示フラグ 1:表示する 0:表示しない
    , detail_flg            character varying(1)           not null    --詳細表示フラグ 1:表示する 0:表示しない
    , size_col              integer                                    --表示サイズ（横）
    , size_row              integer                                    --表示サイズ（縦）
    , line                  integer                                    --表示行数
    , order_no              integer                        not null    --表示順
    , default_sort_flg      character varying(1)           not null    --デフォルトソートフラグ 1:デフォルトソートに指定 0:デフォルトソートに指定しない
    , asc_desc              character varying(4)                       --ソート順(asc/desc)
    , create_user_id        integer                        not null    --登録ユーザーID
    , create_date           timestamp without time zone    not null    --登録日
    , update_date           timestamp without time zone    not null    --更新日
    , primary key (gpdb_item_id)
);


--汎用DBレコード
DROP TABLE IF EXISTS eip_t_gpdb_record;
CREATE TABLE eip_t_gpdb_record (
      gpdb_record_id     integer                        not null    --汎用DBレコードID
    , gpdb_id            integer                        not null    --汎用DBID
    , gpdb_item_id       integer                        not null    --汎用DB項目定義ID
    , record_no          integer                        not null    --レコードNo この数値が同じものをまとめて1レコードとする
    , value              text                                       --値
    , create_user_id     integer                        not null    --登録ユーザーID
    , update_user_id     integer                        not null    --更新ユーザーID
    , create_date        timestamp without time zone    not null    --登録日
    , update_date        timestamp without time zone    not null    --更新日
    , primary key (gpdb_record_id)
);

--汎用DB添付ファイル
DROP TABLE IF EXISTS eip_t_gpdb_record_file;
CREATE TABLE eip_t_gpdb_record_file (
      file_id        integer                not null
    , owner_id       integer
    , gpdb_record_id integer
    , file_name      character varying(128) not null
    , file_path      text                   not null
    , file_thumbnail bytea
    , create_date    date
    , update_date    timestamp without time zone
    , primary key (file_id)
);


--区分マスタ
DROP TABLE IF EXISTS eip_m_gpdb_kubun;
CREATE TABLE eip_m_gpdb_kubun (
      gpdb_kubun_id      integer                        not null    --区分ID
    , gpdb_kubun_name    text                           not null    --区分名
    , create_date        timestamp without time zone    not null    --登録日
    , update_date        timestamp without time zone    not null    --更新日
    , primary key (gpdb_kubun_id)
);


--区分値
DROP TABLE IF EXISTS eip_m_gpdb_kubun_value;
CREATE TABLE eip_m_gpdb_kubun_value (
      gpdb_kubun_value_id  integer                        not null    --区分値ID
    , gpdb_kubun_id        integer                        not null    --区分ID
    , gpdb_kubun_value     text                           not null    --区分値
    , order_no             integer                        not null    --表示順
    , create_date          timestamp without time zone    not null    --登録日
    , update_date          timestamp without time zone    not null    --更新日
    , primary key (gpdb_kubun_value_id)
);

--シーケンス
DROP SEQUENCE IF EXISTS pk_eip_t_gpdb;
DROP SEQUENCE IF EXISTS pk_eip_t_gpdb_item;
DROP SEQUENCE IF EXISTS pk_eip_t_gpdb_record;
DROP SEQUENCE IF EXISTS pk_eip_t_gpdb_record_file;
DROP SEQUENCE IF EXISTS pk_eip_m_gpdb_kubun;
DROP SEQUENCE IF EXISTS pk_eip_m_gpdb_kubun_value;
CREATE SEQUENCE pk_eip_t_gpdb INCREMENT 20;
CREATE SEQUENCE pk_eip_t_gpdb_item INCREMENT 20;
CREATE SEQUENCE pk_eip_t_gpdb_record INCREMENT 20;
CREATE SEQUENCE pk_eip_t_gpdb_record_file INCREMENT 20;
CREATE SEQUENCE pk_eip_m_gpdb_kubun INCREMENT 20;
CREATE SEQUENCE pk_eip_m_gpdb_kubun_value INCREMENT 20;

--インデックス
DROP INDEX IF EXISTS eip_t_gpdb_record_record_no_index;
CREATE INDEX eip_t_gpdb_record_record_no_index ON eip_t_gpdb_record (record_no);
