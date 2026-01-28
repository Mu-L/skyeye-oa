package com.skyeye.evaluation.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EvaluationItemTypeEnum
 * @Description: 评估项类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EvaluationItemTypeEnum implements SkyeyeEnumClass {

    TECH_DIFFICULTY("1", "技术难度", true, false),
    RESOURCE_DEMAND("2", "资源需求", true, false),
    TIME_RISK("3", "时间风险", true, false),
    COST_RISK("4", "成本风险", true, false),
    QUALITY_RISK("5", "质量风险", true, false),
    MARKET_RISK("6", "市场风险", true, false),
    MANAGEMENT_RISK("7", "管理风险", true, false),
    CUSTOM("8", "自定义", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}