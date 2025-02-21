/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: SysEveRoleMenu
 * @Description: 角色与桌面/菜单/权限点(PC端)关联表
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 9:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_role_menu")
@ApiModel("角色与桌面/菜单/权限点(PC端)关联表")
public class SysEveRoleMenu extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("role_id")
    @ApiModelProperty(value = "角色id", required = "required")
    private String roleId;

    @TableField("menu_id")
    @ApiModelProperty(value = "菜单/权限点id", required = "required")
    private String menuId;

    @Property("创建人id")
    @TableField(value = "creater", updateStrategy = FieldStrategy.NEVER)
    private String creater;

    @Property("创建时间")
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private String createTime;

}
