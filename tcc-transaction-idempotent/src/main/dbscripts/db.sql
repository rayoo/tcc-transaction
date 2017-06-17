CREATE TABLE `tcc_idempotent` (
  `domain` varchar(100) DEFAULT NULL COMMENT '业务模块名称',
  `gtxid` varbinary(32) NOT NULL COMMENT '全局事务ID',
  `btxid` varbinary(32) NOT NULL COMMENT '分支事务ID',
  `method` varchar(100) DEFAULT NULL COMMENT '方法名称',
  `status` int(2) DEFAULT NULL COMMENT '事务阶段(try1/confirm2/cancel3)',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  UNIQUE KEY `UQ_GTXID_BTXID` (`gtxid`,`btxid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='tcc幂等性,全局事务ID和分支事务ID联合唯一索引.'
