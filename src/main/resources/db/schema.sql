-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `admin` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户权限;0-普通用户;1-地区管理员;2-超级管理员',
  `area_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '区域id-对应commInterfaces行id',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户表';
CREATE UNIQUE INDEX uk_user_username ON `user` (username);

-- 预警通知记录表
DROP TABLE IF EXISTS `alert_notifications`;
CREATE TABLE `alert_notifications` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `sensor_id` int NOT NULL DEFAULT 0 COMMENT '触发预警的传感器id',
  `sensor_param_id` int NOT NULL DEFAULT 0 COMMENT '触发预警的参数id',
  `alert_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '0-未解决;1-已解决'
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '预警通知表';
CREATE INDEX idx_alert_notifications_sensor_id ON `alert_notifications` (sensor_id);
CREATE INDEX idx_alert_notifications_sensor_param_id ON `alert_notifications` (sensor_param_id);

-- 传感器数据表
DROP TABLE IF EXISTS `sensorData`;
CREATE TABLE `sensorData` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `SensorParamId` int NULL DEFAULT NULL,
  `SensorId` int NULL DEFAULT NULL,
  `Value` double NULL DEFAULT NULL,
  `UpdatedAt` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `IX_sensorData_SensorId`(`SensorId` ASC) USING BTREE,
  INDEX `IX_sensorData_SensorParamId`(`SensorParamId` ASC) USING BTREE,
  CONSTRAINT `FK_sensorData_sensorParameter_SensorParamId` FOREIGN KEY (`SensorParamId`) REFERENCES `sensorParameter` (`ID`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `FK_sensorData_sensors_SensorId` FOREIGN KEY (`SensorId`) REFERENCES `sensors` (`ID`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- 传感器历史数据表
DROP TABLE IF EXISTS `sensorHistoryData`;
CREATE TABLE `sensorHistoryData` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `SensorParamId` int NULL DEFAULT NULL,
  `SensorId` int NULL DEFAULT NULL,
  `Value` double NULL DEFAULT NULL,
  `DataTime` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `IX_sensorHistoryData_SensorId`(`SensorId` ASC) USING BTREE,
  INDEX `IX_sensorHistoryData_SensorParamId`(`SensorParamId` ASC) USING BTREE,
  CONSTRAINT `FK_sensorHistoryData_sensorParameter_SensorParamId` FOREIGN KEY (`SensorParamId`) REFERENCES `sensorParameter` (`ID`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `FK_sensorHistoryData_sensors_SensorId` FOREIGN KEY (`SensorId`) REFERENCES `sensors` (`ID`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;