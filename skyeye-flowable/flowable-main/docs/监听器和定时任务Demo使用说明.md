# 监听器和定时任务 Demo 使用说明文档

## 目录
- [1. 概述](#1-概述)
- [2. 定时器边界事件处理 Demo](#2-定时器边界事件处理-demo)
- [3. 任务监听器 Demo](#3-任务监听器-demo)
- [4. 超期任务查询和处理 Demo](#4-超期任务查询和处理-demo)
- [5. 快速开始](#5-快速开始)
- [6. 常见问题](#6-常见问题)

---

## 1. 概述

本文档介绍了三个 Flowable 工作流引擎的 Demo 示例，这些 Demo 展示了如何通过代码实现工作流中的常见需求：

1. **定时器边界事件处理** - 处理任务超时后的业务逻辑
2. **任务监听器** - 监听任务生命周期事件
3. **超期任务查询和处理** - 定期查询并处理超期任务

### 1.1 Demo 文件位置

所有 Demo 文件位于：`flowable-main/src/main/java/com/skyeye/activiti/listener/`

| 文件名 | 说明 | 接口/类型 |
|--------|------|-----------|
| `TimerBoundaryEventHandlerDemo.java` | 定时器边界事件处理 | `ExecutionListener` |
| `TaskListenerDemo.java` | 任务监听器 | `TaskListener` |
| `OverdueTaskQueryHandlerDemo.java` | 超期任务查询处理器 | 普通类 |
| `OverdueTaskSchedulerDemo.java` | 超期任务定时调度器 | `@Component` + `@Scheduled` |

---

## 2. 定时器边界事件处理 Demo

### 2.1 功能说明

**TimerBoundaryEventHandlerDemo** 用于处理定时器边界事件触发后的业务逻辑。

**使用场景**：
- 任务超时后自动发送邮件通知
- 任务超时后自动转派给其他人
- 任务超时后自动升级处理
- 任务超时后执行自定义业务逻辑

**重要提示**：
- 定时器边界事件本身可以在流程建模器中配置（无需代码）
- 但触发后的业务逻辑（如发送邮件、自动转派）需要在监听器中编写代码实现
- Flowable 本身不提供内置的到期自动处理功能

### 2.2 配置步骤

#### 步骤 1：在流程设计器中配置定时器边界事件

1. **打开流程设计器**
   - 创建新流程或编辑已有流程
   - 打开流程设计器

2. **创建用户任务**
   - 从工具栏拖拽"用户任务"到画布
   - 配置任务名称和负责人

3. **添加定时器边界事件**
   - 从工具栏拖拽"定时器边界事件"到用户任务节点边缘
   - 定时器边界事件会自动附加到任务节点上

4. **配置定时器时间**
   - 选中定时器边界事件
   - 在属性面板中配置定时器：
     - **循环时间**：`P2D`（2天后）或 `PT2H`（2小时后）
     - **开始时间**：`2025-01-31T18:00:00`（绝对时间）
     - **持续时间**：`PT30M`（30分钟后）

#### 步骤 2：连接服务任务节点

**重要说明**：定时器边界事件的属性面板中**没有"执行监听器"选项**。定时器边界事件触发后，需要连接到下一个节点（通常是服务任务），业务逻辑在服务任务中实现。

**流程图示例**：
```
用户任务（审批）
    │
    ├─[正常完成]→ 网关 → 结束
    │
    └─[定时器边界事件]→ 服务任务（超时处理）→ 结束
         (2天后触发)      (TimerBoundaryEventHandlerDemo)
```

1. **创建服务任务节点**
   - 从工具栏拖拽"服务任务"到画布
   - 将定时器边界事件连接到服务任务节点（从定时器边界事件拖拽箭头到服务任务）

2. **配置服务任务**
   - 选中服务任务节点
   - 在属性面板中找到"类"（Class）属性
   - 填写完整类名：`com.skyeye.activiti.listener.TimerBoundaryEventHandlerDemo`

3. **配置服务任务字段（可选）**
   - 如果需要传递参数，可以在"字段"（Fields）中配置
   - 例如：字段名 `processInstanceId`，类型 `string`，值 `${processInstanceId}`

4. **保存并发布**
   - 保存流程模型
   - 发布流程定义

### 2.3 BPMN XML 配置示例

```xml
<userTask id="approvalTask" name="审批任务">
  <!-- 定时器边界事件 -->
  <boundaryEvent id="timeoutEvent" attachedToRef="approvalTask">
    <timerEventDefinition>
      <timeDuration>P2D</timeDuration>
    </timerEventDefinition>
  </boundaryEvent>
</userTask>

<!-- 定时器边界事件连接的服务任务 -->
<serviceTask id="timeoutHandlerTask" name="超时处理" 
             flowable:class="com.skyeye.activiti.listener.TimerBoundaryEventHandlerDemo">
  <!-- 可选：配置字段参数 -->
  <extensionElements>
    <flowable:field name="processInstanceId">
      <flowable:expression>${processInstanceId}</flowable:expression>
    </flowable:field>
  </extensionElements>
</serviceTask>

<!-- 顺序流：从定时器边界事件到服务任务 -->
<sequenceFlow id="timeoutFlow" sourceRef="timeoutEvent" targetRef="timeoutHandlerTask"/>
```

### 2.4 代码实现说明

**重要变更**：由于定时器边界事件不支持执行监听器，业务逻辑需要在服务任务中实现。因此，`TimerBoundaryEventHandlerDemo` 应该实现 `JavaDelegate` 接口，而不是 `ExecutionListener` 接口。

```java
public class TimerBoundaryEventHandlerDemo implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        // 1. 获取流程和任务信息
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        
        // 2. 获取被中断的任务信息（如果定时器是中断型的）
        // 注意：如果定时器边界事件配置了"取消活动"，原任务会被取消
        
        // 3. 执行业务逻辑
        // - 发送超时通知
        // - 自动转派任务
        // - 升级处理
        // - 记录日志
    }
}
```

**注意**：如果定时器边界事件配置了"取消活动"（cancelActivity=true），原任务会被自动取消。如果需要获取原任务信息，可以通过流程变量传递。

### 2.5 扩展开发

在 `notify` 方法中，你可以实现以下业务逻辑：

1. **发送超时通知**
   ```java
   private void sendTimeoutNotification(String processInstanceId, String assignee, String taskName) {
       // TODO: 实现邮件发送逻辑
       // EmailService emailService = SpringUtils.getBean(EmailService.class);
       // emailService.sendTimeoutNotification(assignee, taskName, processInstanceId);
   }
   ```

2. **自动转派任务**
   ```java
   private void autoReassignTask(String taskId, String currentAssignee) {
       // TODO: 实现任务转派逻辑
       // TaskService taskService = SpringUtils.getBean(TaskService.class);
       // String newAssignee = getNextAssignee(currentAssignee);
       // taskService.setAssignee(taskId, newAssignee);
   }
   ```

3. **升级处理**
   ```java
   private void escalateTask(String processInstanceId, String taskId) {
       // TODO: 实现升级处理逻辑
       // 1. 通知上级领导
       // 2. 提高任务优先级
       // 3. 记录升级日志
   }
   ```

---

## 3. 任务监听器 Demo

### 3.1 功能说明

**TaskListenerDemo** 用于监听任务的生命周期事件，在任务创建、分配、完成、删除时执行自定义逻辑。

**使用场景**：
- 任务创建时自动指派负责人
- 任务创建时发送通知
- 任务完成时更新业务状态
- 任务分配时记录日志
- 任务超时时自动处理

**重要提示**：
- 任务监听器类需要在建模器中配置，但监听器类本身需要编写代码实现
- 任务监听器可以访问任务信息（DelegateTask），而不是执行信息（DelegateExecution）
- 任务监听器主要用于任务相关的操作，执行监听器主要用于流程执行相关的操作

### 3.2 配置步骤

#### 步骤 1：在流程设计器中配置任务监听器

1. **打开流程设计器**
   - 创建新流程或编辑已有流程
   - 打开流程设计器

2. **选择用户任务节点**
   - 在画布上点击用户任务节点

3. **添加任务监听器**
   - 在右侧属性面板中找到"任务监听器"（Task Listeners）
   - 点击"+"按钮添加监听器

4. **配置监听器参数**
   - **事件类型**：选择以下之一：
     - `create` - 任务创建时触发
     - `assignment` - 任务分配时触发
     - `complete` - 任务完成时触发
     - `delete` - 任务删除时触发
   - **监听器类型**：选择 `Java class`
   - **类名**：填写 `com.skyeye.activiti.listener.TaskListenerDemo`

5. **保存并发布**
   - 保存流程模型
   - 发布流程定义

### 3.3 BPMN XML 配置示例

```xml
<userTask id="approvalTask" name="审批任务">
  <extensionElements>
    <!-- 任务创建时触发 -->
    <flowable:taskListener 
        event="create" 
        class="com.skyeye.activiti.listener.TaskListenerDemo"/>
    
    <!-- 任务分配时触发 -->
    <flowable:taskListener 
        event="assignment" 
        class="com.skyeye.activiti.listener.TaskListenerDemo"/>
    
    <!-- 任务完成时触发 -->
    <flowable:taskListener 
        event="complete" 
        class="com.skyeye.activiti.listener.TaskListenerDemo"/>
  </extensionElements>
</userTask>
```

### 3.4 事件类型说明

| 事件类型 | 触发时机 | 典型用途 |
|---------|---------|---------|
| `create` | 任务创建后立即触发 | 自动指派负责人、设置优先级、发送创建通知 |
| `assignment` | 任务分配给某人时触发 | 发送分配通知、记录分配日志、更新统计 |
| `complete` | 任务完成时触发（完成之前） | 更新业务状态、发送完成通知、记录完成日志 |
| `delete` | 任务删除时触发 | 清理任务数据、记录删除日志 |

### 3.5 代码实现说明

监听器实现了 `TaskListener` 接口，在任务事件触发时会调用 `notify` 方法：

```java
@Override
public void notify(DelegateTask delegateTask) {
    String eventName = delegateTask.getEventName(); // create/assignment/complete/delete
    String taskId = delegateTask.getId();
    String assignee = delegateTask.getAssignee();
    
    // 根据事件类型执行不同的业务逻辑
    switch (eventName) {
        case "create":
            handleTaskCreate(delegateTask);
            break;
        case "assignment":
            handleTaskAssignment(delegateTask);
            break;
        // ...
    }
}
```

### 3.6 DelegateTask 常用方法

```java
// 获取任务信息
String taskId = delegateTask.getId();
String taskName = delegateTask.getName();
String assignee = delegateTask.getAssignee();
Date dueDate = delegateTask.getDueDate();
String processInstanceId = delegateTask.getProcessInstanceId();

// 设置任务信息
delegateTask.setAssignee("userId");
delegateTask.setPriority(50);
delegateTask.setDueDate(new Date());

// 获取和设置任务变量
Object variable = delegateTask.getVariable("变量名");
delegateTask.setVariable("变量名", "值");
```

### 3.7 扩展开发示例

#### 示例 1：任务创建时自动指派负责人

```java
private void handleTaskCreate(DelegateTask delegateTask) {
    // 根据业务规则计算负责人
    String autoAssignee = calculateAssignee(delegateTask);
    
    // 自动指派
    delegateTask.setAssignee(autoAssignee);
    
    // 设置任务优先级
    delegateTask.setPriority(50);
    
    // 设置到期时间（2天后）
    Date dueDate = new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000L);
    delegateTask.setDueDate(dueDate);
}
```

#### 示例 2：任务完成时更新业务状态

```java
private void handleTaskComplete(DelegateTask delegateTask) {
    // 获取任务完成结果
    String result = (String) delegateTask.getVariable("result");
    
    // 更新业务状态
    BusinessService businessService = SpringUtils.getBean(BusinessService.class);
    businessService.updateStatus(
        delegateTask.getProcessInstanceId(), 
        "COMPLETED", 
        result
    );
}
```

---

## 4. 超期任务查询和处理 Demo

### 4.1 功能说明

**OverdueTaskQueryHandlerDemo** 和 **OverdueTaskSchedulerDemo** 用于定期查询和处理超期任务。

**使用场景**：
- 定期查询超期任务并发送提醒
- 自动处理超期任务（转派、升级等）
- 统计超期任务数据
- 生成超期任务报告

**重要提示**：
- 需要编写定时任务代码，定期查询超期任务并处理
- Flowable 本身不提供内置的超期任务自动处理功能
- 建议使用定时任务框架，而不是在监听器中实现

### 4.2 配置步骤

#### 步骤 1：启用 Spring 定时任务

在 Spring Boot 启动类上添加 `@EnableScheduling` 注解：

```java
@SpringBootApplication
@EnableScheduling  // 启用定时任务
public class SkyFlowableApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyFlowableApplication.class, args);
    }
}
```

#### 步骤 2：配置 TaskService 注入

确保 `OverdueTaskQueryHandlerDemo` 可以注入 `TaskService`：

```java
@Component
public class OverdueTaskQueryHandlerDemo {
    
    @Autowired
    private TaskService taskService;
    
    // 或者通过构造函数注入
    public OverdueTaskQueryHandlerDemo(TaskService taskService) {
        this.taskService = taskService;
    }
}
```

#### 步骤 3：配置定时任务执行频率

在 `OverdueTaskSchedulerDemo` 中调整执行频率：

```java
@Scheduled(fixedRate = 300000) // 每5分钟执行一次（300000毫秒 = 5分钟）
public void checkOverdueTasks() {
    overdueTaskHandler.queryAndProcessOverdueTasks();
}
```

### 4.3 定时任务配置方式

#### 方式 1：固定频率执行

```java
@Scheduled(fixedRate = 300000) // 每5分钟执行一次
public void checkOverdueTasks() {
    // 执行逻辑
}
```

#### 方式 2：固定延迟执行

```java
@Scheduled(fixedDelay = 300000) // 上次执行完成后延迟5分钟再执行
public void checkOverdueTasks() {
    // 执行逻辑
}
```

#### 方式 3：Cron 表达式

```java
@Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
public void checkOverdueTasks() {
    // 执行逻辑
}
```

### 4.4 Cron 表达式示例

| Cron 表达式 | 说明 |
|------------|------|
| `"0 0/5 * * * ?"` | 每5分钟执行一次 |
| `"0 0 * * * ?"` | 每小时执行一次 |
| `"0 0 9 * * ?"` | 每天上午9点执行 |
| `"0 0 9,17 * * ?"` | 每天上午9点和下午5点执行 |
| `"0 0 9 ? * MON-FRI"` | 工作日上午9点执行 |
| `"0 0 0 1 * ?"` | 每月1号凌晨执行 |

### 4.5 代码实现说明

#### 查询超期任务

```java
private List<Task> queryOverdueTasks() {
    Date now = new Date();
    return taskService.createTaskQuery()
        .active()  // 只查询活跃任务
        .dueBefore(now)  // 查询到期时间在当前时间之前的任务
        .list();
}
```

#### 处理超期任务

```java
private void processOverdueTask(Task task) {
    // 1. 计算超期时长
    long overdueHours = calculateOverdueHours(task.getDueDate());
    
    // 2. 根据超期时长决定处理方式
    if (overdueHours > 24) {
        // 超期超过24小时，自动升级
        escalateOverdueTask(task, overdueHours);
    } else if (overdueHours > 12) {
        // 超期超过12小时，发送紧急通知
        sendUrgentNotification(task, overdueHours);
    } else {
        // 超期12小时内，只发送提醒
        sendNormalReminder(task, overdueHours);
    }
}
```

### 4.6 扩展开发示例

#### 示例 1：发送超期提醒

```java
private void sendOverdueReminder(Task task, long overdueHours) {
    NotificationService notificationService = SpringUtils.getBean(NotificationService.class);
    notificationService.sendOverdueReminder(
        task.getAssignee(), 
        task.getName(), 
        overdueHours
    );
}
```

#### 示例 2：自动升级超期任务

```java
private void escalateOverdueTask(Task task, long overdueHours) {
    // 1. 转派给上级领导
    TaskService taskService = SpringUtils.getBean(TaskService.class);
    String supervisor = getSupervisor(task.getAssignee());
    taskService.setAssignee(task.getId(), supervisor);
    
    // 2. 提高任务优先级
    taskService.setPriority(task.getId(), 100);
    
    // 3. 通知流程发起人
    NotificationService notificationService = SpringUtils.getBean(NotificationService.class);
    notificationService.notifyProcessInitiator(task.getProcessInstanceId());
}
```

#### 示例 3：生成统计报告

```java
public void generateOverdueTaskReport() {
    // 1. 查询所有超期任务
    List<Task> overdueTasks = queryOverdueTasks();
    
    // 2. 按负责人统计
    Map<String, Long> assigneeStats = overdueTasks.stream()
        .collect(Collectors.groupingBy(Task::getAssignee, Collectors.counting()));
    
    // 3. 按流程统计
    Map<String, Long> processStats = overdueTasks.stream()
        .collect(Collectors.groupingBy(Task::getProcessDefinitionId, Collectors.counting()));
    
    // 4. 生成报告
    OverdueTaskReport report = new OverdueTaskReport();
    report.setTotalCount(overdueTasks.size());
    report.setAssigneeStats(assigneeStats);
    report.setProcessStats(processStats);
    report.setGenerateTime(new Date());
    
    // 5. 保存或发送报告
    reportService.save(report);
}
```

---

## 5. 快速开始

### 5.1 使用定时器边界事件处理

1. **配置流程**
   - 在流程设计器中为用户任务添加定时器边界事件
   - 配置定时器时间（如：`P2D` 表示2天后）
   - **重要**：从定时器边界事件连接一个服务任务节点
   - 在服务任务节点的"类"属性中配置 `TimerBoundaryEventHandlerDemo`

2. **完善业务逻辑**
   - 打开 `TimerBoundaryEventHandlerDemo.java`
   - 在 `execute` 方法中实现业务逻辑
   - 例如：发送邮件、转派任务、升级处理等

3. **测试**
   - 启动流程实例
   - 等待定时器触发
   - 查看日志确认服务任务是否执行

### 5.2 使用任务监听器

1. **配置流程**
   - 在流程设计器中为用户任务添加任务监听器
   - 选择事件类型（create/assignment/complete/delete）
   - 配置类名为 `TaskListenerDemo`

2. **完善业务逻辑**
   - 打开 `TaskListenerDemo.java`
   - 在对应的处理方法中实现业务逻辑
   - 例如：自动指派、发送通知、更新状态等

3. **测试**
   - 启动流程实例
   - 执行任务操作（创建、分配、完成等）
   - 查看日志确认监听器是否执行

### 5.3 使用超期任务查询

1. **启用定时任务**
   - 在 Spring Boot 启动类上添加 `@EnableScheduling` 注解

2. **配置依赖注入**
   - 确保 `OverdueTaskQueryHandlerDemo` 可以注入 `TaskService`
   - 确保 `OverdueTaskSchedulerDemo` 可以注入 `OverdueTaskQueryHandlerDemo`

3. **调整执行频率**
   - 在 `OverdueTaskSchedulerDemo` 中调整 `@Scheduled` 注解的参数

4. **完善业务逻辑**
   - 打开 `OverdueTaskQueryHandlerDemo.java`
   - 在 TODO 标记的方法中实现业务逻辑
   - 例如：发送提醒、自动升级、生成报告等

5. **测试**
   - 启动应用
   - 创建一些超期任务
   - 等待定时任务执行
   - 查看日志确认是否处理

---

## 6. 常见问题

### Q1: 定时器边界事件不触发？

**A**: 检查以下几点：
1. 确保定时器时间配置正确（ISO-8601 格式）
2. 确保流程实例正在运行
3. 确保 Flowable 的作业执行器（Job Executor）已激活
4. 检查系统时间是否正确
5. 确保定时器边界事件已连接到服务任务节点

### Q1-1: 定时器边界事件属性面板中没有"执行监听器"选项？

**A**: 这是正常的。定时器边界事件的属性面板中确实没有"执行监听器"选项。正确的配置方式是：
1. 定时器边界事件触发后，需要连接到下一个节点（通常是服务任务）
2. 在服务任务节点的"类"属性中配置处理类（如 `TimerBoundaryEventHandlerDemo`）
3. 处理类应该实现 `JavaDelegate` 接口，而不是 `ExecutionListener` 接口

### Q2: 任务监听器不执行？

**A**: 检查以下几点：
1. 确保监听器类名配置正确（包括包名）
2. 确保监听器类已编译并打包到部署包中
3. 确保事件类型选择正确
4. 检查日志是否有异常信息

### Q3: 定时任务不执行？

**A**: 检查以下几点：
1. 确保在启动类上添加了 `@EnableScheduling` 注解
2. 确保定时任务类添加了 `@Component` 注解
3. 确保定时任务方法添加了 `@Scheduled` 注解
4. 检查 Cron 表达式是否正确

### Q4: 如何获取 Spring Bean？

**A**: 使用工具类 `SpringUtils`：

```java
import com.skyeye.common.util.SpringUtils;

// 获取 Bean
TaskService taskService = SpringUtils.getBean(TaskService.class);
NotificationService notificationService = SpringUtils.getBean(NotificationService.class);
```

### Q5: 监听器异常会影响流程吗？

**A**: 
- **ExecutionListener**：异常通常不会中断流程（取决于配置）
- **TaskListener**：异常可能会影响任务操作，建议处理异常
- **JavaDelegate**：异常会中断流程执行

建议在所有监听器中添加异常处理：

```java
try {
    // 业务逻辑
} catch (Exception e) {
    LOGGER.error("监听器执行异常", e);
    // 根据业务需求决定是否抛出异常
}
```

### Q6: 如何调试监听器？

**A**: 
1. 添加详细日志，记录关键信息
2. 使用断点调试（在 IDE 中设置断点）
3. 检查流程变量和任务变量
4. 查看 Flowable 引擎日志

---

## 7. 总结

本文档介绍了三个 Flowable 工作流引擎的 Demo：

1. **TimerBoundaryEventHandlerDemo** - 处理定时器边界事件触发后的业务逻辑
2. **TaskListenerDemo** - 监听任务生命周期事件
3. **OverdueTaskQueryHandlerDemo** - 查询和处理超期任务

这些 Demo 展示了如何通过代码实现工作流中的常见需求，可以作为实际项目开发的参考。

### 7.1 关键要点

- **定时器边界事件**：可以在流程建模器中配置，但触发后的业务逻辑需要代码实现
- **任务监听器**：需要在建模器中配置监听器类，但监听器类本身需要编写代码
- **超期任务查询**：需要编写定时任务代码，定期查询超期任务并处理

### 7.2 下一步

1. 根据实际需求完善 TODO 部分的业务逻辑
2. 集成到实际项目中
3. 添加单元测试
4. 配置监控和告警

---

**文档版本**：v1.0  
**最后更新**：2025-01-XX  
**维护者**：skyeye云系列

