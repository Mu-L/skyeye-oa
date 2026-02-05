-- 工单派单规则配置表
CREATE TABLE IF NOT EXISTS `crm_service_dispatch_config` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户id',
  `cap_order_quantity` int DEFAULT 10 COMMENT '封顶接单量',
  `auto_dispatch_start_time` varchar(10) DEFAULT NULL COMMENT '自动派单开始时间，格式HH:mm',
  `auto_dispatch_end_time` varchar(10) DEFAULT NULL COMMENT '自动派单结束时间，格式HH:mm',
  `even_assignment_enabled` int DEFAULT 1 COMMENT '系统均匀指派规则是否开启，1开启0关闭',
  `pool_cap_quantity` int DEFAULT 0 COMMENT '工单池封顶接单量',
  `pool_count_suspended` int DEFAULT 1 COMMENT '是否计算工单池暂停工单，1是0否',
  `system_rules` json DEFAULT NULL COMMENT '系统派单规则列表，JSON格式',
  `create_id` varchar(32) DEFAULT NULL COMMENT '创建人id',
  `create_time` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `last_update_id` varchar(32) DEFAULT NULL COMMENT '最后更新人id',
  `last_update_time` varchar(32) DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单派单规则配置表';
