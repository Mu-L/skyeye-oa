# 智能路线推荐系统

## 项目概述

这是一个基于Spring Boot的智能路线推荐系统，主要用于校园导航和路线规划。系统能够根据用户当前位置和目标终点，智能推荐最优的三条路线。

## 核心功能

### 1. 智能路线推荐
- 根据当前位置和目标终点推荐最优路线
- 综合考虑距离、站点数量、路线类型等多个因素
- 返回前三条最优路线，按综合评分排序

### 2. 多维度评分算法
- **距离评分（40%）**：总距离越短评分越高
- **站点评分（30%）**：站点数量越少评分越高
- **起点距离评分（20%）**：到第一个站点距离越近评分越高
- **路线类型评分（10%）**：步行>自行车>公交

### 3. 智能推荐理由
系统会自动生成推荐理由，包括：
- 综合评分最高
- 起点距离很近
- 站点较少，转乘方便
- 总距离较短

## 技术架构

### 后端技术栈
- **Spring Boot 2.x**：主框架
- **MyBatis Plus**：ORM框架
- **MySQL**：数据库
- **Redis**：缓存
- **Maven**：依赖管理

### 核心模块
```
school-pro/
├── src/main/java/com/skyeye/school/route/
│   ├── controller/
│   │   └── RouteController.java          # 路线控制器
│   ├── service/
│   │   ├── RoutesService.java            # 路线服务接口
│   │   ├── RouteStopService.java         # 路线站点服务接口
│   │   └── impl/
│   │       ├── RouteServiceImpl.java     # 路线服务实现
│   │       └── RouteStopServiceImpl.java # 路线站点服务实现
│   ├── entity/
│   │   ├── Routes.java                   # 路线实体
│   │   └── RouteStop.java                # 路线站点实体
│   └── dao/
│       ├── RoutesDao.java                # 路线数据访问层
│       └── RouteStopDao.java             # 路线站点数据访问层
```

## 数据库设计

### 路线表 (school_routes)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(32) | 主键 |
| school_id | VARCHAR(32) | 学校ID |
| start_id | VARCHAR(32) | 起始地点ID |
| end_id | VARCHAR(32) | 终点地点ID |
| route_length | FLOAT | 路线长度 |
| route_type | INT | 路线类型 |
| enabled | INT | 是否启用 |
| description | VARCHAR(500) | 描述 |

### 路线站点表 (school_route_stop)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(32) | 主键 |
| route_id | VARCHAR(32) | 路线ID |
| latitude | VARCHAR(20) | 纬度 |
| longitude | VARCHAR(20) | 经度 |
| stop_order | INT | 站点排序 |

## API接口

### 路线推荐接口
```
POST /post/RouteController/queryRoutesNavigationLists
```

**请求参数：**
- latitude: 当前纬度
- longitude: 当前经度
- endId: 终点地点ID
- schoolId: 学校ID
- typeId: 路线类型（可选）

**返回结果：**
```json
{
    "code": 200,
    "message": "操作成功",
    "beans": [
        {
            "rank": 1,
            "route": {...},
            "distanceToFirstStop": 0.2,
            "routeDistance": 1.5,
            "totalDistance": 1.7,
            "score": 85.6,
            "stopCount": 3,
            "recommendationReason": "综合评分最高，起点距离很近"
        }
    ],
    "total": 3
}
```

## 核心算法

### 1. 距离计算算法
使用Haversine公式计算两点间的球面距离：
```java
private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
    final double R = 6371; // 地球半径（公里）
    
    double latDistance = Math.toRadians(lat2 - lat1);
    double lngDistance = Math.toRadians(lng2 - lng1);
    
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return R * c;
}
```

### 2. 综合评分算法
```java
private double calculateRouteScore(double totalDistance, int stopCount, Integer routeType, double distanceToFirstStop) {
    // 距离评分
    double distanceScore = 100.0 / (1 + totalDistance);
    
    // 站点评分
    double stopScore = 100.0 / (1 + stopCount * 0.5);
    
    // 起点距离评分
    double firstStopScore = 100.0 / (1 + distanceToFirstStop * 2);
    
    // 路线类型评分
    double typeScore = getTypeScore(routeType);
    
    // 综合评分
    return distanceScore * 0.4 + stopScore * 0.3 + firstStopScore * 0.2 + typeScore * 0.1;
}
```

## 部署说明

### 环境要求
- JDK 1.8+
- MySQL 5.7+
- Redis 3.0+
- Maven 3.6+

### 配置说明
1. 修改 `application.yml` 中的数据库连接信息
2. 配置Redis连接信息
3. 设置日志级别

### 启动步骤
1. 执行数据库脚本
2. 配置环境变量
3. 运行 `mvn clean install`
4. 启动应用

## 测试

### 单元测试
运行测试类 `RouteServiceTest.java` 验证核心功能：
```bash
mvn test -Dtest=RouteServiceTest
```

### 接口测试
使用Postman或其他API测试工具测试接口：
```
POST http://localhost:8080/post/RouteController/queryRoutesNavigationLists
Content-Type: application/json

{
    "latitude": 39.9042,
    "longitude": 116.4074,
    "endId": "building_001",
    "schoolId": "school_001",
    "typeId": "1"
}
```

## 性能优化

### 1. 数据库优化
- 为常用查询字段添加索引
- 使用分页查询避免大量数据加载
- 合理使用缓存

### 2. 算法优化
- 距离计算使用缓存
- 批量查询减少数据库访问
- 异步处理大量数据

### 3. 缓存策略
- 路线数据缓存
- 地点信息缓存
- 计算结果缓存

## 扩展功能

### 1. 实时路况
- 集成交通API
- 实时路况分析
- 动态路线调整

### 2. 个性化推荐
- 用户偏好学习
- 历史路线分析
- 智能推荐优化

### 3. 多模态交通
- 步行路线
- 自行车路线
- 公交路线
- 驾车路线

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 联系方式

如有问题或建议，请联系开发团队。 