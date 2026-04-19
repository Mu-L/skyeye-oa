/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * 系统操作日志（按租户隔离）
 */
@Data
@TableName(value = "sys_eve_user_oper_log", autoResultMap = true)
@ApiModel("系统操作日志")
public class SysEveUserOperLog extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @TableField(value = "oper_user_id")
    @ApiModelProperty(value = "操作人用户ID")
    private String operUserId;

    @TableField(value = "oper_user_code")
    @ApiModelProperty(value = "操作人账号", fuzzyLike = true)
    private String operUserCode;

    @TableField(value = "oper_user_name")
    @ApiModelProperty(value = "操作人姓名", fuzzyLike = true)
    private String operUserName;

    @TableField(value = "api_id")
    @ApiModelProperty(value = "接口id")
    private String apiId;

    @TableField(value = "api_name")
    @ApiModelProperty(value = "接口名称", fuzzyLike = true)
    private String apiName;

    @TableField(value = "api_group_name")
    @ApiModelProperty(value = "接口分组")
    private String apiGroupName;

    @TableField(value = "api_model_name")
    @ApiModelProperty(value = "模块名称")
    private String apiModelName;

    @TableField(value = "request_path")
    @ApiModelProperty(value = "请求路径")
    private String requestPath;

    @TableField(value = "http_method")
    @ApiModelProperty(value = "HTTP方法")
    private String httpMethod;

    @TableField(value = "request_params")
    @ApiModelProperty(value = "请求参数JSON")
    private String requestParams;

    @TableField(value = "client_ip")
    @ApiModelProperty(value = "客户端IP")
    private String clientIp;

    @TableField(value = "oper_time")
    @ApiModelProperty(value = "操作时间")
    private String operTime;

    @TableField(value = "cost_ms")
    @ApiModelProperty(value = "耗时毫秒")
    private Long costMs;

    @TableField(value = "return_code")
    @ApiModelProperty(value = "返回码")
    private Integer returnCode;

    @TableField(value = "return_message")
    @ApiModelProperty(value = "返回说明")
    private String returnMessage;

    @TableField(value = "throwable_class")
    @ApiModelProperty(value = "异常类型")
    private String throwableClass;

    @TableField(value = "throwable_message")
    @ApiModelProperty(value = "异常信息")
    private String throwableMessage;

    @TableField(value = "source_service")
    @ApiModelProperty(value = "来源微服务(spring.application.name)", fuzzyLike = true)
    private String sourceService;

    @TableField(value = "source_app_id")
    @ApiModelProperty(value = "来源应用(skyeye.appid)")
    private String sourceAppId;
}
