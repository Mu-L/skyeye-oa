/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IncomeOrder
 * @Description: 明细账订单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/23 12:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "ifs:order", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "ifs_order_head", autoResultMap = true)
@ApiModel("明细账订单实体类")
public class IncomeOrder extends SkyeyeFlowable {

    @TableField(value = "holder_id", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "往来单位Id", required = "required")
    private String holderId;

    @TableField(value = "holder_key", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "往来单位Key", required = "required")
    private String holderKey;

    @TableField(exist = false)
    @Property(value = "往来单位信息")
    private Map<String, Object> holderMation;

    @TableField(value = "type")
    @ApiModelProperty(value = "单据类型", required = "required")
    private String type;

    @TableField(value = "hands_person_id")
    @ApiModelProperty(value = "经手人Id", required = "required")
    private String handsPersonId;

    @TableField(exist = false)
    @Property(value = "经手人信息")
    private Map<String, Object> handsPersonMation;

    @TableField("bill_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField("account_id")
    @ApiModelProperty(value = "账户Id", required = "required")
    private String accountId;

    @TableField(exist = false)
    @Property(value = "账户信息")
    private Map<String, Object> accountMation;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField(exist = false)
    @Property(value = "账套信息")
    private Map<String, Object> setOfBooksMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "收入项目列表", required = "required,json")
    private List<IncomeOrderItem> initem;

}
