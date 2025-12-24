---
description: "版本管理实体类编写规范"
alwaysApply: false
globs:
  - "**/entity/**/*.java"
---

# 版本管理实体类编写规范

## 何时使用Version基类

当实体需要支持版本管理功能时，继承 `Version` 基类：
- 需要版本号管理（大版本、小版本）
- 需要版本历史记录
- 需要版本发布功能
- 需要版本对比功能

## Version基类特性

继承 `Version` 的实体类自动获得：
- `versionNo` - 版本号组
- `largeVersion` - 大版本号
- `smallVersion` - 小版本号
- `fromId` - 来源id（父版本id）
- `whetherLast` - 是否最新版本
- `whetherPublish` - 是否发布

## 实现规范

### Service层处理
```java
@Override
public String createEntity(ProScheme entity, String userId) {
    entity.setStartSmallVersion(false); // 不自动增加小版本
    return super.createEntity(entity, userId);
}

@Override
protected void createPrepose(ProScheme entity) {
    if (StrUtil.isNotEmpty(entity.getId())) {
        // 创建新版本时，继承父版本的编号
        ProScheme preVersion = selectById(entity.getId());
        entity.setSchemeCode(preVersion.getSchemeCode());
        clearCache(entity.getId());
    } else {
        // 首次创建时，生成编号
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setSchemeCode(oddNumber);
    }
}
```

### 查询规范
- 默认查询最新版本：`queryWrapper.eq(MybatisPlusUtil.toColumns(Xxx::getWhetherLast), WhetherEnum.ENABLE_USING.getKey())`
- 查询所有版本：根据 `versionNo` 或 `schemeCode` 查询

### 版本发布
- 使用 `publishVersionById` 方法发布版本
- 发布后 `whetherPublish` 设置为启用状态

## 注意事项

- 版本号由系统自动管理，不要手动设置
- 创建新版本时，需要从父版本继承关键字段（如编号）
- 查询列表时，默认只显示最新版本
- 版本发布后，旧版本不应再被修改

