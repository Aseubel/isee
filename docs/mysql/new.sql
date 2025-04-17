
-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS`user` (
  `id` BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
	`phone` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `admin` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户权限;0-普通用户;1-地区管理员;2-超级管理员',
  `area_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '区域id-对应commInterfaces行id',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户表';
CREATE UNIQUE INDEX uk_user_username ON `user` (username);
CREATE INDEX ind_user_area_id ON `user` (area_id);

-- 预警规则表
-- CREATE TABLE `alert_rules` (
--   `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY '主键',
--   `sensor_param_id` int NOT NULL DEFAULT 0 COMMENT '传感器参数id',
--   `threshold` double NOT NULL DEFAULT 0 COMMENT '阈值',
--   `comparison` varchar(10) NOT NULL CHECK (`comparison` IN ('<', '>', '=', '!=', '<=', '>=')),
--   `alert_level` varchar(20) NOT NULL DEFAULT '警告' COMMENT '预警等级',
--   `enabled` tinyint(1) DEFAULT 1,
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '预警规则表';
-- CREATE UNIQUE INDEX uk_alert_rule_sensor_param_id `alert_rules` (sensor_param_id);
-- 
-- 预警通知记录表
DROP TABLE IF EXISTS `alert_notification`;
CREATE TABLE IF NOT EXISTS `alert_notification` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `sensor_id` int NOT NULL DEFAULT 0 COMMENT '触发预警的传感器id',
  `sensor_param_id` int NOT NULL DEFAULT 0 COMMENT '触发预警的参数id',
  `alert_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
	`area_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '区域id-对应commInterfaces行id',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '0-未解决;1-已解决'
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '预警通知记录表';
CREATE INDEX ind_alert_area_id ON `alert_notification` (area_id);

-- 结果表
DROP TABLE IF EXISTS `result`;
CREATE TABLE IF NOT EXISTS `result` (
    -- 主键
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
		-- 结果id
    `result_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '结果id',
    -- 类型，0-无法判断;1-有害虫;2-无害虫
    `type` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '类型，0-无法判断;1-有害虫;2-无害虫',
		-- 原图id
		`origin_image_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '原图id',
		-- 结果图id
		`result_image_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '结果图id',
		-- 农场
	  `farm` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '农场',
		-- 地区
		`area` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '地区',
    -- 检测时间
    `detect_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检测时间',
    -- 软删除标识 0-未删除 1-已删除
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0-未删除;1-已删除'
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '结果表';
CREATE INDEX ind_farm_area ON `result` (farm, area);
CREATE INDEX ind_type ON `result` (type);


-- 图片表
DROP TABLE IF EXISTS `image`;
CREATE TABLE IF NOT EXISTS `image` (
    -- 主键
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
		-- 图片id
    `image_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '图片id',
    -- 图片路径
    `image_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '图片路径',
    -- 创建时间
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    -- 更新时间
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 软删除标识 0-未删除 1-已删除
    `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0-未删除;1-已删除'
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '图片表';
CREATE UNIQUE INDEX uk_image_id ON `image` (image_id);