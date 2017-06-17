
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tcc` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `tcc`;

/*Table structure for table `tcc_idempotent` 

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
*/

/*Table structure for table `tcc_transaction_biz` */

DROP TABLE IF EXISTS `tcc_transaction`;

CREATE TABLE `tcc_transaction` (
  `TRANSACTION_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DOMAIN` varchar(100) DEFAULT NULL,
  `GLOBAL_TX_ID` varbinary(32) NOT NULL,
  `BRANCH_QUALIFIER` varbinary(32) NOT NULL,
  `CONTENT` varbinary(8000) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `TRANSACTION_TYPE` int(11) DEFAULT NULL,
  `RETRIED_COUNT` int(11) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `LAST_UPDATE_TIME` datetime DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`TRANSACTION_ID`),
  UNIQUE KEY `UX_TX_BQ` (`GLOBAL_TX_ID`,`BRANCH_QUALIFIER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

