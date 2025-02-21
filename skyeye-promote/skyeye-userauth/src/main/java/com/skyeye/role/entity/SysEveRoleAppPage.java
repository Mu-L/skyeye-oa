/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: SysEveRoleAppPage
 * @Description: 角色与桌面/菜单(移动端)关联表
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 10:50
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "app_workbench_page_role")
@ApiModel("角色与桌面/菜单(移动端)关联表")
public class SysEveRoleAppPage extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("role_id")
    @ApiModelProperty(value = "角色id", required = "required")
    private String roleId;

    @TableField("page_id")
    @ApiModelProperty(value = "菜单id", required = "required")
    private String pageId;

}
