package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: LecturesUserRole
 * @Description: 质评用户角色关联实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/23 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName(value = "school_lectures_user_role")
@ApiModel(value = "质评用户角色关联实体类")
public class LecturesUserRole extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("role_id")
    @ApiModelProperty(value = "角色id",required = "required")
    private String roleId;

    @TableField("user_id")
    @ApiModelProperty(value = "用户id",required = "required")
    private String userId;

    @TableField(exist = false)
    @Property("用户信息")
    private Map<String,Object> userMation;
}
