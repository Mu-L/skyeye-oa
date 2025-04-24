/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ActFlowMation
 * @Description: 流程模型管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/4 22:54
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"modelKey"})
@RedisCacheField(name = "act:flow", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "act_flow")
@ApiModel("流程配置实体类")
public class ActFlowMation extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "flow_name")
    @ApiModelProperty(value = "流程模型名称", required = "required", fuzzyLike = true)
    private String flowName;

    @TableField(value = "model_id", fill = FieldFill.INSERT)
    @Property("模型id")
    private String modelId;

    @TableField(value = "model_key")
    @ApiModelProperty(value = "模型key", required = "required", fuzzyLike = true)
    private String modelKey;

    @TableField(value = "apply_app_id")
    @ApiModelProperty(value = "服务类所属的appId")
    private String applyAppId;

    @TableField(value = "apply_service_class_name")
    @ApiModelProperty(value = "适用的服务类名")
    private String applyServiceClassName;

}
