/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.entity.intercourse;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.search.CommonPageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: StoreIntercourseQueryDo
 * @Description: 门店的支出/收入往来列表查询实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/6/29 22:12
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("门店的支出/收入往来列表查询实体类")
public class StoreIntercourseQueryDo extends CommonPageInfo implements Serializable {

    @ApiModelProperty(value = "保养门店id")
    private String keepfitStoreId;

    @ApiModelProperty(value = "保养门店名称")
    private String keepfitStoreName;

    @ApiModelProperty(value = "套餐购买门店id")
    private String mealByStoreId;

    @ApiModelProperty(value = "套餐购买门店名称")
    private String mealByStoreName;

    @ApiModelProperty(value = "开始日期")
    private String startTime;

    @ApiModelProperty(value = "结束日期")
    private String endTime;

    @ApiModelProperty(value = "租户值")
    private String tenantId;

    /**
     * 单据往来日期，格式为yyyy-MM-dd
     */
    private String day;

}
