package com.skyeye.farm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: FarmStation
 * @Description: 车间工位管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/24 22:43
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_FARM_CACHE_KEY)
@TableName(value = "erp_farm_station")
@ApiModel("车间工位管理实体类")
public class FarmStation extends BaseGeneralInfo {

    @TableField(value = "job_fun")
    @ApiModelProperty(value = "工位职能")
    private String jobFun;

    @TableField(value = "work_procedure_id")
    @ApiModelProperty(value = "关联工序id")
    private String workProcedureId;
}
