# 环境变量配置说明

## 服务器配置

| 环境变量    | 说明       | 默认值 |
| ----------- | ---------- | ------ |
| SERVER_PORT | 服务器端口 | 20611  |
| TZ          | 时区       | PRC    |

### Tomcat 配置

- 最大线程数：200
- 最小空闲线程数：50
- 等待队列长度：10

## 数据库配置

| 环境变量                            | 说明           | 示例值                                                                                                                                |
| ----------------------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| SPRING_DATASOURCE_USERNAME          | 数据库用户名   | u_test                                                                                                                                |
| SPRING_DATASOURCE_PASSWORD          | 数据库密码     | p_test                                                                                                                                |
| SPRING_DATASOURCE_URL               | 数据库连接 URL | jdbc:mysql://120.77.250.8:3306/PFAlldata_DB?serverTimezone=UTC&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai |
| SPRING_DATASOURCE_DRIVER_CLASS_NAME | 数据库驱动类名 | com.mysql.cj.jdbc.Driver                                                                                                              |

### 数据库连接池配置(HikariCP)

| 环境变量                | 说明                     | 默认值          |
| ----------------------- | ------------------------ | --------------- |
| SPRING_HIKARI_POOL_NAME | 连接池名称               | Retail_HikariCP |
| minimum-idle            | 最小空闲连接数           | 20              |
| idle-timeout            | 空闲连接存活最大时间(ms) | 180000          |
| maximum-pool-size       | 连接池最大连接数         | 100             |
| auto-commit             | 自动提交                 | true            |

## Redis 配置

| 环境变量              | 说明           | 默认值 |
| --------------------- | -------------- | ------ |
| REDIS_SDK_CONFIG_HOST | Redis 主机地址 | redis  |
| REDIS_SDK_CONFIG_PORT | Redis 端口     | 6379   |

## 线程池配置

| 配置项           | 说明             | 默认值           |
| ---------------- | ---------------- | ---------------- |
| core-pool-size   | 核心线程数       | 20               |
| max-pool-size    | 最大线程数       | 50               |
| keep-alive-time  | 线程存活时间(ms) | 5000             |
| block-queue-size | 阻塞队列大小     | 5000             |
| policy           | 拒绝策略         | CallerRunsPolicy |

## 应用配置

| 配置项       | 说明     | 默认值 |
| ------------ | -------- | ------ |
| api-version  | API 版本 | v1     |
| cross-origin | 跨域配置 | \*     |
