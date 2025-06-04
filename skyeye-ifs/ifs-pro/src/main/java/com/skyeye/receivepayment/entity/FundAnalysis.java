package com.skyeye.receivepayment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: FundAnalysis
 * @Description: 资金分析实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ifs_fund_analysis", autoResultMap = true)
@ApiModel("资金分析实体类")
public class FundAnalysis extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(客户/供应商)", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "price")
    @ApiModelProperty(value = "回付款金额", required = "double", defaultValue = "0")
    private String price;

    @TableField("from_id")
    @ApiModelProperty(value = "来源ID（付款id、回款id、应付id、应收id）")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源信息")
    private Map<String, Object> fromMation;

    @TableField("from_key")
    @ApiModelProperty(value = "来源key（付款、回款）")
    private String fromKey;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间", required = "required")
    private String createTime;
}
