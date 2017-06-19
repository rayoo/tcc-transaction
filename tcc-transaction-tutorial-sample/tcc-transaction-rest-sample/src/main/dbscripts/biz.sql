
CREATE DATABASE /*!32312 IF NOT EXISTS*/`biz` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `biz`;

/*Table structure for table `tcc_idempotent` */

DROP TABLE IF EXISTS `tcc_idempotent`;

CREATE TABLE `tcc_idempotent` (
  `domain` varchar(100) DEFAULT NULL COMMENT '业务模块名称',
  `gtxid` varbinary(32) NOT NULL COMMENT '全局事务ID',
  `btxid` varbinary(32) NOT NULL COMMENT '分支事务ID',
  `method` varchar(100) DEFAULT NULL COMMENT '方法名称',
  `status` int(2) DEFAULT NULL COMMENT '事务阶段(try1/confirm2/cancel3)',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  UNIQUE KEY `UQ_GTXID_BTXID` (`gtxid`,`btxid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='tcc幂等性,全局事务ID和分支事务ID联合唯一索引.'


/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) DEFAULT NULL COMMENT '用户名',
  `dr` int(2) DEFAULT '0' COMMENT '0未删除，1删除',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
