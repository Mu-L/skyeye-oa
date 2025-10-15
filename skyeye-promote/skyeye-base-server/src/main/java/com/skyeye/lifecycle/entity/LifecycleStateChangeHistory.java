/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: LifecycleStateChangeHistory
 * @Description: 生命周期状态周转历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "lifecycle_state_change_history")
@ApiModel("生命周期状态周转历史实体类")
public class LifecycleStateChangeHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(员工id)", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key(员工key)", required = "required")
    private String objectKey;

    @TableField(value = "object_app_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的应用的appId", required = "required")
    private String objectAppId;

    @TableField(value = "template_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "业务对象数据所对应得模板id", required = "required")
    private String templateId;

    @TableField(value = "from_node_id")
    @ApiModelProperty(value = "来源状态节点id")
    private String fromNodeId;

    @TableField(value = "to_node_id")
    @ApiModelProperty(value = "目标状态节点id")
    private String toNodeId;

}
