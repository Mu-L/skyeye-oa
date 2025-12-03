#  :tw-1f308:  Dromara-skyeye

> 智能制造一体化，采用Springboot(微服务) + UNI-APP + Ant Design Vue的零代码平台开发模式。包含50多种电子流程，CRM、PM、ERP、MES、ADM、OA、EHR、AI、项目、商城、财务、多班次考勤、薪资、招聘、云售后、论坛、问卷、报表设计、工作流、Saas等功能。打造全网首套零代码、功能最全、价格最优惠的智能制造行业供应链一体化管理软件。

# 为我投票可获取免费体验账号：https://gitee.com/dromara/skyeye/issues/ID3GFF

**郑重声明：** 

**1. Skyeye会员（只有 `Skyeye会员` 才能获取完整源代码，扫描下方付款码成为会员吧）拿到源码后可进行学习、毕设、企业等使用。**

**2. [官方网站](http://ip.makerview.cn:30003/skyPortal)    [二开文档](https://articles.zsxq.com/id_xi3xhacte72g.html)     [二开视频教程](https://www.bilibili.com/video/BV1w34y1M7ZH/?spm_id_from=333.1387.collection.video_card.click&vd_source=714dd9434dc2ba981f2f47b7aa44be38)      [操作视频](https://www.bilibili.com/video/BV16mwVeKE4X/?vd_source=714dd9434dc2ba981f2f47b7aa44be38)      [功能清单](https://kdocs.cn/l/cbf2cgCLrUyz)**

**3. 基础功能包括但不限于：低代码、动态属性、布局设计器、工作流设计器、编码设计器、打印模板设计器、灵活性权限管控、代码生成器、多终端适配等**

**为什么推荐使用本项目？**

① 无论你是学生、个人与企业，使用时，都可以不保留作者、Copyright 信息。

② 代码全部开放，让你可以了解整个项目的架构设计。

③ 具备低代码、功能全面、快速便捷开发、无需重复的CRUD等优点，短时间内可完成一款系统的开发。

④ 体验地址：`Star`后在下方微信公众号里回复【体验】，会有具体的获取方式。

## 项目地址

- [Gitcode - 项目地址](https://gitcode.com/doc_wei/skyeye-oa)
- [Gitee - 项目地址](https://gitee.com/dromara/skyeye)

## 🐶 沟通交流

| 微信公众号(Skyeye智能制造云办公) | Skyeye云低代码咨询QQ | `Skyeye会员`加我微信，咨询加QQ，加微信意味着直接付款 | 扫码支付899，您可以选择先支付再加微信好友，也可以选择先加微信好友再支付。但是微信不闲聊，咨询请加QQ |
|:-------------------:|:-------------------:|:-------------------:|:-------------------:|
|   ![](images/mindMap/微信公众号.jpg) | ![](images/mindMap/qq2.jpg) | ![](images/mindMap/微信.jpg) | ![](images/mindMap/wechatPay.jpg) |

## Skyeye云整体规划图

![输入图片说明](images/mindMap/Skyeye%E4%BA%91%E7%9B%AE%E6%A0%87.jpeg)

## 项目框架介绍

### 环境依赖

| 依赖 | 版本 | 端口 |
|:---------------------:|:---------------------:|:---------------------:|
| Java | 1.8 | 无 |
| rocket MQ | 4.9.2 | 9876 |
| Redis | 5.0 / 6.0 | 6379 |
| nacos | 2.3.0 | 8848|
| MySQL | 5.7或更高版本，[点我配置](https://blog.csdn.net/qq_42175986/article/details/82384160) | 3308 |

##  :tw-1f31e:  架构介绍

![输入图片说明](images/mindMap/image11.png)

###  :jack_o_lantern:  技术选型

#### 后端技术:

| 框架 | 说明 | 版本 | 学习指南 |
|---|---|---|---|
| [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba)    | 微服务框架  | 2.1.0.RELEASE  | [文档](https://github.com/YunaiV/SpringBoot-Labs) |
| [Nacos](https://nacos.io/)   | 配置中心 & 注册中心  | 1.4.3  | [文档](https://nacos.io/docs/v1/what-is-nacos/)   |
| [RocketMQ](https://rocketmq.apache.org/zh/)  | 消息队列 | 4.0.0 | [文档](https://rocketmq.apache.org/zh/docs/4.x/)             |
| [Sentinel](https://github.com/alibaba/sentinel)  | 服务保障| 2.1.0.RELEASE  | [文档](https://zhuanlan.zhihu.com/p/681044230)             |
| [XXL Job](https://github.com/xuxueli/xxl-job) | 定时任务 | 2.3.0 | [文档](https://www.xuxueli.com/xxl-job/#google_vignette) |
| [Spring Cloud Zuul](https://cloud.spring.io/spring-cloud-netflix/multi/multi__router_and_filter_zuul.html)                | 服务网关             | 3.4.1       | [文档](https://www.jianshu.com/p/cf748031a08d) |
| [MySQL](https://www.mysql.com/cn/)                                                          | 数据库服务器           | 5.7 / 8.0+  |                                                                     |
| [Druid](https://github.com/alibaba/druid)                                                   | JDBC 连接池、监控组件    | 1.2.23      | [文档](https://zhuanlan.zhihu.com/p/555116830)      |
| [MyBatis Plus](https://baomidou.com/)                                                    | MyBatis 增强工具包    | 3.5.7       | [文档](https://baomidou.com/introduce/)              |
| [Redis](https://redis.io/)                                                                  | key-value 数据库    | 5.0 / 6.0   |                                                                     |
| [Flowable](https://github.com/flowable/flowable-engine)                                     | 工作流引擎            | 6.8.0       | [文档](https://doc.iocoder.cn/bpm/)                                   |
| [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin)                       | Spring Boot 监控平台 | 2.0.3      | [文档](https://blog.51cto.com/u_15916106/7063036)                |
| [hutool](https://www.hutool.cn/)                                                         | 一个小而全的Java工具类库    | 5.5.4 | [文档](https://doc.hutool.cn/pages/index/)            |
| [Lombok](https://projectlombok.org/)                                                        | 消除冗长的 Java 代码    | 1.16.22     | [文档](https://zhuanlan.zhihu.com/p/32779910)               |
| [JUnit](https://junit.org/junit5/)                                                          | Java 单元测试框架      | 4.12       | -                                                                   |

#### 前端技术：

| 框架 | 技术 | 版本 | 学习指南 |
|---|---|---|---|
|Vue 3 + JavaScript + Vite 5.0.0 + Ant Design Vue 4.2.6| **已发布** | - | - |
|[uni-app](https://uniapp.dcloud.net.cn/)|一个使用Vue.js开发所有前端应用的框架，开发者编写一套代码，可发布到iOS、Android、Web（响应式）、以及各种小程序、快应用等多个平台。| VUE3 |[文档](https://uniapp.dcloud.net.cn/component/)|

##  :tw-1f30f:  PC端效果图

### 基础内容
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|组件管理|![输入图片说明](images/show/base/20240802001image.png)|![输入图片说明](images/show/base/2024080202image.png)||
|布局/操作/属性管理|![输入图片说明](images/show/base/2024080203image.png)|![输入图片说明](images/show/base/2024080204image.png)|![输入图片说明](images/show/base/20240802005image.png)|
|菜单/角色/编码管理|![输入图片说明](images/show/base/20240802010image.png)|![输入图片说明](images/show/base/20240802011image.png)|![输入图片说明](images/show/base/20240802012image.png)|

### CRM
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|客户管理(包括合同、跟单、文档等)|![输入图片说明](images/show/crm/image.png)|![输入图片说明](images/show/crm/1image.png)|![输入图片说明](images/show/crm/2image.png)|
|客户管理(包括合同、跟单、文档等)|![输入图片说明](images/show/crm/3image.png)|![输入图片说明](images/show/crm/4image.png)|![输入图片说明](images/show/crm/5image.png)|
|报表分析|![输入图片说明](images/show/crm/6image.png)|![输入图片说明](images/show/crm/7image.png)|![输入图片说明](images/show/crm/8image.png)|

### ERP
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|商品管理 **(支持一物一码)** |![输入图片说明](images/show/erp/08image.png)|![输入图片说明](images/show/erp/07image.png)|![输入图片说明](images/show/erp/09image.png)|
|采购模块|![输入图片说明](images/show/erp/01image.png)|![输入图片说明](images/show/erp/02image.png)|![输入图片说明](images/show/erp/03image.png)|
|采购模块|![输入图片说明](images/show/erp/04image.png)|![输入图片说明](images/show/erp/05image.png)|![输入图片说明](images/show/erp/06image.png)|
|销售模块|![输入图片说明](images/show/erp/10image.png)|![输入图片说明](images/show/erp/11image.png)|![输入图片说明](images/show/erp/12image.png)|
|报表模块|![输入图片说明](images/show/erp/13image.png)|![输入图片说明](images/show/erp/14image.png)|![输入图片说明](images/show/erp/15image.png)|

### ERP仓库
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|其他单据管理|![输入图片说明](images/show/erpDepot/image.png)|![输入图片说明](images/show/erpDepot/1image.png)|![输入图片说明](images/show/erpDepot/2image.png)|
|仓库管理|![输入图片说明](images/show/erpDepot/3image.png)|![输入图片说明](images/show/erpDepot/4image.png)||
|盘点管理|![输入图片说明](images/show/erpDepot/5image.png)|![输入图片说明](images/show/erpDepot/6image.png)||
|出入库管理|![输入图片说明](images/show/erpDepot/7image.png)|![输入图片说明](images/show/erpDepot/8image.png)||
|商品条形码|![输入图片说明](images/show/erpDepot/9image.png)|![输入图片说明](images/show/erpDepot/10image.png)||

### MES生产
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|生产管理|![输入图片说明](images/show/mes/1image.png)|![输入图片说明](images/show/mes/2image.png)|![输入图片说明](images/show/mes/3image.png)|
|设置中心|![输入图片说明](images/show/mes/4image.png)|![输入图片说明](images/show/mes/5image.png)|![输入图片说明](images/show/mes/6image.png)|
|物料管理|![输入图片说明](images/show/mes/7image.png)|![输入图片说明](images/show/mes/8image.png)|![输入图片说明](images/show/mes/9image.png)|
|生产执行|![输入图片说明](images/show/mes/10image.png)|![输入图片说明](images/show/mes/11image.png)|![输入图片说明](images/show/mes/12image.png)|
|物料确认|![输入图片说明](images/show/mes/13image.png)|![输入图片说明](images/show/mes/14image.png)|![输入图片说明](images/show/mes/15image.png)|

### 行政办公
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|会议室模块|![输入图片说明](images/show/oa/1image.png)|![输入图片说明](images/show/oa/2image.png)||
|用品模块|![输入图片说明](images/show/oa/3image.png)|![输入图片说明](images/show/oa/4image.png)|![输入图片说明](images/show/oa/5image.png)|
|资产模块|![输入图片说明](images/show/oa/6image.png)|![输入图片说明](images/show/oa/7image.png)|![输入图片说明](images/show/oa/8image.png)|
|公文模块|![输入图片说明](images/show/oa/9image.png)|![输入图片说明](images/show/oa/10image.png)|![输入图片说明](images/show/oa/11image.png)|
|印章，证照，车辆|![输入图片说明](images/show/oa/12image.png)|![输入图片说明](images/show/oa/13image.png)|![输入图片说明](images/show/oa/14image.png)|

### 售后管理模块
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|工单管理|![输入图片说明](images/show/sealService/1image.png)|![输入图片说明](images/show/sealService/2image.png)||
|配件管理|![输入图片说明](images/show/sealService/3image.png)|![输入图片说明](images/show/sealService/4image.png)||
|工人管理|![输入图片说明](images/show/sealService/5image.png)|![输入图片说明](images/show/sealService/6image.png)|![输入图片说明](images/show/sealService/7image.png)|

##  :tw-1f30f:  移动端效果图

> 移动端和PC端功能类似，这里不截那么多图拉。

### 基础模块
| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/20240730image.png)|![输入图片说明](images/show/phone/2024073002image.png)|![输入图片说明](images/show/phone/2024073003image.png)|![输入图片说明](images/show/phone/2024073004image.png)|

### ERP

| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/2024073005image.png)|![输入图片说明](images/show/phone/2024073006image.png)|![输入图片说明](images/show/phone/2024073007image.png)|![输入图片说明](images/show/phone/2024073008image.png)|

### CRM

| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/2024073012image.png)|![输入图片说明](images/show/phone/2024073009image.png)|![输入图片说明](images/show/phone/2024073010image.png)|![输入图片说明](images/show/phone/2024073011image.png)|

### OA

| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/2024073013image.png)|![输入图片说明](images/show/phone/2024073014image.png)|![输入图片说明](images/show/phone/2024073015image.png)|![输入图片说明](images/show/phone/2024073016image.png)|


## 特别赞助

> 点击图片即可跳转

|  赞助商  |  赞助商  |  赞助商  |  赞助商  |
|--------|-------|-------|-------|
| [![输入图片说明](images/show/sponsor/maxkey_banner.jpg)](https://gitee.com/dromara/MaxKey) | [![输入图片说明](https://minio.tianai.cloud/public/captcha/logo/logo-519x100.png)](https://gitee.com/dromara/tianai-captcha/) |||
