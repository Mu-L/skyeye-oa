/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.dsform.entity.DsFormPage;
import com.skyeye.menu.classenum.MenuType;
import com.skyeye.menu.classenum.UrlType;
import com.skyeye.operate.classenum.AppMenuPageType;
import com.skyeye.win.entity.SysDesktop;
import lombok.Data;

/**
 * @ClassName: AppWorkPage
 * @Description: APP菜单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/24 21:33
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "sys:appMenu")
@TableName(value = "app_workbench_page")
@ApiModel("APP菜单实体类")
public class AppWorkPage extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "菜单/目录名称", required = "required")
    private String name;

    @TableField("logo")
    @ApiModelProperty(value = "菜单logo")
    private String logo;

    @TableField("url")
    @ApiModelProperty(value = "菜单路径")
    private String url;

    @TableField("url_type")
    @ApiModelProperty(value = "APP菜单URL类型", enumClass = UrlType.class, required = "num")
    private Integer urlType;

    @TableField("order_by")
    @ApiModelProperty(value = "排序，值越大越往后", required = "required,num")
    private Integer orderBy;

    @TableField(value = "type", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "菜单类型", enumClass = MenuType.class)
    private Integer type;

    @TableField("page_type")
    @ApiModelProperty(value = "页面类型", enumClass = AppMenuPageType.class)
    private Integer pageType;

    @TableField("page_id")
    @ApiModelProperty(value = "表单布局id")
    private String pageId;

    @TableField(exist = false)
    @Property(value = "当 pageType 为表单布局时，存储的表单布局信息")
    private DsFormPage pageMation;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "所属目录id", defaultValue = "0")
    private String parentId;

    @TableField(exist = false)
    @Property(value = "所属目录信息")
    private AppWorkPage parentMation;

    @TableField(value = "desktop_id")
    @ApiModelProperty(value = "所属桌面id", required = "required")
    private String desktopId;

    @TableField(exist = false)
    @Property(value = "菜单所属桌面")
    private SysDesktop desktopMation;

}
