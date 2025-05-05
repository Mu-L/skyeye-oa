/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.IconOrImgInfo;
import lombok.Data;

/**
 * @ClassName: SysEveUserCustomMenu
 * @Description: 用户自定义菜单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/5 20:52
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user_custom_menu")
@ApiModel("用户自定义菜单实体类")
public class SysEveUserCustomMenu extends IconOrImgInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "菜单名称", required = "required")
    private String name;

    @TableField("`url`")
    @ApiModelProperty(value = "菜单链接", required = "required")
    private String url;

    @TableField("type")
    @Property(value = "菜单类型")
    private String type;

    @TableField("parent_id")
    @ApiModelProperty(value = "父菜单ID")
    private String parentId;

    @TableField("open_type")
    @Property(value = "菜单链接打开类型，父菜单默认为1.1：打开iframe，2：打开html。")
    private Integer openType;

    @TableField(value = "light_app_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "轻应用id")
    private String lightAppId;

    @TableField(value = "desktop_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "菜单所属桌面id")
    private String desktopId;
}
