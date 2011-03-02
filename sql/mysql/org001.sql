-- MySQL dump 10.13  Distrib 5.5.9, for Linux (i686)
--
-- Host: localhost    Database: org001
-- ------------------------------------------------------
-- Server version	5.5.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `app_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `body` text COLLATE utf8_unicode_ci,
  `external_id` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `portlet_params` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `priority` double DEFAULT NULL,
  `title` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_map`
--

DROP TABLE IF EXISTS `activity_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_map` (
  `activity_id` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `is_read` int(11) DEFAULT NULL,
  `login_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_map`
--

LOCK TABLES `activity_map` WRITE;
/*!40000 ALTER TABLE `activity_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aipo_license`
--

DROP TABLE IF EXISTS `aipo_license`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aipo_license` (
  `license_id` int(11) NOT NULL AUTO_INCREMENT,
  `license` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `limit_users` int(11) DEFAULT NULL,
  PRIMARY KEY (`license_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aipo_license`
--

LOCK TABLES `aipo_license` WRITE;
/*!40000 ALTER TABLE `aipo_license` DISABLE KEYS */;
/*!40000 ALTER TABLE `aipo_license` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_data`
--

DROP TABLE IF EXISTS `app_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_data` (
  `app_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `login_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `value` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_data`
--

LOCK TABLES `app_data` WRITE;
/*!40000 ALTER TABLE `app_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `app_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `app_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `consumer_key` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `consumer_secret` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `description` text COLLATE utf8_unicode_ci,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) DEFAULT NULL,
  `summary` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_config`
--

DROP TABLE IF EXISTS `container_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_config`
--

LOCK TABLES `container_config` WRITE;
/*!40000 ALTER TABLE `container_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_facility_group`
--

DROP TABLE IF EXISTS `eip_facility_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_facility_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `facility_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `facility_id` (`facility_id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `eip_facility_group_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `eip_m_facility` (`facility_id`) ON DELETE CASCADE,
  CONSTRAINT `eip_facility_group_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `turbine_group` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_facility_group`
--

LOCK TABLES `eip_facility_group` WRITE;
/*!40000 ALTER TABLE `eip_facility_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_facility_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_address_group`
--

DROP TABLE IF EXISTS `eip_m_address_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_address_group` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `public_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_address_group`
--

LOCK TABLES `eip_m_address_group` WRITE;
/*!40000 ALTER TABLE `eip_m_address_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_address_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_addressbook`
--

DROP TABLE IF EXISTS `eip_m_addressbook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_addressbook` (
  `address_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) DEFAULT NULL,
  `first_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `first_name_kana` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name_kana` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cellular_phone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cellular_mail` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  `position_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `public_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_user_id` int(11) DEFAULT NULL,
  `update_user_id` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_addressbook`
--

LOCK TABLES `eip_m_addressbook` WRITE;
/*!40000 ALTER TABLE `eip_m_addressbook` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_addressbook` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_addressbook_company`
--

DROP TABLE IF EXISTS `eip_m_addressbook_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_addressbook_company` (
  `company_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `company_name_kana` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `post_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `zipcode` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fax_number` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `update_user_id` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_addressbook_company`
--

LOCK TABLES `eip_m_addressbook_company` WRITE;
/*!40000 ALTER TABLE `eip_m_addressbook_company` DISABLE KEYS */;
INSERT INTO `eip_m_addressbook_company` VALUES (1,'未分類','ミブンルイ','','','','','','',1,1,NULL,NULL);
/*!40000 ALTER TABLE `eip_m_addressbook_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_company`
--

DROP TABLE IF EXISTS `eip_m_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_company` (
  `company_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `company_name_kana` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `zipcode` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fax_number` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ipaddress` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `ipaddress_internal` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `port_internal` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_company`
--

LOCK TABLES `eip_m_company` WRITE;
/*!40000 ALTER TABLE `eip_m_company` DISABLE KEYS */;
INSERT INTO `eip_m_company` VALUES (1,'','','','','','','','',80,'',80,'2011-03-02','2011-03-02 18:31:47');
/*!40000 ALTER TABLE `eip_m_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_config`
--

DROP TABLE IF EXISTS `eip_m_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_config`
--

LOCK TABLES `eip_m_config` WRITE;
/*!40000 ALTER TABLE `eip_m_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_facility`
--

DROP TABLE IF EXISTS `eip_m_facility`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_facility` (
  `facility_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `facility_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`facility_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_facility`
--

LOCK TABLES `eip_m_facility` WRITE;
/*!40000 ALTER TABLE `eip_m_facility` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_facility` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_mail_account`
--

DROP TABLE IF EXISTS `eip_m_mail_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_mail_account` (
  `account_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `account_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `account_type` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `smtpserver_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `pop3server_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `pop3user_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `pop3password` blob NOT NULL,
  `mail_user_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mail_address` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `smtp_port` varchar(5) COLLATE utf8_unicode_ci NOT NULL,
  `smtp_encryption_flg` int(11) DEFAULT NULL,
  `pop3_port` varchar(5) COLLATE utf8_unicode_ci NOT NULL,
  `pop3_encryption_flg` int(11) DEFAULT NULL,
  `auth_send_flg` int(11) DEFAULT NULL,
  `auth_send_user_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `auth_send_user_passwd` blob,
  `auth_receive_flg` int(11) DEFAULT NULL,
  `del_at_pop3_flg` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `del_at_pop3_before_days_flg` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `del_at_pop3_before_days` int(11) DEFAULT NULL,
  `non_received_flg` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `default_folder_id` int(11) DEFAULT NULL,
  `last_received_date` datetime DEFAULT NULL,
  `signature` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_mail_account`
--

LOCK TABLES `eip_m_mail_account` WRITE;
/*!40000 ALTER TABLE `eip_m_mail_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_mail_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_mail_notify_conf`
--

DROP TABLE IF EXISTS `eip_m_mail_notify_conf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_mail_notify_conf` (
  `notify_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `notify_type` int(11) NOT NULL,
  `notify_flg` int(11) NOT NULL,
  `notify_time` time DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`notify_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_mail_notify_conf`
--

LOCK TABLES `eip_m_mail_notify_conf` WRITE;
/*!40000 ALTER TABLE `eip_m_mail_notify_conf` DISABLE KEYS */;
INSERT INTO `eip_m_mail_notify_conf` VALUES (1,1,1,3,'07:00:00','2011-03-02','2011-03-02 18:31:47'),(2,1,21,3,NULL,'2011-03-02','2011-03-02 18:31:47'),(3,1,22,3,NULL,'2011-03-02','2011-03-02 18:31:47'),(4,1,23,3,NULL,'2011-03-02','2011-03-02 18:31:47'),(5,1,24,3,NULL,'2011-03-02','2011-03-02 18:31:47');
/*!40000 ALTER TABLE `eip_m_mail_notify_conf` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_mybox`
--

DROP TABLE IF EXISTS `eip_m_mybox`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_mybox` (
  `mybox_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL,
  `aipo_id` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `aipo_passwd` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`mybox_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_mybox`
--

LOCK TABLES `eip_m_mybox` WRITE;
/*!40000 ALTER TABLE `eip_m_mybox` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_mybox` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_position`
--

DROP TABLE IF EXISTS `eip_m_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_position` (
  `position_id` int(11) NOT NULL AUTO_INCREMENT,
  `position_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`position_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_position`
--

LOCK TABLES `eip_m_position` WRITE;
/*!40000 ALTER TABLE `eip_m_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_post`
--

DROP TABLE IF EXISTS `eip_m_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_post` (
  `post_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL,
  `post_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `zipcode` varchar(8) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `in_telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `out_telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fax_number` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `group_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  UNIQUE KEY `eip_m_post_group_name_key` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_post`
--

LOCK TABLES `eip_m_post` WRITE;
/*!40000 ALTER TABLE `eip_m_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_m_user_position`
--

DROP TABLE IF EXISTS `eip_m_user_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_m_user_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_m_user_position`
--

LOCK TABLES `eip_m_user_position` WRITE;
/*!40000 ALTER TABLE `eip_m_user_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_m_user_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_acl_portlet_feature`
--

DROP TABLE IF EXISTS `eip_t_acl_portlet_feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_acl_portlet_feature` (
  `feature_id` int(11) NOT NULL AUTO_INCREMENT,
  `feature_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature_alias_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `acl_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`feature_id`)
) ENGINE=InnoDB AUTO_INCREMENT=202 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_acl_portlet_feature`
--

LOCK TABLES `eip_t_acl_portlet_feature` WRITE;
/*!40000 ALTER TABLE `eip_t_acl_portlet_feature` DISABLE KEYS */;
INSERT INTO `eip_t_acl_portlet_feature` VALUES (111,'schedule_self','スケジュール（自分の予定）操作',31),(112,'schedule_other','スケジュール（他ユーザーの予定）操作',31),(113,'schedule_facility','スケジュール（施設の予約）操作',12),(121,'blog_entry_self','ブログ（自分の記事）操作',31),(122,'blog_entry_other','ブログ（他ユーザーの記事）操作',3),(123,'blog_entry_reply','ブログ（記事へのコメント）操作',20),(124,'blog_theme','ブログ（テーマ）操作',31),(131,'msgboard_topic','掲示板（トピック）操作',31),(132,'msgboard_topic_reply','掲示板（トピック返信）操作',20),(133,'msgboard_category','掲示板（自分のカテゴリ）操作',31),(134,'msgboard_category_other','掲示板（他ユーザーのカテゴリ）操作',27),(135,'msgboard_topic_other','掲示板（他ユーザーのトピック）操作',24),(141,'todo_todo_self','ToDo（自分のToDo）操作',31),(142,'todo_todo_other','ToDo（他ユーザーのToDo）操作',3),(143,'todo_category_self','ToDo（カテゴリ）操作',31),(151,'workflow_request_self','ワークフロー（自分の依頼）操作',31),(152,'workflow_request_other','ワークフロー（他ユーザーの依頼）操作',3),(161,'addressbook_address_inside','アドレス帳（社内アドレス）操作',3),(162,'addressbook_address_outside','アドレス帳（社外アドレス）操作',31),(163,'addressbook_company','アドレス帳（会社情報）操作',31),(164,'addressbook_company_group','アドレス帳（社外グループ）操作',31),(171,'timecard_timecard_self','タイムカード（自分のタイムカード）操作',47),(172,'timecard_timecard_other','タイムカード（他人のタイムカード）操作',33),(181,'cabinet_file','共有フォルダ（ファイル）操作',31),(182,'cabinet_folder','共有フォルダ（フォルダ）操作',30),(191,'manhour_summary_self','プロジェクト管理（自分の工数）操作',1),(192,'manhour_summary_other','プロジェクト管理（他ユーザーの工数）操作',1),(193,'manhour_common_category','プロジェクト管理（自分の共有カテゴリ）操作',31),(194,'manhour_common_category_other','プロジェクト管理（他ユーザーの共有カテゴリ）操作',27),(201,'portlet_customize','ポートレット操作',29);
/*!40000 ALTER TABLE `eip_t_acl_portlet_feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_acl_role`
--

DROP TABLE IF EXISTS `eip_t_acl_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_acl_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `feature_id` int(11) NOT NULL,
  `acl_type` int(11) DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_acl_role`
--

LOCK TABLES `eip_t_acl_role` WRITE;
/*!40000 ALTER TABLE `eip_t_acl_role` DISABLE KEYS */;
INSERT INTO `eip_t_acl_role` VALUES (1,'スケジュール（自分の予定）管理者',111,31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません',NULL,NULL),(2,'スケジュール（他ユーザーの予定）',112,3,NULL,NULL,NULL),(3,'スケジュール（施設の予約）管理者',113,12,NULL,NULL,NULL),(4,'ブログ（自分の記事）管理者',121,31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません',NULL,NULL),(5,'ブログ（他ユーザーの記事）管理者',122,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません',NULL,NULL),(6,'ブログ（記事へのコメント）管理者',123,20,NULL,NULL,NULL),(7,'ブログ（テーマ）管理者',124,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(8,'掲示板（トピック）管理者',131,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(9,'掲示板（トピック返信）管理者',132,20,NULL,NULL,NULL),(10,'掲示板（自分のカテゴリ）管理者',133,31,'＊追加、編集、削除は一覧表示と詳細表示の権限を持っていないと使用できません',NULL,NULL),(12,'ToDo（自分のToDo）管理者',141,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(13,'ToDo（他ユーザーのToDo）管理者',142,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません',NULL,NULL),(14,'ToDo（カテゴリ）管理者',143,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(15,'ワークフロー（自分の依頼）管理者',151,31,'＊詳細表示、追加、削除は一覧表示の権限を持っていないと使用できません ＊承認、再申請や差し戻しは編集の権限が必要です',NULL,NULL),(16,'ワークフロー（他ユーザーの依頼）管理者',152,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません',NULL,NULL),(17,'アドレス帳（社内アドレス）管理者',161,3,'＊詳細表示は一覧表示の権限を持っていないと使用できません',NULL,NULL),(18,'アドレス帳（社外アドレス）管理者',162,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(19,'アドレス帳（会社情報）管理者',163,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(20,'アドレス帳（社外グループ）管理者',164,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(21,'タイムカード（自分のタイムカード）管理者',171,47,'＊追加、編集、外部出力は一覧表示の権限を持っていないと使用できません',NULL,NULL),(22,'タイムカード（他人のタイムカード）管理者',172,33,'＊自分のタイムカード一覧表示の権限を持っていないと使用できません\n＊外部出力は一覧表示の権限を持っていないと使用できません',NULL,NULL),(23,'共有フォルダ（ファイル）管理者',181,31,'＊詳細表示、追加、編集、削除は一覧表示の権限を持っていないと使用できません',NULL,NULL),(24,'共有フォルダ（フォルダ）管理者',182,30,'＊編集、削除は詳細表示の権限を持っていないと使用できません',NULL,NULL),(25,'プロジェクト管理（自分の工数）管理者',191,1,NULL,NULL,NULL),(26,'プロジェクト管理（他ユーザーの工数）管理者',192,1,NULL,NULL,NULL),(27,'プロジェクト管理（自分の共有カテゴリ）管理者',193,31,NULL,NULL,NULL),(28,'プロジェクト管理（他ユーザーの共有カテゴリ）管理者',194,3,NULL,NULL,NULL),(29,'ポートレット管理者',201,29,NULL,NULL,NULL);
/*!40000 ALTER TABLE `eip_t_acl_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_acl_user_role_map`
--

DROP TABLE IF EXISTS `eip_t_acl_user_role_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_acl_user_role_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_acl_user_role_map`
--

LOCK TABLES `eip_t_acl_user_role_map` WRITE;
/*!40000 ALTER TABLE `eip_t_acl_user_role_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_acl_user_role_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_addressbook_group_map`
--

DROP TABLE IF EXISTS `eip_t_addressbook_group_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_addressbook_group_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_addressbook_group_map`
--

LOCK TABLES `eip_t_addressbook_group_map` WRITE;
/*!40000 ALTER TABLE `eip_t_addressbook_group_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_addressbook_group_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_blog`
--

DROP TABLE IF EXISTS `eip_t_blog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_blog` (
  `blog_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`blog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_blog`
--

LOCK TABLES `eip_t_blog` WRITE;
/*!40000 ALTER TABLE `eip_t_blog` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_blog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_blog_comment`
--

DROP TABLE IF EXISTS `eip_t_blog_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_blog_comment` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `comment` text COLLATE utf8_unicode_ci,
  `entry_id` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `entry_id` (`entry_id`),
  CONSTRAINT `eip_t_blog_comment_ibfk_1` FOREIGN KEY (`entry_id`) REFERENCES `eip_t_blog_entry` (`entry_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_blog_comment`
--

LOCK TABLES `eip_t_blog_comment` WRITE;
/*!40000 ALTER TABLE `eip_t_blog_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_blog_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_blog_entry`
--

DROP TABLE IF EXISTS `eip_t_blog_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_blog_entry` (
  `entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `title` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `blog_id` int(11) NOT NULL,
  `thema_id` int(11) DEFAULT NULL,
  `allow_comments` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`entry_id`),
  KEY `blog_id` (`blog_id`),
  KEY `thema_id` (`thema_id`),
  CONSTRAINT `eip_t_blog_entry_ibfk_1` FOREIGN KEY (`blog_id`) REFERENCES `eip_t_blog` (`blog_id`) ON DELETE CASCADE,
  CONSTRAINT `eip_t_blog_entry_ibfk_2` FOREIGN KEY (`thema_id`) REFERENCES `eip_t_blog_thema` (`thema_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_blog_entry`
--

LOCK TABLES `eip_t_blog_entry` WRITE;
/*!40000 ALTER TABLE `eip_t_blog_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_blog_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_blog_file`
--

DROP TABLE IF EXISTS `eip_t_blog_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_blog_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `title` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `file_path` text COLLATE utf8_unicode_ci NOT NULL,
  `file_thumbnail` blob,
  `entry_id` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `entry_id` (`entry_id`),
  CONSTRAINT `eip_t_blog_file_ibfk_1` FOREIGN KEY (`entry_id`) REFERENCES `eip_t_blog_entry` (`entry_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_blog_file`
--

LOCK TABLES `eip_t_blog_file` WRITE;
/*!40000 ALTER TABLE `eip_t_blog_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_blog_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_blog_footmark_map`
--

DROP TABLE IF EXISTS `eip_t_blog_footmark_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_blog_footmark_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `blog_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `blog_id` (`blog_id`),
  CONSTRAINT `eip_t_blog_footmark_map_ibfk_1` FOREIGN KEY (`blog_id`) REFERENCES `eip_t_blog` (`blog_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_blog_footmark_map`
--

LOCK TABLES `eip_t_blog_footmark_map` WRITE;
/*!40000 ALTER TABLE `eip_t_blog_footmark_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_blog_footmark_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_blog_thema`
--

DROP TABLE IF EXISTS `eip_t_blog_thema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_blog_thema` (
  `thema_id` int(11) NOT NULL AUTO_INCREMENT,
  `thema_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci,
  `create_user_id` int(11) NOT NULL,
  `update_user_id` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`thema_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_blog_thema`
--

LOCK TABLES `eip_t_blog_thema` WRITE;
/*!40000 ALTER TABLE `eip_t_blog_thema` DISABLE KEYS */;
INSERT INTO `eip_t_blog_thema` VALUES (1,'未分類','',0,0,NULL,NULL);
/*!40000 ALTER TABLE `eip_t_blog_thema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_cabinet_file`
--

DROP TABLE IF EXISTS `eip_t_cabinet_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_cabinet_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `folder_id` int(11) NOT NULL,
  `file_title` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `file_name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `file_path` text COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_user_id` int(11) DEFAULT NULL,
  `update_user_id` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `folder_id` (`folder_id`),
  CONSTRAINT `eip_t_cabinet_file_ibfk_1` FOREIGN KEY (`folder_id`) REFERENCES `eip_t_cabinet_folder` (`folder_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_cabinet_file`
--

LOCK TABLES `eip_t_cabinet_file` WRITE;
/*!40000 ALTER TABLE `eip_t_cabinet_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_cabinet_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_cabinet_folder`
--

DROP TABLE IF EXISTS `eip_t_cabinet_folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_cabinet_folder` (
  `folder_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL,
  `folder_name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_user_id` int(11) DEFAULT NULL,
  `update_user_id` int(11) DEFAULT NULL,
  `public_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`folder_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_cabinet_folder`
--

LOCK TABLES `eip_t_cabinet_folder` WRITE;
/*!40000 ALTER TABLE `eip_t_cabinet_folder` DISABLE KEYS */;
INSERT INTO `eip_t_cabinet_folder` VALUES (1,0,'ルートフォルダ','',0,0,'0',NULL,NULL);
/*!40000 ALTER TABLE `eip_t_cabinet_folder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_cabinet_folder_map`
--

DROP TABLE IF EXISTS `eip_t_cabinet_folder_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_cabinet_folder_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folder_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `status` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `folder_id` (`folder_id`),
  CONSTRAINT `eip_t_cabinet_folder_map_ibfk_1` FOREIGN KEY (`folder_id`) REFERENCES `eip_t_cabinet_folder` (`folder_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_cabinet_folder_map`
--

LOCK TABLES `eip_t_cabinet_folder_map` WRITE;
/*!40000 ALTER TABLE `eip_t_cabinet_folder_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_cabinet_folder_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_common_category`
--

DROP TABLE IF EXISTS `eip_t_common_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_common_category` (
  `common_category_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_user_id` int(11) NOT NULL,
  `update_user_id` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`common_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_common_category`
--

LOCK TABLES `eip_t_common_category` WRITE;
/*!40000 ALTER TABLE `eip_t_common_category` DISABLE KEYS */;
INSERT INTO `eip_t_common_category` VALUES (1,'未分類','',0,0,NULL,NULL);
/*!40000 ALTER TABLE `eip_t_common_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_eventlog`
--

DROP TABLE IF EXISTS `eip_t_eventlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_eventlog` (
  `eventlog_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `event_date` datetime DEFAULT NULL,
  `event_type` int(11) DEFAULT NULL,
  `portlet_type` int(11) DEFAULT NULL,
  `entity_id` int(11) DEFAULT NULL,
  `ip_addr` text COLLATE utf8_unicode_ci,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`eventlog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_eventlog`
--

LOCK TABLES `eip_t_eventlog` WRITE;
/*!40000 ALTER TABLE `eip_t_eventlog` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_eventlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_ext_timecard`
--

DROP TABLE IF EXISTS `eip_t_ext_timecard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_ext_timecard` (
  `timecard_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `punch_date` date DEFAULT NULL,
  `type` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `clock_in_time` datetime DEFAULT NULL,
  `clock_out_time` datetime DEFAULT NULL,
  `reason` text COLLATE utf8_unicode_ci,
  `outgoing_time1` datetime DEFAULT NULL,
  `comeback_time1` datetime DEFAULT NULL,
  `outgoing_time2` datetime DEFAULT NULL,
  `comeback_time2` datetime DEFAULT NULL,
  `outgoing_time3` datetime DEFAULT NULL,
  `comeback_time3` datetime DEFAULT NULL,
  `outgoing_time4` datetime DEFAULT NULL,
  `comeback_time4` datetime DEFAULT NULL,
  `outgoing_time5` datetime DEFAULT NULL,
  `comeback_time5` datetime DEFAULT NULL,
  `remarks` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`timecard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_ext_timecard`
--

LOCK TABLES `eip_t_ext_timecard` WRITE;
/*!40000 ALTER TABLE `eip_t_ext_timecard` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_ext_timecard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_ext_timecard_system`
--

DROP TABLE IF EXISTS `eip_t_ext_timecard_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_ext_timecard_system` (
  `system_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `system_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_hour` int(11) DEFAULT NULL,
  `start_minute` int(11) DEFAULT NULL,
  `end_hour` int(11) DEFAULT NULL,
  `end_minute` int(11) DEFAULT NULL,
  `worktime_in` int(11) DEFAULT NULL,
  `resttime_in` int(11) DEFAULT NULL,
  `worktime_out` int(11) DEFAULT NULL,
  `resttime_out` int(11) DEFAULT NULL,
  `change_hour` int(11) DEFAULT NULL,
  `outgoing_add_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`system_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_ext_timecard_system`
--

LOCK TABLES `eip_t_ext_timecard_system` WRITE;
/*!40000 ALTER TABLE `eip_t_ext_timecard_system` DISABLE KEYS */;
INSERT INTO `eip_t_ext_timecard_system` VALUES (1,0,'通常',9,0,18,0,360,60,360,60,4,'T','2011-03-02','2011-03-02 18:31:47');
/*!40000 ALTER TABLE `eip_t_ext_timecard_system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_ext_timecard_system_map`
--

DROP TABLE IF EXISTS `eip_t_ext_timecard_system_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_ext_timecard_system_map` (
  `system_map_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `system_id` int(11) NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`system_map_id`),
  KEY `system_id` (`system_id`),
  CONSTRAINT `eip_t_ext_timecard_system_map_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `eip_t_ext_timecard_system` (`system_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_ext_timecard_system_map`
--

LOCK TABLES `eip_t_ext_timecard_system_map` WRITE;
/*!40000 ALTER TABLE `eip_t_ext_timecard_system_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_ext_timecard_system_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_mail`
--

DROP TABLE IF EXISTS `eip_t_mail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_mail` (
  `mail_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `folder_id` int(11) NOT NULL,
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `read_flg` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `subject` text COLLATE utf8_unicode_ci,
  `person` text COLLATE utf8_unicode_ci,
  `event_date` datetime DEFAULT NULL,
  `file_volume` int(11) DEFAULT NULL,
  `has_files` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `file_path` text COLLATE utf8_unicode_ci,
  `mail` blob,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`mail_id`),
  KEY `eip_t_mail_user_id_index` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_mail`
--

LOCK TABLES `eip_t_mail` WRITE;
/*!40000 ALTER TABLE `eip_t_mail` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_mail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_mail_filter`
--

DROP TABLE IF EXISTS `eip_t_mail_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_mail_filter` (
  `filter_id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `dst_folder_id` int(11) DEFAULT NULL,
  `filter_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `filter_string` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `filter_type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sort_order` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_mail_filter`
--

LOCK TABLES `eip_t_mail_filter` WRITE;
/*!40000 ALTER TABLE `eip_t_mail_filter` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_mail_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_mail_folder`
--

DROP TABLE IF EXISTS `eip_t_mail_folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_mail_folder` (
  `folder_id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `folder_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_mail_folder`
--

LOCK TABLES `eip_t_mail_folder` WRITE;
/*!40000 ALTER TABLE `eip_t_mail_folder` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_mail_folder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_memo`
--

DROP TABLE IF EXISTS `eip_t_memo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_memo` (
  `memo_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `memo_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`memo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_memo`
--

LOCK TABLES `eip_t_memo` WRITE;
/*!40000 ALTER TABLE `eip_t_memo` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_memo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_msgboard_category`
--

DROP TABLE IF EXISTS `eip_t_msgboard_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_msgboard_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) DEFAULT NULL,
  `category_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `public_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_msgboard_category`
--

LOCK TABLES `eip_t_msgboard_category` WRITE;
/*!40000 ALTER TABLE `eip_t_msgboard_category` DISABLE KEYS */;
INSERT INTO `eip_t_msgboard_category` VALUES (1,0,'その他','','T',NULL,NULL);
/*!40000 ALTER TABLE `eip_t_msgboard_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_msgboard_category_map`
--

DROP TABLE IF EXISTS `eip_t_msgboard_category_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_msgboard_category_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `status` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `eip_t_msgboard_category_map_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `eip_t_msgboard_category` (`category_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_msgboard_category_map`
--

LOCK TABLES `eip_t_msgboard_category_map` WRITE;
/*!40000 ALTER TABLE `eip_t_msgboard_category_map` DISABLE KEYS */;
INSERT INTO `eip_t_msgboard_category_map` VALUES (1,1,0,'A');
/*!40000 ALTER TABLE `eip_t_msgboard_category_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_msgboard_file`
--

DROP TABLE IF EXISTS `eip_t_msgboard_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_msgboard_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) DEFAULT NULL,
  `topic_id` int(11) DEFAULT NULL,
  `file_name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `file_path` text COLLATE utf8_unicode_ci NOT NULL,
  `file_thumbnail` blob,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `topic_id` (`topic_id`),
  CONSTRAINT `eip_t_msgboard_file_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `eip_t_msgboard_topic` (`topic_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_msgboard_file`
--

LOCK TABLES `eip_t_msgboard_file` WRITE;
/*!40000 ALTER TABLE `eip_t_msgboard_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_msgboard_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_msgboard_topic`
--

DROP TABLE IF EXISTS `eip_t_msgboard_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_msgboard_topic` (
  `topic_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `topic_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `category_id` int(11) DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `update_user_id` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`topic_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `eip_t_msgboard_topic_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `eip_t_msgboard_category` (`category_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_msgboard_topic`
--

LOCK TABLES `eip_t_msgboard_topic` WRITE;
/*!40000 ALTER TABLE `eip_t_msgboard_topic` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_msgboard_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_note`
--

DROP TABLE IF EXISTS `eip_t_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_note` (
  `note_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `client_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `company_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telephone` varchar(24) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email_address` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `add_dest_type` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `subject_type` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `custom_subject` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `message` text COLLATE utf8_unicode_ci,
  `accept_date` datetime DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`note_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_note`
--

LOCK TABLES `eip_t_note` WRITE;
/*!40000 ALTER TABLE `eip_t_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_note_map`
--

DROP TABLE IF EXISTS `eip_t_note_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_note_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `note_id` int(11) NOT NULL,
  `user_id` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `del_flg` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note_stat` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `confirm_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `note_id` (`note_id`),
  CONSTRAINT `eip_t_note_map_ibfk_1` FOREIGN KEY (`note_id`) REFERENCES `eip_t_note` (`note_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_note_map`
--

LOCK TABLES `eip_t_note_map` WRITE;
/*!40000 ALTER TABLE `eip_t_note_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_note_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_schedule`
--

DROP TABLE IF EXISTS `eip_t_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_schedule` (
  `schedule_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `repeat_pattern` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `place` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `public_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `edit_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `update_user_id` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_schedule`
--

LOCK TABLES `eip_t_schedule` WRITE;
/*!40000 ALTER TABLE `eip_t_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_schedule_map`
--

DROP TABLE IF EXISTS `eip_t_schedule_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_schedule_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `schedule_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `common_category_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `schedule_id` (`schedule_id`),
  KEY `common_category_id` (`common_category_id`),
  CONSTRAINT `eip_t_schedule_map_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `eip_t_schedule` (`schedule_id`) ON DELETE CASCADE,
  CONSTRAINT `eip_t_schedule_map_ibfk_2` FOREIGN KEY (`common_category_id`) REFERENCES `eip_t_common_category` (`common_category_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_schedule_map`
--

LOCK TABLES `eip_t_schedule_map` WRITE;
/*!40000 ALTER TABLE `eip_t_schedule_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_schedule_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_timecard`
--

DROP TABLE IF EXISTS `eip_t_timecard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_timecard` (
  `timecard_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `work_date` datetime DEFAULT NULL,
  `work_flag` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `reason` text COLLATE utf8_unicode_ci,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`timecard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_timecard`
--

LOCK TABLES `eip_t_timecard` WRITE;
/*!40000 ALTER TABLE `eip_t_timecard` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_timecard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_timecard_settings`
--

DROP TABLE IF EXISTS `eip_t_timecard_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_timecard_settings` (
  `timecard_settings_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `start_hour` int(11) DEFAULT NULL,
  `start_minute` int(11) DEFAULT NULL,
  `end_hour` int(11) DEFAULT NULL,
  `end_minute` int(11) DEFAULT NULL,
  `worktime_in` int(11) DEFAULT NULL,
  `resttime_in` int(11) DEFAULT NULL,
  `worktime_out` int(11) DEFAULT NULL,
  `resttime_out` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`timecard_settings_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_timecard_settings`
--

LOCK TABLES `eip_t_timecard_settings` WRITE;
/*!40000 ALTER TABLE `eip_t_timecard_settings` DISABLE KEYS */;
INSERT INTO `eip_t_timecard_settings` VALUES (1,1,9,0,18,0,360,60,360,60,NULL,NULL);
/*!40000 ALTER TABLE `eip_t_timecard_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_todo`
--

DROP TABLE IF EXISTS `eip_t_todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_todo` (
  `todo_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `todo_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `public_flag` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `addon_schedule_flg` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`todo_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `eip_t_todo_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `eip_t_todo_category` (`category_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_todo`
--

LOCK TABLES `eip_t_todo` WRITE;
/*!40000 ALTER TABLE `eip_t_todo` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_todo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_todo_category`
--

DROP TABLE IF EXISTS `eip_t_todo_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_todo_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `category_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_todo_category`
--

LOCK TABLES `eip_t_todo_category` WRITE;
/*!40000 ALTER TABLE `eip_t_todo_category` DISABLE KEYS */;
INSERT INTO `eip_t_todo_category` VALUES (1,0,'未分類','',NULL,NULL);
/*!40000 ALTER TABLE `eip_t_todo_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_whatsnew`
--

DROP TABLE IF EXISTS `eip_t_whatsnew`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_whatsnew` (
  `whatsnew_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `portlet_type` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `entity_id` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`whatsnew_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_whatsnew`
--

LOCK TABLES `eip_t_whatsnew` WRITE;
/*!40000 ALTER TABLE `eip_t_whatsnew` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_whatsnew` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_workflow_category`
--

DROP TABLE IF EXISTS `eip_t_workflow_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_workflow_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `category_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `template` text COLLATE utf8_unicode_ci,
  `route_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  KEY `route_id` (`route_id`),
  CONSTRAINT `eip_t_workflow_category_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `eip_t_workflow_route` (`route_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_workflow_category`
--

LOCK TABLES `eip_t_workflow_category` WRITE;
/*!40000 ALTER TABLE `eip_t_workflow_category` DISABLE KEYS */;
INSERT INTO `eip_t_workflow_category` VALUES (1,0,'未分類','',NULL,NULL,NULL,NULL),(2,0,'有給休暇届','',NULL,NULL,NULL,NULL),(3,0,'稟議書','',NULL,NULL,NULL,NULL),(4,0,'結婚休暇届','',NULL,NULL,NULL,NULL),(5,0,'産前産後休暇届','',NULL,NULL,NULL,NULL),(6,0,'育児休暇届','',NULL,NULL,NULL,NULL),(7,0,'育児時間届','',NULL,NULL,NULL,NULL),(8,0,'特別有給休暇届（業務上負傷等）','',NULL,NULL,NULL,NULL),(9,0,'忌引き休暇届','',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `eip_t_workflow_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_workflow_file`
--

DROP TABLE IF EXISTS `eip_t_workflow_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_workflow_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) DEFAULT NULL,
  `request_id` int(11) DEFAULT NULL,
  `file_name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `file_path` text COLLATE utf8_unicode_ci NOT NULL,
  `file_thumbnail` blob,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `request_id` (`request_id`),
  CONSTRAINT `eip_t_workflow_file_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `eip_t_workflow_request` (`request_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_workflow_file`
--

LOCK TABLES `eip_t_workflow_file` WRITE;
/*!40000 ALTER TABLE `eip_t_workflow_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_workflow_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_workflow_request`
--

DROP TABLE IF EXISTS `eip_t_workflow_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_workflow_request` (
  `request_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `request_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `progress` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `price` bigint(20) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `route_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `category_id` (`category_id`),
  KEY `route_id` (`route_id`),
  CONSTRAINT `eip_t_workflow_request_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `eip_t_workflow_category` (`category_id`) ON DELETE CASCADE,
  CONSTRAINT `eip_t_workflow_request_ibfk_2` FOREIGN KEY (`route_id`) REFERENCES `eip_t_workflow_route` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_workflow_request`
--

LOCK TABLES `eip_t_workflow_request` WRITE;
/*!40000 ALTER TABLE `eip_t_workflow_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_workflow_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_workflow_request_map`
--

DROP TABLE IF EXISTS `eip_t_workflow_request_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_workflow_request_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `request_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_index` int(11) NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `request_id` (`request_id`),
  CONSTRAINT `eip_t_workflow_request_map_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `eip_t_workflow_request` (`request_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_workflow_request_map`
--

LOCK TABLES `eip_t_workflow_request_map` WRITE;
/*!40000 ALTER TABLE `eip_t_workflow_request_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_workflow_request_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eip_t_workflow_route`
--

DROP TABLE IF EXISTS `eip_t_workflow_route`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eip_t_workflow_route` (
  `route_id` int(11) NOT NULL AUTO_INCREMENT,
  `route_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `create_date` date DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `route` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eip_t_workflow_route`
--

LOCK TABLES `eip_t_workflow_route` WRITE;
/*!40000 ALTER TABLE `eip_t_workflow_route` DISABLE KEYS */;
/*!40000 ALTER TABLE `eip_t_workflow_route` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jetspeed_group_profile`
--

DROP TABLE IF EXISTS `jetspeed_group_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jetspeed_group_profile` (
  `country` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL,
  `group_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `language` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL,
  `media_type` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `page` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `profile` blob,
  `psml_id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`psml_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jetspeed_group_profile`
--

LOCK TABLES `jetspeed_group_profile` WRITE;
/*!40000 ALTER TABLE `jetspeed_group_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `jetspeed_group_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jetspeed_role_profile`
--

DROP TABLE IF EXISTS `jetspeed_role_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jetspeed_role_profile` (
  `country` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL,
  `language` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL,
  `media_type` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `page` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `profile` blob,
  `psml_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`psml_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jetspeed_role_profile`
--

LOCK TABLES `jetspeed_role_profile` WRITE;
/*!40000 ALTER TABLE `jetspeed_role_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `jetspeed_role_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jetspeed_user_profile`
--

DROP TABLE IF EXISTS `jetspeed_user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jetspeed_user_profile` (
  `country` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL,
  `language` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL,
  `media_type` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `page` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `profile` blob,
  `psml_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`psml_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jetspeed_user_profile`
--

LOCK TABLES `jetspeed_user_profile` WRITE;
/*!40000 ALTER TABLE `jetspeed_user_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `jetspeed_user_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_id`
--

DROP TABLE IF EXISTS `module_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_id` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_id`
--

LOCK TABLES `module_id` WRITE;
/*!40000 ALTER TABLE `module_id` DISABLE KEYS */;
/*!40000 ALTER TABLE `module_id` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_consumer`
--

DROP TABLE IF EXISTS `oauth_consumer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_consumer` (
  `app_id` int(11) DEFAULT NULL,
  `consumer_key` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `consumer_secret` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  CONSTRAINT `oauth_consumer_ibfk_1` FOREIGN KEY (`app_id`) REFERENCES `application` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_consumer`
--

LOCK TABLES `oauth_consumer` WRITE;
/*!40000 ALTER TABLE `oauth_consumer` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_consumer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_token`
--

DROP TABLE IF EXISTS `oauth_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_token` (
  `access_token` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `key` int(11) NOT NULL AUTO_INCREMENT,
  `session_handle` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `token_expire_milis` int(11) DEFAULT NULL,
  `token_secret` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_token`
--

LOCK TABLES `oauth_token` WRITE;
/*!40000 ALTER TABLE `oauth_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turbine_group`
--

DROP TABLE IF EXISTS `turbine_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turbine_group` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `objectdata` blob,
  `owner_id` int(11) DEFAULT NULL,
  `group_alias_name` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `public_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `turbine_group_group_name_key` (`group_name`),
  UNIQUE KEY `turbine_group_owner_id_key` (`owner_id`,`group_alias_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turbine_group`
--

LOCK TABLES `turbine_group` WRITE;
/*!40000 ALTER TABLE `turbine_group` DISABLE KEYS */;
INSERT INTO `turbine_group` VALUES (1,'Jetspeed',NULL,NULL,NULL,NULL),(2,'LoginUser',NULL,NULL,NULL,NULL),(3,'Facility',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `turbine_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turbine_permission`
--

DROP TABLE IF EXISTS `turbine_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turbine_permission` (
  `permission_id` int(11) NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `objectdata` blob,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `turbine_permission_permission_name_key` (`permission_name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turbine_permission`
--

LOCK TABLES `turbine_permission` WRITE;
/*!40000 ALTER TABLE `turbine_permission` DISABLE KEYS */;
INSERT INTO `turbine_permission` VALUES (1,'view',NULL),(2,'customize',NULL),(3,'maximize',NULL),(4,'minimize',NULL),(5,'personalize',NULL),(6,'info',NULL),(7,'close',NULL),(8,'detach',NULL);
/*!40000 ALTER TABLE `turbine_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turbine_role`
--

DROP TABLE IF EXISTS `turbine_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turbine_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `objectdata` blob,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `turbine_role_role_name_key` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turbine_role`
--

LOCK TABLES `turbine_role` WRITE;
/*!40000 ALTER TABLE `turbine_role` DISABLE KEYS */;
INSERT INTO `turbine_role` VALUES (1,'user',NULL),(2,'admin',NULL),(3,'guest',NULL);
/*!40000 ALTER TABLE `turbine_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turbine_role_permission`
--

DROP TABLE IF EXISTS `turbine_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turbine_role_permission` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  UNIQUE KEY `role_permission_index` (`role_id`,`permission_id`),
  KEY `role_id` (`role_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `turbine_role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `turbine_permission` (`permission_id`),
  CONSTRAINT `turbine_role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `turbine_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turbine_role_permission`
--

LOCK TABLES `turbine_role_permission` WRITE;
/*!40000 ALTER TABLE `turbine_role_permission` DISABLE KEYS */;
INSERT INTO `turbine_role_permission` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(3,1),(3,6);
/*!40000 ALTER TABLE `turbine_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turbine_user`
--

DROP TABLE IF EXISTS `turbine_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turbine_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `password_value` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `first_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `last_name` varchar(99) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `confirm_value` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `disabled` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `objectdata` blob,
  `password_changed` datetime DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  `position_id` int(11) DEFAULT NULL,
  `in_telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `out_telephone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cellular_phone` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cellular_mail` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cellular_uid` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `first_name_kana` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name_kana` varchar(99) COLLATE utf8_unicode_ci DEFAULT NULL,
  `photo` blob,
  `created_user_id` int(11) DEFAULT NULL,
  `updated_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `turbine_user_login_name_key` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turbine_user`
--

LOCK TABLES `turbine_user` WRITE;
/*!40000 ALTER TABLE `turbine_user` DISABLE KEYS */;
INSERT INTO `turbine_user` VALUES (1,'admin','0DPiKuNIrrVmD8IUCuw1hQxNqZc=',' ','Admin','','CONFIRMED','2011-03-02 18:31:47','2011-03-02 18:31:47','2011-03-02 18:31:47','F',NULL,'2011-03-02 18:31:47',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'template','MibsvmUCE6Sc0DrmcUB1Dk80AIM=','Aimluck','Template','','CONFIRMED','2011-03-02 18:31:47','2011-03-02 18:31:47','2011-03-02 18:31:47','T',NULL,'2011-03-02 18:31:47',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'anon','YVGPsXFatNaYrKMqeECsey5QfT4=','Anonymous','User','','CONFIRMED','2011-03-02 18:31:47','2011-03-02 18:31:47','2011-03-02 18:31:47','F',NULL,'2011-03-02 18:31:47',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `turbine_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turbine_user_group_role`
--

DROP TABLE IF EXISTS `turbine_user_group_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turbine_user_group_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `group_id` (`group_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `turbine_user_group_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `turbine_user` (`user_id`),
  CONSTRAINT `turbine_user_group_role_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `turbine_group` (`group_id`),
  CONSTRAINT `turbine_user_group_role_ibfk_3` FOREIGN KEY (`role_id`) REFERENCES `turbine_role` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turbine_user_group_role`
--

LOCK TABLES `turbine_user_group_role` WRITE;
/*!40000 ALTER TABLE `turbine_user_group_role` DISABLE KEYS */;
INSERT INTO `turbine_user_group_role` VALUES (1,2,1,1),(2,1,1,1),(3,1,1,2),(4,3,1,3);
/*!40000 ALTER TABLE `turbine_user_group_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-02 18:50:26
