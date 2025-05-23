/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.payment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PaymentCollection
 * @Description: 回款实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/2 20:28
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "crm:payment", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_payment_collection", autoResultMap = true)
@ApiModel("回款实体类")
public class PaymentCollection extends SkyeyeFlowable {

    @TableField(exist = false)
    @Property(value = "单号，仅用于展示使用")
    private String name;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "collection_time")
    @ApiModelProperty(value = "回款日期", required = "required")
    private String collectionTime;

    @TableField(value = "contract_id")
    @ApiModelProperty(value = "合同ID", required = "required")
    private String contractId;

    @TableField(exist = false)
    @Property(value = "合同")
    private Map<String, Object> contractMation;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "回款方式，参考数据字典")
    private String typeId;

    @TableField(value = "receivable_id")
    @ApiModelProperty(value = "应收事项id")
    private String receivableId;

    @TableField(value = "price")
    @ApiModelProperty(value = "回款金额", required = "double", defaultValue = "0")
    private String price;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "invoice_price")
    @Property(value = "已开票金额")
    private String invoicePrice;

}
