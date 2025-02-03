/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Role
 * @Description: 角色管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/12 21:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.SYS_ROLE_CACHE_KEY)
@TableName(value = "sys_eve_role")
@ApiModel("角色管理实体类")
public class Role extends BaseGeneralInfo {

    @TableField("parent_id")
    @ApiModelProperty(value = "所属父节点id")
    private String parentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "PC端所有菜单/权限点")
    private List<String> menuIds;

    @TableField(exist = false)
    @Property(value = "PC端所有桌面")
    private List<String> pcDesktopId;

    @TableField(exist = false)
    @Property(value = "PC端所有菜单")
    private List<String> pcMenuId;

    @TableField(exist = false)
    @Property(value = "PC端所有权限点")
    private List<String> pcAuthId;

    @TableField(exist = false)
    @Property(value = "PC端所有权限点信息")
    private List<Map<String, Object>> pcAuthNum;

    @TableField(exist = false)
    @Property(value = "手机端所有菜单/权限点")
    private List<String> appMenuIds;

    @TableField(exist = false)
    @Property(value = "APP端所有桌面")
    private List<String> appDesktopId;

    @TableField(exist = false)
    @Property(value = "APP端所有菜单")
    private List<String> appMenuId;

    @TableField(exist = false)
    @Property(value = "APP端所有权限点")
    private List<String> appAuthId;

    @TableField(exist = false)
    @Property(value = "APP端所有权限点信息")
    private List<Map<String, Object>> appAuthNum;

}
