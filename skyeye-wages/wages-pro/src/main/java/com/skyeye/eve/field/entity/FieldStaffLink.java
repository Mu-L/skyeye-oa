/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: FieldStaffLink
 * @Description: 员工与薪资字段类型关系实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:18
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("员工与薪资字段类型关系实体类")
@TableName(value = "wages_field_staff_mation")
public class FieldStaffLink extends CommonInfo {

    @TableField("staff_id")
    @ApiModelProperty(value = "员工id", required = "required")
    private String staffId;

    @TableField("field_type_key")
    @ApiModelProperty(value = "薪资字段key", required = "required")
    private String fieldTypeKey;

    @TableField(value = "amount_money")
    @ApiModelProperty(value = "员工与薪资字段类型对应的钱", required = "required,double")
    private String amountMoney;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
