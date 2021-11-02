/*
MySQL Backup
Database: SmartCampus
Backup Time: 2021-04-22 22:39:02
*/

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `SmartCampus`.`answer`;
DROP TABLE IF EXISTS `SmartCampus`.`answer_collect`;
DROP TABLE IF EXISTS `SmartCampus`.`answer_dislike`;
DROP TABLE IF EXISTS `SmartCampus`.`answer_like`;
DROP TABLE IF EXISTS `SmartCampus`.`chat`;
DROP TABLE IF EXISTS `SmartCampus`.`comment`;
DROP TABLE IF EXISTS `SmartCampus`.`comment_dislike`;
DROP TABLE IF EXISTS `SmartCampus`.`comment_like`;
DROP TABLE IF EXISTS `SmartCampus`.`dynamic`;
DROP TABLE IF EXISTS `SmartCampus`.`feedback`;
DROP TABLE IF EXISTS `SmartCampus`.`follow_follower`;
DROP TABLE IF EXISTS `SmartCampus`.`message`;
DROP TABLE IF EXISTS `SmartCampus`.`question`;
DROP TABLE IF EXISTS `SmartCampus`.`question_follow`;
DROP TABLE IF EXISTS `SmartCampus`.`question_tag`;
DROP TABLE IF EXISTS `SmartCampus`.`tag`;
DROP TABLE IF EXISTS `SmartCampus`.`user`;
CREATE TABLE `answer` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `author_id` int(10) NOT NULL COMMENT '回答者',
  `content` longtext NOT NULL COMMENT '回答内容',
  `answer_url` varchar(255) NOT NULL COMMENT '回答url',
  `update_time` datetime NOT NULL COMMENT '更新日期，具体到秒',
  `like_count` int(10) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `dislike_count` int(10) NOT NULL DEFAULT '0' COMMENT '踩数',
  `collect_count` int(10) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `comment_count` int(10) NOT NULL DEFAULT '0' COMMENT '评论数',
  `follow_id` int(10) DEFAULT NULL COMMENT '收藏该回答的用户id',
  `question_id` int(10) NOT NULL COMMENT '问题id',
  PRIMARY KEY (`id`),
  KEY `fk_author_answer` (`author_id`),
  KEY `fk_follow_answer` (`follow_id`),
  KEY `fk_question_answer` (`question_id`),
  CONSTRAINT `fk_author_answer` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_follow_answer` FOREIGN KEY (`follow_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_question_answer` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
CREATE TABLE `answer_collect` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '收藏回答的用户id',
  `answer_id` int(10) NOT NULL COMMENT '回答id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未收藏',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;
CREATE TABLE `answer_dislike` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '踩用户id',
  `answer_id` int(10) NOT NULL COMMENT '回答id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未踩',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;
CREATE TABLE `answer_like` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '点赞用户id',
  `answer_id` int(10) NOT NULL COMMENT '回答id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '点赞状态，默认未点赞',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;
CREATE TABLE `chat` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '用户id',
  `content` varchar(255) NOT NULL COMMENT '正文',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
CREATE TABLE `comment` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `author_id` int(10) NOT NULL COMMENT '作者id',
  `content` varchar(255) NOT NULL DEFAULT '无正文' COMMENT '正文',
  `like_count` int(10) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `dislike_count` int(10) DEFAULT '0' COMMENT '踩数',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `answer_id` int(10) NOT NULL COMMENT '回答id',
  PRIMARY KEY (`id`),
  KEY `fk_user_comment` (`author_id`),
  KEY `fk_answer_comment` (`answer_id`),
  CONSTRAINT `fk_answer_comment` FOREIGN KEY (`answer_id`) REFERENCES `answer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_comment` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `comment_dislike` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '用户id',
  `comment_id` int(10) NOT NULL COMMENT '评论id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未踩',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `comment_like` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '用户id',
  `comment_id` int(10) NOT NULL COMMENT '评论id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未点赞',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dynamic` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `follower_id` int(10) NOT NULL COMMENT '接受动态的用户id',
  `follow_id` int(10) NOT NULL COMMENT '动态对象的用户id',
  `type` int(2) NOT NULL COMMENT '1为提问，2为回答',
  `object_id` int(10) NOT NULL COMMENT '动态对象的id(问题和回答)',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未读',
  `url` varchar(255) NOT NULL COMMENT '跳转链接',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `feedback` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '用户id',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` varchar(255) NOT NULL COMMENT '正文',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未读',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `follow_follower` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `follow_id` int(10) NOT NULL COMMENT '被关注用户id',
  `follower_id` int(10) NOT NULL COMMENT '粉丝id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `message` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '通知用户id',
  `state_type` int(2) NOT NULL COMMENT '1-like,2-dislike,3-collect,4-comment,5-warn,6-delete',
  `object_type` int(2) NOT NULL COMMENT '1-question,2-answer,3-comment',
  `object_id` int(10) NOT NULL COMMENT '通知对象的id',
  `url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `message` varchar(255) DEFAULT NULL COMMENT '通知信息',
  `message_uid` int(10) NOT NULL COMMENT '通知信息中的用户id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未读',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8;
