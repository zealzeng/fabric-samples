CREATE TABLE `user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) CHARACTER SET utf8mb4 NOT NULL COMMENT 'User nick name',
  `user_mobile` char(11) CHARACTER SET utf8mb4 NOT NULL,
  `user_pwd` varchar(64) COLLATE utf8mb4_bin NOT NULL,
  `user_create_time` datetime(3) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_mobile_UNIQUE` (`user_mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

