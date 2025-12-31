/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.payment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.eve.payment.classenum.PaymentHistoryState;
import com.skyeye.eve.payment.classenum.PaymentHistoryType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WagesPaymentHistory
 * @Description: 薪资发放历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/22 17:12
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@ApiModel("薪资发放历史实体类")
@TableName(value = "wages_payment_history", autoResultMap = true)
@RedisCacheField(name = CacheConstants.WAGES_PAYMENT_CACHE_KEY)
public class WagesPaymentHistory extends CommonInfo {

    @TableField("staff_id")
    @ApiModelProperty(value = "员工id", required = "required")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private Map<String, Object> staffMation;

    @TableField("pay_month")
    @ApiModelProperty(value = "发放的薪资年月", required = "required")
    private String payMonth;

    @TableField(value = "wages_json", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "薪资发放详细信息，json串[{\"name\":\"基础工资(名称)\", \"modelId\": \"模型id，可为空\", \"childField\": [{\"name\":\"奖励\", \"moneyValue\": \"100(实际金额)\", \"key\": \"jiangli\", \"fieldType\": \"字段类型，参考#WagesModelFieldType\", \"defaultMoney\": \"0,该薪资模板中设置的默认金额\", \"orderBy\": \"排序号\", \"formula\": \"计算公式\"}]}]", required = "required")
    private List<Map<String, Object>> wagesJson;

    @TableField("act_wages")
    @ApiModelProperty(value = "实发总薪资", required = "required")
    private String actWages;

    @TableField("create_time")
    @ApiModelProperty(value = "薪资核算日期", required = "required")
    private String createTime;

    @TableField("grant_time")
    @ApiModelProperty(value = "薪资发放日期", required = "required")
    private String grantTime;

    @TableField("type")
    @ApiModelProperty(value = "核算类型", enumClass = PaymentHistoryType.class, required = "required")
    private Integer type;

    @TableField("state")
    @ApiModelProperty(value = "是否发放", enumClass = PaymentHistoryState.class, required = "required")
    private Integer state;

}
