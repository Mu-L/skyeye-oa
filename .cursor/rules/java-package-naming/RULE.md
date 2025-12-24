---
description: "Java包命名规范和目录结构标准"
alwaysApply: true
---

# Java包命名规范

## 包名规则

1. **必须全小写**：包名中的所有字母必须是小写
2. **多个单词直接连接**：不使用下划线或驼峰，直接连接
   - ✅ 正确：`com.skyeye.abnormalmarking`
   - ❌ 错误：`com.skyeye.abnormalMarking`（包含大写）
   - ❌ 错误：`com.skyeye.abnormal_marking`（使用下划线）

## 目录结构规范

标准模块目录结构：
```
模块名-pro/src/main/java/com/skyeye/模块名/
  entity/          # 实体类
  dao/             # 数据访问层
  service/         # 服务层
    impl/          # 服务实现类
  controller/      # 控制层
  classenum/       # 枚举类（可选）
```

## 模块命名规范

模块包名应使用小写字母，多个单词直接连接：
- ✅ `abnormalmarking` - 异常标记
- ✅ `afterseal` - 售后服务
- ✅ `project` - 项目
- ✅ `scheme` - 方案

## 示例

```java
// 正确的包名
package com.skyeye.abnormalmarking.entity;
package com.skyeye.abnormalmarking.dao;
package com.skyeye.abnormalmarking.service;
package com.skyeye.abnormalmarking.service.impl;
package com.skyeye.abnormalmarking.controller;
```

## 注意事项

- 包名必须与目录结构完全一致
- 创建新模块时，确保包名全小写
- 避免使用Java关键字作为包名

