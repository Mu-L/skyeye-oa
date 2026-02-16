/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ApsScheduleSaveParam
 * @Description: APS排程保存参数（用户微调后的排产信息）
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("APS排程保存参数")
public class ApsScheduleSaveParam {

    @ApiModelProperty(value = "排程明细列表", required = "required")
    private List<ApsScheduleItem> items;

    @Data
    @ApiModel("APS排程明细")
    public static class ApsScheduleItem {
        @ApiModelProperty(value = "加工单工序ID", required = "required")
        private String machinProcedureId;

        @ApiModelProperty(value = "计划开始时间，格式yyyy-MM-dd HH:mm:ss", required = "required")
        private String planStartTime;

        @ApiModelProperty(value = "计划结束时间，格式yyyy-MM-dd HH:mm:ss", required = "required")
        private String planEndTime;
    }
}
