/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.eve.gw.classenum.GwDocumentOpenCategory;
import com.skyeye.eve.gw.classenum.GwDocumentPeriod;
import com.skyeye.eve.gw.classenum.GwDocumentSecret;
import com.skyeye.eve.gw.classenum.GwDocumentUrgency;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: GwReceiveDocument
 * @Description: 公文收文管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/25 22:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "gw:receiveDocument", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "gw_receive_document", autoResultMap = true)
@ApiModel("公文收文管理实体类")
public class GwReceiveDocument extends SkyeyeFlowable {

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField("secret")
    @ApiModelProperty(value = "密级级别", enumClass = GwDocumentSecret.class)
    private String secret;

    @TableField("period")
    @ApiModelProperty(value = "保密期间", enumClass = GwDocumentPeriod.class)
    private String period;

    @TableField("urgency")
    @ApiModelProperty(value = "紧急程度", enumClass = GwDocumentUrgency.class)
    private String urgency;

    @TableField("open_category")
    @ApiModelProperty(value = "公开类别", enumClass = GwDocumentOpenCategory.class)
    private String openCategory;

    @TableField("year")
    @ApiModelProperty(value = "年份", required = "required")
    private String year;

    @TableField("number")
    @ApiModelProperty(value = "第几号文", required = "required")
    private String number;

    @TableField("enterprise")
    @ApiModelProperty(value = "企字", required = "required")
    private String enterprise;

    @TableField("receive_time")
    @ApiModelProperty(value = "收文日期", required = "required")
    private String receiveTime;

    @TableField("send_department_name")
    @ApiModelProperty(value = "发文部门名称")
    private String sendDepartmentName;

    @TableField(value = "receive_department_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "收文的部门id")
    private List<String> receiveDepartmentId;

    @TableField(exist = false)
    @Property("收文的部门信息")
    private List<Map<String, Object>> receiveDepartmentMation;

    @TableField("content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

}
