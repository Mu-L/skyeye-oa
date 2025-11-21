# JavaDelegate 与 ExecutionListener 区别说明

## 目录
- [1. 概述](#1-概述)
- [2. 核心区别对比](#2-核心区别对比)
- [3. 使用场景对比](#3-使用场景对比)
- [4. 配置方式对比](#4-配置方式对比)
- [5. 代码实现对比](#5-代码实现对比)
- [6. 项目中的实际应用](#6-项目中的实际应用)
- [7. 如何选择](#7-如何选择)

---

## 1. 概述

在 Flowable 工作流引擎中，`JavaDelegate` 和 `ExecutionListener` 是两种不同的接口，用于在不同的场景下执行自定义业务逻辑。

### 1.1 JavaDelegate

- **用途**：用于实现服务任务（Service Task）的业务逻辑
- **特点**：作为流程中的一个**节点**存在，会阻塞流程执行直到完成
- **执行时机**：当流程执行到服务任务节点时

### 1.2 ExecutionListener

- **用途**：用于监听流程执行过程中的各种**事件**
- **特点**：作为流程的**监听器**存在，不阻塞流程执行
- **执行时机**：在特定事件发生时触发（如 start、end、take）

---

## 2. 核心区别对比

| 对比项 | JavaDelegate | ExecutionListener |
|--------|-------------|-------------------|
| **接口定义** | `execute(DelegateExecution execution)` | `notify(DelegateExecution execution)` |
| **本质** | 流程节点（Service Task） | 事件监听器 |
| **在流程中的位置** | 作为流程中的一个节点 | 附加在节点或流程上 |
| **是否阻塞流程** | ✅ 是，流程会等待执行完成 | ❌ 否，异步执行，不阻塞 |
| **执行时机** | 流程执行到该节点时 | 特定事件发生时（start/end/take） |
| **配置位置** | 服务任务节点 | 任何节点、流程、顺序流 |
| **异常处理** | 异常会中断流程执行 | 异常通常不会中断流程（可配置） |
| **返回值** | 无返回值 | 无返回值 |
| **使用频率** | 每个服务任务使用一次 | 可以在多个地方配置多个监听器 |

---

## 3. 使用场景对比

### 3.1 JavaDelegate 适用场景

#### ✅ 适合使用 JavaDelegate 的场景：

1. **业务逻辑处理**
   - 需要执行具体的业务操作
   - 需要调用外部系统或服务
   - 需要处理数据转换或计算

2. **作为流程节点**
   - 需要在流程图中显示为一个节点
   - 需要明确表示这是一个业务处理步骤

3. **需要阻塞等待**
   - 需要等待业务逻辑执行完成后再继续流程
   - 需要确保业务逻辑执行成功才能继续

#### 📝 项目中的实际例子：

```java
// ApprovalIsEndListener - 审批结束处理
public class ApprovalIsEndListener implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // 处理审批结果
        // 调用业务服务
        // 更新业务状态
    }
}
```

**配置方式**：在流程设计器中，将服务任务节点的"类"属性设置为 `com.skyeye.listener.ApprovalIsEndListener`

### 3.2 ExecutionListener 适用场景

#### ✅ 适合使用 ExecutionListener 的场景：

1. **事件监听和日志记录**
   - 记录流程执行日志
   - 监控流程执行情况
   - 统计流程数据

2. **非阻塞的业务处理**
   - 发送通知（不阻塞流程）
   - 记录审计日志
   - 更新统计信息

3. **在多个时机执行**
   - 节点开始时执行（start 事件）
   - 节点结束时执行（end 事件）
   - 顺序流触发时执行（take 事件）

4. **会签节点监控**
   - 监听会签节点的执行情况
   - 统计会签进度
   - 在会签完成时执行逻辑

#### 📝 项目中的实际例子：

```java
// MultiInstanceloopListenerDemo - 会签节点监听
public class MultiInstanceloopListenerDemo implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        // 记录会签执行情况
        // 统计会签进度
        // 发送通知（可选）
    }
}
```

**配置方式**：在流程设计器中，在节点的"执行监听器"中添加监听器，选择事件类型（start/end）

---

## 4. 配置方式对比

### 4.1 JavaDelegate 配置

#### 在流程设计器中配置：

1. **创建服务任务节点**
   - 从工具栏拖拽"服务任务"到画布
   - 或选择已有节点，修改类型为"服务任务"

2. **配置服务任务**
   - 选中服务任务节点
   - 在属性面板中找到"类"（Class）属性
   - 填写完整类名：`com.skyeye.listener.ApprovalIsEndListener`

3. **配置字段（可选）**
   - 如果需要传递参数，可以在"字段"（Fields）中配置
   - 例如：字段名 `state`，类型 `string`，值 `pass`

#### BPMN XML 配置：

```xml
<serviceTask id="approvalEndTask" name="审批结束处理" 
             flowable:class="com.skyeye.listener.ApprovalIsEndListener">
  <extensionElements>
    <flowable:field name="state">
      <flowable:string>pass</flowable:string>
    </flowable:field>
  </extensionElements>
</serviceTask>
```

### 4.2 ExecutionListener 配置

#### 在流程设计器中配置：

1. **选择节点或流程**
   - 选中需要添加监听器的节点（用户任务、会签节点等）
   - 或选择整个流程

2. **添加执行监听器**
   - 在属性面板中找到"执行监听器"（Execution Listeners）
   - 点击"+"按钮添加监听器

3. **配置监听器**
   - **事件类型**：选择 `start`、`end` 或 `take`
   - **监听器类型**：选择 `Java class`
   - **类名**：填写 `com.skyeye.activiti.listener.MultiInstanceloopListenerDemo`

#### BPMN XML 配置：

```xml
<!-- 在节点上配置 -->
<userTask id="countersignTask" name="会签审批">
  <extensionElements>
    <flowable:executionListener 
        event="start" 
        class="com.skyeye.activiti.listener.MultiInstanceloopListenerDemo"/>
    <flowable:executionListener 
        event="end" 
        class="com.skyeye.activiti.listener.MultiInstanceloopListenerDemo"/>
  </extensionElements>
</userTask>

<!-- 在流程上配置 -->
<process id="myProcess" name="我的流程">
  <extensionElements>
    <flowable:executionListener 
        event="start" 
        class="com.skyeye.activiti.listener.ProcessStartListener"/>
  </extensionElements>
</process>
```

---

## 5. 代码实现对比

### 5.1 JavaDelegate 实现

```java
package com.skyeye.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.common.engine.api.delegate.Expression;

public class ApprovalIsEndListener implements JavaDelegate {
    
    // 可以通过字段注入参数
    private Expression state;
    
    @Override
    public void execute(DelegateExecution execution) {
        // 1. 获取流程变量
        String processInstanceId = execution.getProcessInstanceId();
        
        // 2. 获取字段参数
        String result = (String) state.getValue(execution);
        
        // 3. 执行业务逻辑（这里是主要逻辑）
        // - 调用业务服务
        // - 更新数据库
        // - 发送通知等
        
        // 4. 设置流程变量（可选）
        execution.setVariable("approvalResult", result);
        
        // 注意：如果这里抛出异常，流程会中断
    }
}
```

**特点**：
- 方法名：`execute`
- 主要职责：执行业务逻辑
- 异常处理：异常会中断流程
- 可以注入字段参数

### 5.2 ExecutionListener 实现

```java
package com.skyeye.activiti.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiInstanceloopListenerDemo implements ExecutionListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiInstanceloopListenerDemo.class);
    
    @Override
    public void notify(DelegateExecution execution) {
        // 1. 获取事件类型
        String eventName = execution.getEventName(); // start 或 end
        
        // 2. 获取流程信息
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        
        // 3. 记录日志或执行轻量级操作
        LOGGER.info("流程节点 {} 触发 {} 事件", activityId, eventName);
        
        // 4. 可以获取和设置流程变量
        Object variable = execution.getVariable("variableName");
        execution.setVariable("newVariable", "value");
        
        // 注意：通常不在这里执行耗时操作
        // 异常通常不会中断流程（取决于配置）
    }
}
```

**特点**：
- 方法名：`notify`
- 主要职责：监听和记录
- 异常处理：异常通常不会中断流程
- 不能直接注入字段参数（需要通过流程变量传递）

---

## 6. 项目中的实际应用

### 6.1 JavaDelegate 应用示例

#### 示例：ApprovalIsEndListener（审批结束处理）

**文件位置**：`flowable-main/src/main/java/com/skyeye/listener/ApprovalIsEndListener.java`

**用途**：处理审批结束后的业务逻辑

**配置位置**：服务任务节点

**执行流程**：
```
用户任务（审批） → 网关（判断） → 服务任务（ApprovalIsEndListener） → 结束事件
```

**代码特点**：
- 实现了 `JavaDelegate` 接口
- 使用 `execute` 方法
- 可以注入字段参数（`state`）
- 调用业务服务处理审批结果
- 异常会中断流程

### 6.2 ExecutionListener 应用示例

#### 示例：MultiInstanceloopListenerDemo（会签节点监听）

**文件位置**：`flowable-main/src/main/java/com/skyeye/activiti/listener/MultiInstanceloopListenerDemo.java`

**用途**：监听会签节点的执行情况

**配置位置**：会签节点的执行监听器

**执行流程**：
```
会签节点开始 → start 事件触发 → notify 方法执行（记录日志）
会签节点结束 → end 事件触发 → notify 方法执行（统计进度）
```

**代码特点**：
- 实现了 `ExecutionListener` 接口
- 使用 `notify` 方法
- 记录会签执行日志
- 统计会签进度
- 异常不会中断流程

---

## 7. 如何选择

### 7.1 选择 JavaDelegate 的情况

✅ **选择 JavaDelegate，如果：**

1. **需要作为流程节点**
   - 需要在流程图中明确显示为一个处理步骤
   - 需要用户知道这个业务处理的存在

2. **需要阻塞执行**
   - 必须等待业务逻辑执行完成
   - 业务逻辑执行失败应该中断流程

3. **需要执行主要业务逻辑**
   - 这是流程的核心业务处理
   - 需要调用外部系统或服务
   - 需要更新数据库或业务状态

4. **需要传递参数**
   - 需要通过字段（Fields）传递配置参数
   - 参数在流程设计时确定

**典型场景**：
- 审批结果处理
- 数据同步
- 外部系统调用
- 业务状态更新

### 7.2 选择 ExecutionListener 的情况

✅ **选择 ExecutionListener，如果：**

1. **只需要监听和记录**
   - 不需要阻塞流程执行
   - 主要用于记录日志或统计

2. **需要在多个时机执行**
   - 需要在节点开始和结束时都执行
   - 需要在多个节点上使用相同的逻辑

3. **执行轻量级操作**
   - 记录日志
   - 发送通知（异步）
   - 更新统计信息

4. **不影响主流程**
   - 即使监听器失败，也不应该影响流程执行
   - 是辅助功能，不是核心业务

**典型场景**：
- 流程执行日志记录
- 会签节点进度监控
- 审计日志记录
- 统计信息更新

### 7.3 决策流程图

```
开始
  ↓
需要作为流程节点显示？
  ├─ 是 → 选择 JavaDelegate
  └─ 否 ↓
需要阻塞流程等待执行完成？
  ├─ 是 → 选择 JavaDelegate
  └─ 否 ↓
需要执行主要业务逻辑？
  ├─ 是 → 选择 JavaDelegate
  └─ 否 ↓
只需要监听和记录？
  ├─ 是 → 选择 ExecutionListener
  └─ 否 → 根据具体情况选择
```

### 7.4 组合使用示例

在实际项目中，可以同时使用两种方式：

```java
// 流程示例：
// 1. 用户任务（审批）
//    - 执行监听器（start）：记录审批开始日志
//    - 执行监听器（end）：记录审批结束日志
// 2. 服务任务（处理审批结果）
//    - JavaDelegate：执行审批结果处理业务逻辑
// 3. 结束事件
//    - 执行监听器（start）：记录流程结束日志
```

**配置示例**：

```xml
<userTask id="approvalTask" name="审批">
  <!-- ExecutionListener：记录日志 -->
  <extensionElements>
    <flowable:executionListener event="start" 
        class="com.skyeye.listener.TaskStartListener"/>
    <flowable:executionListener event="end" 
        class="com.skyeye.listener.TaskEndListener"/>
  </extensionElements>
</userTask>

<serviceTask id="processResult" name="处理结果">
  <!-- JavaDelegate：执行业务逻辑 -->
  <extensionElements>
    <flowable:class>com.skyeye.listener.ApprovalIsEndListener</flowable:class>
  </extensionElements>
</serviceTask>
```

---

## 8. 总结对比表

| 特性 | JavaDelegate | ExecutionListener |
|------|-------------|-------------------|
| **接口方法** | `execute()` | `notify()` |
| **本质** | 流程节点 | 事件监听器 |
| **阻塞性** | 阻塞流程 | 不阻塞流程 |
| **配置位置** | 服务任务节点 | 任何节点/流程 |
| **执行时机** | 节点执行时 | 事件发生时 |
| **异常影响** | 中断流程 | 通常不中断 |
| **参数注入** | 支持字段注入 | 不支持（用变量） |
| **使用频率** | 每个节点一次 | 可多个监听器 |
| **典型用途** | 业务逻辑处理 | 日志记录、监控 |
| **项目示例** | ApprovalIsEndListener | MultiInstanceloopListenerDemo |

---

## 9. 常见问题

### Q1: 可以在 ExecutionListener 中执行耗时操作吗？

**A**: 不推荐。ExecutionListener 通常用于轻量级操作。如果需要执行耗时操作，应该：
- 使用 JavaDelegate（如果必须阻塞等待）
- 或使用异步任务（如果不需要阻塞）

### Q2: JavaDelegate 可以配置多个吗？

**A**: 一个服务任务节点只能配置一个 JavaDelegate 类。如果需要执行多个操作，可以在一个 JavaDelegate 中调用多个服务。

### Q3: ExecutionListener 可以配置多个吗？

**A**: 可以。可以在同一个节点上配置多个 ExecutionListener，监听不同的事件（start、end、take）。

### Q4: 两者可以互相替代吗？

**A**: 不能完全替代。它们有不同的用途：
- JavaDelegate 用于执行业务逻辑（作为节点）
- ExecutionListener 用于监听事件（作为监听器）

### Q5: 如何传递参数？

**A**: 
- **JavaDelegate**：通过字段（Fields）配置，在类中注入 `Expression` 类型的字段
- **ExecutionListener**：通过流程变量传递，使用 `execution.getVariable()` 获取

---

**文档版本**：v1.0  
**最后更新**：2025-01-XX  
**维护者**：skyeye云系列

