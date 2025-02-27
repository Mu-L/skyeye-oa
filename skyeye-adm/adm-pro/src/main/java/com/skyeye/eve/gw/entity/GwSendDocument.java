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
 * @ClassName: GwSendDocument
 * @Description: 公文发文管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/25 22:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "gw:sendDocument", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "gw_send_document", autoResultMap = true)
@ApiModel("公文发文管理实体类")
public class GwSendDocument extends SkyeyeFlowable {

    @TableField("template_id")
    @ApiModelProperty(value = "套红模版id", required = "required")
    private String templateId;

    @TableField(exist = false)
    @Property("套红模版信息")
    private GwTemplates templateMation;

    @TableField("model_id")
    @ApiModelProperty(value = "公文模版id", required = "required")
    private String modelId;

    @TableField(exist = false)
    @Property("公文模版信息")
    private GwModel modelMation;

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required")
    private String title;

    @TableField("secret")
    @ApiModelProperty(value = "密级级别", required = "num", enumClass = GwDocumentSecret.class)
    private String secret;

    @TableField("period")
    @ApiModelProperty(value = "保密期间", required = "num", enumClass = GwDocumentPeriod.class)
    private String period;

    @TableField("urgency")
    @ApiModelProperty(value = "紧急程度", required = "num", enumClass = GwDocumentUrgency.class)
    private String urgency;

    @TableField("open_category")
    @ApiModelProperty(value = "公开类别", required = "num", enumClass = GwDocumentOpenCategory.class)
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

    @TableField("send_time")
    @ApiModelProperty(value = "发文日期", required = "required")
    private String sendTime;

    @TableField("send_department_id")
    @ApiModelProperty(value = "发文部门id")
    private String sendDepartmentId;

    @TableField(exist = false)
    @Property("发文部门信息")
    private Map<String, Object> sendDepartmentMation;

    @TableField(value = "cc_department_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "抄送部门id")
    private List<String> ccDepartmentId;

    @TableField(exist = false)
    @Property("抄送部门信息")
    private List<Map<String, Object>> ccDepartmentMation;

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

    @TableField(value = "path")
    @Property(value = "公文文件地址")
    private String path;

    @TableField(value = "pic_path")
    @Property(value = "公文图片文件地址")
    private String picPath;

}
