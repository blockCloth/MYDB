# MYDB

**在线体验地址：** http://db.blockcloth.cn

## 项目概述

MYDB 是一个简易版本的 MySQL 数据库。项目涵盖了数据存储、事务管理、多版本并发控制（MVCC）和索引构建等核心数据库功能，还模拟实现了日志管理和事务状态查询等高级特性。

- 数据的可靠性和数据恢复
- 两段锁协议（2PL）实现可串行化调度
- MVCC
- 两种事务隔离级别（读提交和可重复读）
- 死锁处理
- 简单的表和字段管理
- 简陋的 SQL 解析
- 基于 WebSocket 的通信协议

## 运行方式

- 首先拷贝项目到指定目录
- 通过`IDEA/Eclipse`导入maven项目
- 运行启动类即可

### 修改项目存储位置

- Windows环境：
  - 修改`application-dev.yml`里面的内容即可
- Linux环境：
  - 修改`application-prod.yml`里面的内容即可

## 执行示例
![image](https://github.com/blockCloth/MYDB/assets/93373863/f9739b25-01f3-4a1c-aaba-b9198c609eed)
![image](https://github.com/blockCloth/MYDB/assets/93373863/0b9e597e-44a5-4856-b6ff-d431b65c6249)

