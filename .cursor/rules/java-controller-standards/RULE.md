---
description: "Java Controller层编写规范和标准"
alwaysApply: false
globs:
  - "**/controller/**/*.java"
  - "**/controller/*.java"
---

# Java Controller层编写规范

## 基本结构

Controller类必须遵循以下结构：

1. **文件头注释**：包含版权信息
2. **包声明**：`com.skyeye.模块名.controller`
3. **类注解**：`@RestController`、`@Api`
4. **依赖注入**：使用 `@Autowired` 注入Service
5. **接口方法**：使用 `@ApiOperation`、`@ApiImplicitParams`、`@RequestMapping`

## 注解使用规范

### 类级别注解
```java
@RestController
@Api(value = "功能名称", tags = "功能名称", modelName = "功能名称")
public class XxxController {
}
```

### 方法级别注解
- `@ApiOperation` - API操作描述
  - `id` - 唯一标识
  - `value` - 操作描述
  - `method` - HTTP方法（GET/POST/DELETE/PUT）
  - `allUse` - 使用范围（"0"-公开，"1"-需要权限，"2"-内部使用）

- `@ApiImplicitParams` - 参数说明
  - 使用 `classBean` 指定实体类（自动解析字段）
  - 或使用 `@ApiImplicitParam` 数组手动指定参数

- `@RequestMapping` - 请求映射
  - 路径格式：`/post/XxxController/方法名`

## 标准CRUD接口

### 查询列表
```java
@ApiOperation(id = "queryXxxList", value = "查询XXX列表", method = "POST", allUse = "2")
@ApiImplicitParams(classBean = CommonPageInfo.class)
@RequestMapping("/post/XxxController/queryXxxList")
public void queryXxxList(InputObject inputObject, OutputObject outputObject) {
    xxxService.queryPageList(inputObject, outputObject);
}
```

### 新增/编辑
```java
@ApiOperation(id = "writeXxx", value = "新增/编辑XXX", method = "POST", allUse = "1")
@ApiImplicitParams(classBean = Xxx.class)
@RequestMapping("/post/XxxController/writeXxx")
public void writeXxx(InputObject inputObject, OutputObject outputObject) {
    xxxService.saveOrUpdateEntity(inputObject, outputObject);
}
```

### 根据ID查询
```java
@ApiOperation(id = "queryXxxById", value = "根据id查询XXX详情", method = "GET", allUse = "2")
@ApiImplicitParams({
    @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
@RequestMapping("/post/XxxController/queryXxxById")
public void queryXxxById(InputObject inputObject, OutputObject outputObject) {
    xxxService.selectById(inputObject, outputObject);
}
```

### 批量查询
```java
@ApiOperation(id = "queryXxxByIds", value = "根据ids批量查询XXX详情", method = "POST", allUse = "2")
@ApiImplicitParams({
    @ApiImplicitParam(id = "ids", name = "ids", value = "主键id集合", required = "required")})
@RequestMapping("/post/XxxController/queryXxxByIds")
public void queryXxxByIds(InputObject inputObject, OutputObject outputObject) {
    xxxService.selectByIds(inputObject, outputObject);
}
```

### 删除
```java
@ApiOperation(id = "deleteXxxById", value = "根据ID删除XXX信息", method = "DELETE", allUse = "1")
@ApiImplicitParams({
    @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
@RequestMapping("/post/XxxController/deleteXxxById")
public void deleteXxxById(InputObject inputObject, OutputObject outputObject) {
    xxxService.deleteById(inputObject, outputObject);
}
```

## 自定义业务接口

根据业务需求添加自定义接口，遵循相同的注解规范。

## 参数和返回值

- 所有接口方法使用 `InputObject` 和 `OutputObject`
- 不直接使用 `@RequestParam` 或 `@RequestBody`
- 通过 `InputObject.getParams()` 获取参数
- 通过 `OutputObject.setBeans()` 设置返回数据

