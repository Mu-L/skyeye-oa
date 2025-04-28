/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.tenant.classenum.TenantAppMenuType;
import lombok.Data;

/**
 * @ClassName: TenantAppMenu
 * @Description: 应用与菜单的关系管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "tenant_app_menu")
@ApiModel("应用与菜单的关系管理实体类")
public class TenantAppMenu extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "app_id")
    @Property(value = "应用id")
    private String appId;

    @TableField(value = "object_id")
    @Property(value = "菜单/权限点ID")
    private String objectId;

    @TableField(value = "type")
    @Property(value = "类型", enumClass = TenantAppMenuType.class)
    private Integer type;

}
