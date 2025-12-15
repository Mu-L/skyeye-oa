/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SupplierContract
 * @Description: 供应商合同信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "title"})
@RedisCacheField(name = CacheConstants.ERP_SUPPLIER_CONTRACT_CACHE_KEY, cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_supplier_contract", autoResultMap = true)
@ApiModel("供应商合同信息实体类")
public class SupplierContract extends SkyeyeFlowable {

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @TableField(value = "title")
    @ApiModelProperty(value = "合同名称", required = "required", fuzzyLike = true)
    private String title;

    @TableField(exist = false)
    @Property(value = "合同名称")
    private String name;

    @TableField(value = "price")
    @ApiModelProperty(value = "合同金额", required = "double", defaultValue = "0")
    private String price;

    @TableField(value = "paid_price")
    @ApiModelProperty(value = "已付金额", required = "double", defaultValue = "0")
    private String paidPrice;

    @TableField(value = "invoice_price")
    @Property(value = "已开票金额")
    private String invoicePrice;

    @TableField(value = "material_total_price")
    @ApiModelProperty(value = "产品明细总金额", required = "double", defaultValue = "0")
    private String materialTotalPrice;

    @TableField(value = "signing_time")
    @ApiModelProperty(value = "签约日期", required = "required")
    private String signingTime;

    @TableField(value = "effect_time")
    @ApiModelProperty(value = "生效日期")
    private String effectTime;

    @TableField(value = "service_end_time")
    @ApiModelProperty(value = "服务结束日期")
    private String serviceEndTime;

    @TableField(value = "contacts")
    @ApiModelProperty(value = "联系人ID", required = "required")
    private String contacts;

    @TableField(exist = false)
    @Property(value = "联系人")
    private Map<String, Object> contactsMation;

    @TableField(value = "technical_terms")
    @ApiModelProperty(value = "主要技术条款")
    private String technicalTerms;

    @TableField(value = "business_terms")
    @ApiModelProperty(value = "主要商务条款")
    private String businessTerms;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "所属部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "所属部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "relation_user_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "关联人员", required = "required,json")
    private List<String> relationUserId;

    @TableField(exist = false)
    @Property(value = "关联人员")
    private List<Map<String, Object>> relationUserMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品明细信息", required = "json")
    private List<SupplierContractChild> supplierContractChildList;

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型，参考#SupplierContractFromType")
    private Integer fromTypeId;

    @TableField(value = "from_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField("child_state")
    @Property(value = "合同产品状态，参考#SupplierContractChildStateEnum")
    private String childState;

}
