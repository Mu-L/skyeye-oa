---
description: "Java实体类编写规范和标准"
alwaysApply: false
globs:
  - "**/entity/**/*.java"
  - "**/entity/*.java"
---

# Java实体类编写规范

## 基本结构

实体类必须遵循以下结构：

1. **文件头注释**：包含版权信息、作者、日期
2. **包声明**：使用小写字母，多个单词直接连接（如：`com.skyeye.abnormalmarking.entity`）
3. **导入语句**：按需导入
4. **类注释**：包含 `@ClassName`、`@Description`、`@author`、`@date`、`@Copyright`
5. **类注解**：`@Data`、`@TableName`、`@ApiModel` 等
6. **字段定义**：使用 `@TableField` 和 `@ApiModelProperty` 注解

## 注解使用规范

### 必需注解
- `@Data` - Lombok注解，自动生成getter/setter
- `@TableName(value = "表名")` - MyBatis Plus表名映射
- `@ApiModel("实体类描述")` - API文档注解

### 可选注解
- `@UniqueField` - 唯一字段标识（如果实体有唯一字段）
- `@RedisCacheField(name = "缓存key")` - Redis缓存配置
- `@TableField(exist = false)` - 非数据库字段标识

### 字段注解
- `@TableField("字段名")` - 数据库字段映射
- `@ApiModelProperty(value = "字段描述", required = "required")` - API文档字段说明
- `@Property(value = "字段描述")` - 用于非数据库字段的说明

## 继承规范

根据业务需求选择合适的基类：
- `BaseGeneralInfo` - 通用实体（包含标准字段：create_id, create_time, last_update_id, last_update_time, tenant_id）
- `Version` - 版本管理实体（包含版本号相关字段）
- `SkyeyeFlowable` - 工作流实体（包含流程相关字段）
- `CommonInfo` - 通用信息实体（用于子表）

## 字段命名规范

- 使用驼峰命名：`projectId`、`schemeCode`
- 布尔字段使用 `is`/`has`/`can` 前缀：`isEnabled`、`hasPermission`
- 关联对象使用 `Mation` 后缀：`projectMation`、`objectMation`
- 列表字段使用 `List` 后缀：`budgetDetailList`、`bomChildList`

## 示例

```java
/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxx.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: Xxx
 * @Description: 实体类描述
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "表名")
@ApiModel("实体类描述")
public class Xxx extends BaseGeneralInfo {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}
```

