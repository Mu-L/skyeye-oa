/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FarmStaffVO
 * @Description: 车间与员工的关系入参实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("车间与员工的关系入参实体类")
public class FarmStaffVO implements Serializable {

    @ApiModelProperty(value = "车间ID", required = "required")
    private String farmId;

    @ApiModelProperty(value = "员工ID和工位ID,[{'staffId':'', 'farmStationId':'','pieceWorkPrice':'20'},{'staffId':'', 'farmStationId':''}.....]", required = "required,json")
    private List<Map<String,Object>> staffIdAndStationIdList;
}
