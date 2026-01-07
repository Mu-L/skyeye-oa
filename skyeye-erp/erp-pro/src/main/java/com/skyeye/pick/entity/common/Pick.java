/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.entity.common;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.depot.entity.Depot;
import com.skyeye.farm.entity.Farm;
import com.skyeye.pick.classenum.PickFromType;
import com.skyeye.pick.entity.PickChild;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Pick
 * @Description: 物料单父类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/30 8:49
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("加工单实体类")
public class Pick extends SkyeyeFlowable {

    /**
     * 订单类型，每个服务类的serviceClassName
     */
    @TableField(value = "id_key", updateStrategy = FieldStrategy.NEVER)
    private String idKey;

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间id")
    private String farmId;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型", enumClass = PickFromType.class)
    private Integer fromTypeId;

    @TableField(value = "from_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "子单据信息", required = "required,json")
    private List<PickChild> pickChildList;

    @TableField("other_state")
    @Property("其他状态信息，根据单据类型不同，状态信息表达含义不同。")
    private Integer otherState;

}
