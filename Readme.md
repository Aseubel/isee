# 概述

基于 Spring Boot 框架构建单体应用，采用分层架构设计，目的是构建一个用于智慧农业的数据监测系统：

- **表现层**：Restful API 提供前端交互接口
- **服务层**：业务逻辑处理
- **数据访问层**：与数据库交互
- **安全层**：基于 `Redis` + `Jwt` 实现简单权限控制

# 后端

## 数据库表

数据库已经与传感器实现数据同步

正常情况 sensorHistoryData 每分钟（可能有 1~2 秒偏差）更新一次所有传感器及其参数

```sql
-- this means area
DROP TABLE IF EXISTS `commInterfaces`;
CREATE TABLE `commInterfaces`  (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Enable` int NULL DEFAULT NULL,
  `Type` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `LocalPort` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `RemotePort` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `IpAddress` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Baudrate` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `DataBits` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `StopBits` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `SParity` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensorData
-- ----------------------------
DROP TABLE IF EXISTS `sensorData`;
CREATE TABLE `sensorData`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensorHistoryData
-- ----------------------------
DROP TABLE IF EXISTS `sensorHistoryData`;
CREATE TABLE `sensorHistoryData`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 1533281 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensorParameter
-- ----------------------------
DROP TABLE IF EXISTS `sensorParameter`;
CREATE TABLE `sensorParameter`  (
  `ID` int NOT NULL AUTO_INCREMENT,
  `SensorTypeId` int NULL DEFAULT NULL,
  `Name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `DisplayName` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Sign` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `RegisterAddr` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Scale` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `IX_sensorParameter_SensorTypeId`(`SensorTypeId` ASC) USING BTREE,
  CONSTRAINT `FK_sensorParameter_sensorType_SensorTypeId` FOREIGN KEY (`SensorTypeId`) REFERENCES `sensorType` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensorType
-- ----------------------------
DROP TABLE IF EXISTS `sensorType`;
CREATE TABLE `sensorType`  (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `DisplayName` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Manufacturer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Model` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Type` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Protocol` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensors
-- ----------------------------
DROP TABLE IF EXISTS `sensors`;
CREATE TABLE `sensors`  (
  `ID` int NOT NULL AUTO_INCREMENT,
  `DisplayName` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Enable` int NULL DEFAULT NULL,
  `SlaveId` int NULL DEFAULT NULL,
  `SensorTypeId` int NULL DEFAULT NULL,
  `ComInterfaceId` int NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `IX_sensors_ComInterfaceId`(`ComInterfaceId` ASC) USING BTREE,
  INDEX `IX_sensors_SensorTypeId`(`SensorTypeId` ASC) USING BTREE,
  CONSTRAINT `FK_sensors_commInterfaces_ComInterfaceId` FOREIGN KEY (`ComInterfaceId`) REFERENCES `commInterfaces` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_sensors_sensorType_SensorTypeId` FOREIGN KEY (`SensorTypeId`) REFERENCES `sensorType` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

## 技术栈

- Spring Boot 3.x

- MyBatis or MyBatis-Plus

- MySQL

- Redis（缓存和鉴权）

## 系统功能模块设计

### 用户权限系统

1. **用户管理**：增删改查用户，重置密码，分配角色
2. **地区管理**：添加地区，分配地区给用户
3. **权限控制**：
   - 超级管理员：可访问所有地区数据，管理所有用户和地区
   - 地区管理员：只能访问和管理自己负责的地区
   - 普通用户：只能查看分配给自己的地区数据

```sql
-- 用户表
CREATE TABLE `user` (
  `id` BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `admin` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户权限;0-普通用户;1-地区管理员;2-超级管理员',
  `area_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '区域id-对应commInterfaces行id',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1',
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户表';
CREATE UNIQUE INDEX uk_user_username ON `user` (username);
```

### 数据查询系统

1. **实时数据查询**：按地区、传感器类型查询最新数据
2. **历史数据查询**：按时间范围、地区、传感器类型查询历史数据

### 预警系统

1. **预警通知**：当某个传感器某个参数（或全部参数）半小时没有更新时触发预警，实时通知超级管理员和所属地区管理员
2. **预警历史记录**：查询历史预警通知状态

```sql
-- 预警通知记录表
CREATE TABLE `alert_notifications` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY '主键',
  `sensor_id` int NOT NULL DEFAULT 0 COMMENT '触发预警的传感器id',
  `sensor_param_id` int NOT NULL 0 COMMENT '触发预警的参数id',
  `alert_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '0-未解决;1-已解决',
) ENGINE = InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '预警通知表';
CREATE UNIQUE INDEX uk_alert_notifications_sensor_id ON `alert_notifications` (sensor_id);
CREATE UNIQUE INDEX uk_alert_notifications_sensor_param_id ON `alert_notifications` (sensor_param_id);
```

## 实现要点

1. **权限控制**：
   - 自定义注解结合 AOP 实现基于角色和地区的权限验证
   - `Redis` + `Token` 的形式来保障账户登录安全
2. **数据查询优化**：
   - 使用 MyBatis-Plus 提高数据访问效率
   - 对频繁查询的数据使用 Redis 缓存，每分钟更新缓存保障数据有效
   - 提供分页和排序功能，优化大量数据查询体验
3. **预警机制**：
   - 定时任务轮询传感器数据，与前端使用 SSE 连接
   - 当数据触发预警时，生成通知存储数据库，通过 SSE 向前端推送，同时手机短信通知管理员

## 部署

`Docker` 部署

## 可能的拓展优化

1. **数据导出**：支持将查询结果导出为 CSV 或 Excel 格式
