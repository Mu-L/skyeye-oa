/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.account.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import lombok.Data;

/**
 * @ClassName: Account
 * @Description: 账户信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.IFS_ACCOUNT_CACHE_KEY)
@TableName(value = "ifs_account", autoResultMap = true)
@ApiModel("账户信息实体类")
public class Account extends BaseGeneralInfo {

    @TableField(value = "serial_no")
    @ApiModelProperty(value = "编号", required = "required")
    private String serialNo;

    @TableField(value = "initial_amount", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "期初金额")
    private String initialAmount;

    @TableField(value = "current_amount")
    @Property(value = "当前金额")
    private String currentAmount;

    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否默认", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer isDefault;

    @TableField(value = "delete_flag")
    @Property(value = "删除标记", enumClass = DeleteFlagEnum.class)
    private Integer deleteFlag;

}
