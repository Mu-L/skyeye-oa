/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentarchive.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentArchive
 * @Description: 设备档案实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/28 15:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "erp:equipment:archive")
@TableName(value = "erp_equipment_archive", autoResultMap = true)
@ApiModel("设备档案实体类")
public class EquipmentArchive extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "设备编码")
    private String oddNumber;

    @TableField(value = "equipment_type_id")
    @ApiModelProperty(value = "设备类型id", required = "required")
    private String equipmentTypeId;

    @TableField(value = "equipment_type_name")
    @ApiModelProperty(value = "设备类型名称")
    private String equipmentTypeName;

    @TableField(value = "equipment_state")
    @ApiModelProperty(value = "设备状态", required = "required")
    private String equipmentState;

    @TableField(value = "equipment_img")
    @ApiModelProperty(value = "设备图片")
    private String equipmentImg;

    @TableField(value = "technical_manual")
    @ApiModelProperty(value = "设备技术手册")
    private String technicalManual;

    @TableField(value = "model")
    @ApiModelProperty(value = "规格型号")
    private String model;

    @TableField(value = "use_farm_id")
    @ApiModelProperty(value = "使用车间id")
    private String useFarmId;

    @TableField(value = "install_address")
    @ApiModelProperty(value = "安装地点")
    private String installAddress;

    @TableField(value = "responsible_id")
    @ApiModelProperty(value = "设备负责人id")
    private String responsibleId;

    @TableField(value = "responsible_phone")
    @ApiModelProperty(value = "设备负责人联系方式")
    private String responsiblePhone;

    @TableField(value = "manufacturer")
    @ApiModelProperty(value = "生产厂商")
    private String manufacturer;

    @TableField(value = "supplier_id")
    @ApiModelProperty(value = "供应商id")
    private String supplierId;

    @TableField(value = "supplier_code")
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @TableField(value = "supplier_name")
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @TableField(value = "buy_time")
    @ApiModelProperty(value = "购买日期")
    private String buyTime;

    @TableField(value = "enable_time")
    @ApiModelProperty(value = "启用日期")
    private String enableTime;

    @TableField(exist = false)
    @Property(value = "设备负责人信息")
    private Map<String, Object> responsibleMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备关联业务记录列表(可选,json)")
    private List<EquipmentArchiveBizRecord> bizRecordList;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备点检记录(可选,json)")
    private EquipmentCheckOrder checkRecord;
}

