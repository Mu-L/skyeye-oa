package com.skyeye.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: CostAccount
 * @Description: 项目实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/1 15:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "pro_cost_account", autoResultMap = true)
@ApiModel("项目实体类")
public class CostAccount extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("project_id")
    @ApiModelProperty(value = "项目id", required = "required")
    private String projectId;

    @TableField(exist = false)
    @Property("项目信息")
    private Map<String, Object> projectMation;

    @TableField("total_price")
    @ApiModelProperty(value = "总金额", defaultValue = "0")
    private String totalPrice;

    @TableField("cost_type")
    @ApiModelProperty(value = "成本类型", required = "required")
    private Integer costType;

    @TableField("add_flag")
    @ApiModelProperty(value = "新增标识", defaultValue = "1")
    private Integer addFlag;

    @TableField("tenant_id")
    @Property("项目信息")
    private String tenantId;
}
