package com.skyeye.cost.entity;

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
 * @ClassName: ProUserCost
 * @Description: 人力成本实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/1 15:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "pro_user_cost", autoResultMap = true)
@ApiModel("人力成本实体类")
public class ProUserCost extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("project_id")
    @ApiModelProperty(value = "项目id", required = "required")
    private String projectId;

    @TableField(exist = false)
    @Property("项目信息")
    private Map<String, Object> projectMation;

    @TableField("department_id")
    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property("部门信息")
    private Map<String, Object> departmentMation;

    @TableField("total_price")
    @ApiModelProperty(value = "总金额", defaultValue = "0",required = "double")
    private String totalPrice;

    @TableField("user_id")
    @ApiModelProperty(value = "用户id", required = "required")
    private String userId;

    @TableField(exist = false)
    @Property("用户信息")
    private Map<String, Object> userMation;

    @TableField("man_hours")
    @ApiModelProperty(value = "工时", required = "required,double")
    private String manHours;

    @TableField("work_hours")
    @ApiModelProperty(value = "时长", required = "required,double")
    private String workHours;

    @TableField("state")
    @ApiModelProperty(value = "状态,是否是临时工 0是、1否", required = "required")
    private Integer state;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
