/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: SysEveUserCustomParent
 * @Description: 用户菜单自定义拖拽组合实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/5 20:39
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user_custom_parent")
@ApiModel("用户菜单自定义拖拽组合实体类")
public class SysEveUserCustomParent extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("menu_id")
    @ApiModelProperty(value = "菜单id", required = "required")
    private String menuId;

    @TableField("level")
    @Property(value = "级别")
    private Integer level;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "父级菜单id", required = "required")
    private String parentId;

}
