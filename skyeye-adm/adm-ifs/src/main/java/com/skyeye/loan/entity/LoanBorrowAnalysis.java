package com.skyeye.loan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;


/**
 * @ClassName: LoanBorrowAnalysis
 * @Description: 借款单分析实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 13:59
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ifs_loan_borrow_analysis", autoResultMap = true)
@ApiModel("借款单分析实体类")
public class LoanBorrowAnalysis extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("price")
    @ApiModelProperty("费用金额")
    private String price;

    @TableField("period_time")
    @ApiModelProperty("年月")
    private String periodTime;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private String createTime;

}
