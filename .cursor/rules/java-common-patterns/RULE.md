---
description: "Java常用开发模式和代码模板"
alwaysApply: false
---

# Java常用开发模式和代码模板

## 金额计算模式

使用 `CalculationUtil` 进行金额计算：

```java
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;

// 乘法：数量 * 单价
String subtotal = CalculationUtil.multiply(quantity, unitPrice, CommonNumConstants.NUM_TWO);

// 加法：累加总金额
String total = CalculationUtil.add(amount1, amount2, CommonNumConstants.NUM_TWO);

// 减法：计算差额
String difference = CalculationUtil.subtract(amount1, amount2, CommonNumConstants.NUM_TWO);
```

## 查询条件构建模式

使用 `QueryWrapper` 和 `MybatisPlusUtil`：

```java
QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
queryWrapper.eq(MybatisPlusUtil.toColumns(Entity::getField), value);
queryWrapper.in(MybatisPlusUtil.toColumns(Entity::getField), valueList);
queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Entity::getCreateTime));
```

## 关联数据处理模式

### 主表关联子表
```java
@Override
public Entity getDataFromDb(String id) {
    Entity entity = super.getDataFromDb(id);
    // 设置子表数据
    entity.setChildList(childService.queryByParentId(id));
    return entity;
}

@Override
protected void writePostpose(Entity entity, String userId) {
    super.writePostpose(entity, userId);
    // 保存子表数据
    if (CollectionUtil.isNotEmpty(entity.getChildList())) {
        entity.getChildList().forEach(child -> child.setParentId(entity.getId()));
        childService.createEntity(entity.getChildList(), userId);
    }
}
```

## 数据校验模式

```java
@Override
public void validatorEntity(Entity entity) {
    super.validatorEntity(entity);
    // 自定义校验逻辑
    if (StrUtil.isEmpty(entity.getRequiredField())) {
        throw new CustomException("必填字段不能为空");
    }
}
```

## 枚举使用模式

```java
// 状态字段使用枚举
@ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
private Integer enabled;

// 查询时使用枚举
queryWrapper.eq(MybatisPlusUtil.toColumns(Entity::getEnabled), EnableEnum.ENABLE_USING.getKey());
```

## 缓存使用模式

```java
// 实体类添加缓存注解
@RedisCacheField(name = "cache:key:name", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)

// 清除缓存
clearCache(entity.getId());
```

## 编号生成模式

```java
@Override
protected void createPrepose(Entity entity) {
    if (StrUtil.isEmpty(entity.getCode())) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String code = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setCode(code);
    }
}
```

