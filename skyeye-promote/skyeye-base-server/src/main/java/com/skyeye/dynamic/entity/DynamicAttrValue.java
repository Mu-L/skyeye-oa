/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dynamic.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: DynamicAttrValue
 * @Description: 动态属性值实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/13 14:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "objectKey"})
@TableName(value = "skyeye_dynamic_attr_value")
@ApiModel("动态属性值实体类")
public class DynamicAttrValue extends CommonInfo {

    @TableId("id")
    @Property("主键id")
    private String id;

    @TableField(value = "object_app_id")
    @ApiModelProperty(value = "appId", required = "required")
    private String objectAppId;

    @TableField(value = "object_id")
    @ApiModelProperty(value = "业务对象数据的id", required = "required")
    private String objectId;

    @TableField(value = "object_key")
    @ApiModelProperty(value = "业务对象服务的className", required = "required")
    private String objectKey;

    @TableField(value = "attr_value")
    @ApiModelProperty(value = "值，json字符串,{\"test\": \"123\"}")
    private String attrValue;

    @Property("创建时间")
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private String createTime;

    @Property("最后更新日期")
    @TableField(value = "last_update_time")
    private String lastUpdateTime;

}
