---
description: "Java Dao层编写规范和标准"
alwaysApply: false
globs:
  - "**/dao/**/*.java"
  - "**/dao/*.java"
---

# Java Dao层编写规范

## 基本结构

Dao接口必须遵循以下结构：

1. **文件头注释**：包含版权信息、作者、日期
2. **包声明**：`com.skyeye.模块名.dao`
3. **接口定义**：继承 `SkyeyeBaseMapper<Entity>`
4. **类注释**：包含 `@ClassName`、`@Description`、`@author`、`@date`、`@Copyright`

## 规范要求

### 接口定义
- 必须继承 `SkyeyeBaseMapper<Entity>`
- 接口名：`XxxDao`（如：`ProSchemeDao`）
- 通常不需要定义额外方法，MyBatis Plus提供基础CRUD

### 示例
```java
/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxx.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.xxx.entity.Xxx;

/**
 * @ClassName: XxxDao
 * @Description: XXX数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface XxxDao extends SkyeyeBaseMapper<Xxx> {

}
```

## 自定义查询方法

如果需要在Dao中定义自定义SQL查询，使用MyBatis注解或XML映射：

```java
@Select("SELECT * FROM table_name WHERE condition = #{param}")
List<Xxx> customQuery(String param);
```

## 注意事项

- Dao层只负责数据访问，不包含业务逻辑
- 复杂查询逻辑应在Service层使用 `QueryWrapper` 实现
- 避免在Dao层定义过多自定义方法

