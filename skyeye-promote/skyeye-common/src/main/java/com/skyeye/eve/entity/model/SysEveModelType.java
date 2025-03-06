/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: SysEveModelType
 * @Description: 素材分类实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/6 9:05
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"typeName", "parentId"})
@TableName(value = "sys_eve_model_type")
@ApiModel("素材分类实体类")
public class SysEveModelType extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("type_name")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String typeName;

    @TableField("parent_id")
    @ApiModelProperty(value = "父节点id", required = "required")
    private String parentId;

}
