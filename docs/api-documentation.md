# API 接口文档

## 通用说明

所有接口返回格式统一为：

```json
{
  "code": 200,
  "info": "success",
  "data": null
}
```

### 状态码说明

| 状态码 | 说明                |
| ------ | ------------------- |
| 200    | 操作成功            |
| 401    | 未授权或 token 过期 |
| 500    | 服务器内部错误      |

### 错误返回示例

1. 未授权错误：

```json
{
  "code": 401,
  "info": "token已过期",
  "data": null
}
```

2. 服务器错误：

```json
{
  "code": 500,
  "info": "服务器内部错误",
  "data": null
}
```

### 权限说明

系统采用分级权限控制：

- 管理员级别(admin)：0-普通用户，1-区域管理员，2-系统管理员
- 区域权限(areaId)：用户只能访问被分配区域的数据

## 用户管理接口

### 用户登录

- 请求路径：`/api/user/login`
- 请求方式：POST
- 权限要求：无
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | username | String | 是 | 用户名 |
  | password | String | 是 | 密码 |
- 返回示例：
  ```json
  {
    "code": 200,
    "info": "success",
    "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

### 创建用户

- 请求路径：`/api/user/create`
- 请求方式：POST
- 权限要求：管理员级别 ≥2
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | user | User 对象 | 是 | 用户信息 |

- User 对象结构：
  ```json
  {
    "userId": "string",
    "username": "string",
    "password": "string",
    "admin": 0,
    "areaId": 1,
    "isEnabled": true
  }
  ```
- 返回示例：
  ```json
  {
    "code": 200,
    "info": "success",
    "data": null
  }
  ```

### 更新用户信息

- 请求路径：`/api/user/update`
- 请求方式：PUT
- 权限要求：管理员级别 ≥2
- 请求参数：同创建用户
- 返回示例：同创建用户

### 删除用户

- 请求路径：`/api/user/delete/{userId}`
- 请求方式：DELETE
- 权限要求：管理员级别 ≥2
- 路径参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | userId | String | 是 | 用户 ID |
- 返回示例：同创建用户

### 重置密码

- 请求路径：`/api/user/reset-password/{userId}`
- 请求方式：POST
- 权限要求：管理员级别 ≥2
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | userId | String | 是 | 用户 ID |
  | newPassword | String | 是 | 新密码 |
- 返回示例：同创建用户

### 获取区域用户列表

- 请求路径：`/api/user/list/{areaId}`
- 请求方式：GET
- 权限要求：管理员级别 ≥1，需要区域权限
- 路径参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | areaId | Integer | 是 | 区域 ID |
- 返回示例：
  ```json
  {
    "code": 200,
    "info": "success",
    "data": [
      {
        "userId": "12345",
        "username": "user1",
        "admin": 1,
        "areaId": 1,
        "isEnabled": true
      }
    ]
  }
  ```

### 分配用户区域

- 请求路径：`/api/user/assign-area`
- 请求方式：POST
- 权限要求：管理员级别 ≥2
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | userId | String | 是 | 用户 ID |
  | areaId | Integer | 是 | 区域 ID |
- 返回示例：同创建用户

## 传感器数据接口

### 获取实时数据

- 请求路径：`/api/sensor/realtime`
- 请求方式：GET
- 权限要求：需要区域权限
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | areaId | Integer | 否 | 区域 ID |
  | sensorTypeId | Integer | 否 | 传感器类型 ID |
- 返回示例：
  ```json
  {
    "code": 200,
    "info": "success",
    "data": {
      "1": [
        {
          "id": 1,
          "sensorId": 1,
          "sensorParamId": 1,
          "value": 25.6,
          "timestamp": "2023-12-25T10:30:00Z"
        }
      ]
    }
  }
  ```

### 获取历史数据

- 请求路径：`/api/sensor/history`
- 请求方式：GET
- 权限要求：需要区域权限
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | startTime | LocalDateTime | 是 | 开始时间 |
  | endTime | LocalDateTime | 是 | 结束时间 |
  | areaId | Integer | 否 | 区域 ID |
  | sensorTypeId | Integer | 否 | 传感器类型 ID |
  | current | Integer | 否 | 当前页码，默认 1 |
  | size | Integer | 否 | 每页大小，默认 10 |
- 返回示例：
  ```json
  {
    "code": 200,
    "info": "success",
    "data": {
      "records": [
        {
          "id": 1,
          "sensorId": 1,
          "sensorParamId": 1,
          "value": 25.6,
          "timestamp": "2023-12-25T10:30:00Z"
        }
      ],
      "total": 100,
      "size": 10,
      "current": 1
    }
  }
  ```

## 告警接口

### 获取告警列表

- 请求路径：`/api/alert/list`
- 请求方式：GET
- 权限要求：管理员级别 ≥1，需要区域权限
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | areaId | Integer | 否 | 区域 ID |
- 返回示例：
  ```json
  {
    "code": 200,
    "info": "success",
    "data": [
      {
        "id": 1,
        "sensorId": 1,
        "sensorParamId": 1,
        "alertTime": "2023-12-25T10:30:00Z",
        "status": 0
      }
    ]
  }
  ```

### 更新告警状态

- 请求路径：`/api/alert/{alertId}/status`
- 请求方式：PUT
- 权限要求：管理员级别 ≥1，需要区域权限
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  |--------|------|----------|------|
  | alertId | Integer | 是 | 告警 ID |
  | status | Integer | 是 | 告警状态 |
- 返回示例：同创建用户