CREATE TABLE `question` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键id，自增',
  `title` varchar(255) NOT NULL DEFAULT '无标题' COMMENT '标题',
  `content` varchar(255) NOT NULL DEFAULT '无正文' COMMENT '正文',
  `view_count` int(10) NOT NULL DEFAULT '0' COMMENT '浏览次数(点击量)',
  `like_count` int(10) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `collect_count` int(10) NOT NULL DEFAULT '0' COMMENT '问题关注数(收藏数)',
  `answer_count` int(10) NOT NULL DEFAULT '0' COMMENT '回答数',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `author_id` int(10) NOT NULL COMMENT '问题提出者',
  PRIMARY KEY (`id`),
  KEY `fk_author_question` (`author_id`),
  CONSTRAINT `fk_author_question` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
CREATE TABLE `question_follow` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `uid` int(10) NOT NULL COMMENT '用户id',
  `question_id` int(10) NOT NULL COMMENT '问题id',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态，默认未关注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;
CREATE TABLE `question_tag` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `question_id` int(10) NOT NULL COMMENT '问题id',
  `tag_id` int(10) NOT NULL COMMENT '标签id',
  PRIMARY KEY (`id`),
  KEY `fk_tag_question` (`question_id`),
  KEY `fk_question_tag` (`tag_id`),
  CONSTRAINT `fk_question_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_tag_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;
CREATE TABLE `tag` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `tag_name` varchar(20) NOT NULL DEFAULT '标签' COMMENT '标签名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色',
  `user_name` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `gender` int(1) NOT NULL DEFAULT '1' COMMENT '0-female,1-male',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '昵称',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `photo_url` varchar(255) NOT NULL COMMENT '头像',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
BEGIN;
LOCK TABLES `SmartCampus`.`answer` WRITE;
DELETE FROM `SmartCampus`.`answer`;
INSERT INTO `SmartCampus`.`answer` (`id`,`author_id`,`content`,`answer_url`,`update_time`,`like_count`,`dislike_count`,`collect_count`,`comment_count`,`follow_id`,`question_id`) VALUES (34, 11, '<div style=\"background-color: #F6F4EC\" id=\"editor-one\" class=\"editor-wrapper placeholderText\" contenteditable=\"true\">我捡到了</div>', '/question/33/answer/34', '2021-04-22 20:30:30', 0, 0, 0, 0, NULL, 33),(35, 12, '<div style=\"background-color: #F6F4EC\" id=\"editor-one\" class=\"editor-wrapper placeholderText\" contenteditable=\"true\">当你孤单你会想起谁</div>', '/question/33/answer/35', '2021-04-22 20:31:37', 0, 0, 0, 0, NULL, 33);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`answer_collect` WRITE;
DELETE FROM `SmartCampus`.`answer_collect`;
INSERT INTO `SmartCampus`.`answer_collect` (`id`,`uid`,`answer_id`,`state`) VALUES (64, 11, 34, 0),(65, 12, 34, 0),(66, 12, 35, 0);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`answer_dislike` WRITE;
DELETE FROM `SmartCampus`.`answer_dislike`;
INSERT INTO `SmartCampus`.`answer_dislike` (`id`,`uid`,`answer_id`,`state`) VALUES (64, 11, 34, 0),(65, 12, 34, 0),(66, 12, 35, 0);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`answer_like` WRITE;
DELETE FROM `SmartCampus`.`answer_like`;
INSERT INTO `SmartCampus`.`answer_like` (`id`,`uid`,`answer_id`,`state`) VALUES (64, 11, 34, 0),(65, 12, 34, 0),(66, 12, 35, 0);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`chat` WRITE;
DELETE FROM `SmartCampus`.`chat`;
INSERT INTO `SmartCampus`.`chat` (`id`,`uid`,`content`,`update_time`) VALUES (16, 11, '哥们，我耳机丢了，帮我找一下呗，谢了', '2021-04-18 16:06:05');
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`comment` WRITE;
DELETE FROM `SmartCampus`.`comment`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`comment_dislike` WRITE;
DELETE FROM `SmartCampus`.`comment_dislike`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`comment_like` WRITE;
DELETE FROM `SmartCampus`.`comment_like`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`dynamic` WRITE;
DELETE FROM `SmartCampus`.`dynamic`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`feedback` WRITE;
DELETE FROM `SmartCampus`.`feedback`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`follow_follower` WRITE;
DELETE FROM `SmartCampus`.`follow_follower`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`message` WRITE;
DELETE FROM `SmartCampus`.`message`;
INSERT INTO `SmartCampus`.`message` (`id`,`uid`,`state_type`,`object_type`,`object_id`,`url`,`message`,`message_uid`,`state`,`update_time`) VALUES (155, 11, 7, 2, 34, '/user/question/33/answer/34', '回答了你的问题', 11, 1, '2021-04-18 16:06:47'),(156, 11, 7, 2, 35, '/user/question/33/answer/35', '回答了你的问题', 12, 0, '2021-04-22 20:31:37');
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`question` WRITE;
DELETE FROM `SmartCampus`.`question`;
INSERT INTO `SmartCampus`.`question` (`id`,`title`,`content`,`view_count`,`like_count`,`collect_count`,`answer_count`,`update_time`,`author_id`) VALUES (33, '2021.4.18，教学楼丢失AirPodsPro一份', '2021.4.18，教学楼丢失AirPodsPro一份，上面有一个黑色的煤球保护壳，内部刻有“郑永康”三个字，希望捡到的好哥们可以联系我，重重有赏', 40, 0, 0, 2, '2021-04-18 16:05:29', 11);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`question_follow` WRITE;
DELETE FROM `SmartCampus`.`question_follow`;
INSERT INTO `SmartCampus`.`question_follow` (`id`,`uid`,`question_id`,`state`) VALUES (48, 11, 33, 0),(49, 12, 33, 0);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`question_tag` WRITE;
DELETE FROM `SmartCampus`.`question_tag`;
INSERT INTO `SmartCampus`.`question_tag` (`id`,`question_id`,`tag_id`) VALUES (69, 33, 1);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`tag` WRITE;
DELETE FROM `SmartCampus`.`tag`;
INSERT INTO `SmartCampus`.`tag` (`id`,`tag_name`) VALUES (1, '耳机'),(2, '手机'),(3, '书包'),(4, '书本'),(5, '水杯'),(6, '电脑'),(7, '平板'),(8, '眼镜'),(9, '手表');
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `SmartCampus`.`user` WRITE;
DELETE FROM `SmartCampus`.`user`;
INSERT INTO `SmartCampus`.`user` (`id`,`role`,`user_name`,`password`,`gender`,`birth`,`email`,`nick_name`,`signature`,`photo_url`) VALUES (11, 'user', 'zyk', '289c34c323be8ce0a7f60689ee6d8ac2', 1, NULL, '974230624@qq.com', 'zyk', NULL, '/asserts/img/user-default.jpg'),(12, 'user', 'yhl', '64f7a489bab1fc22bfeb6dccfc2103e8', 1, NULL, '123456789@qq.com', 'yhl', NULL, '/asserts/img/user-default.jpg');
UNLOCK TABLES;
COMMIT;
