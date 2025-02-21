/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.machin.classenum.MachinPickStateEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Machin
 * @Description: 加工单
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/29 16:54
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_MACHIN_CACHE_KEY, cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "erp_machin_header", autoResultMap = true)
@ApiModel("加工单实体类")
public class Machin extends SkyeyeFlowable {

    @TableField(exist = false)
    @Property(value = "树的名称(订单编号)")
    private String name;

    @TableField("from_type_id")
    @ApiModelProperty(value = "来源单据类型，参考#MachinFromType")
    private Integer fromTypeId;

    @TableField("from_id")
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField("pick_state")
    @Property(value = "领料状态", enumClass = MachinPickStateEnum.class)
    private String pickState;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "子单据信息(工序信息)", required = "required,json")
    private List<MachinChild> machinChildList;

    @TableField(exist = false)
    @Property(value = "条形码信息")
    private Map<String, Object> barCodeMation;

    @TableField(exist = false)
    @Property(value = "加工单所需的原材料信息")
    private List<BomChild> needRawMaterialList;

}
