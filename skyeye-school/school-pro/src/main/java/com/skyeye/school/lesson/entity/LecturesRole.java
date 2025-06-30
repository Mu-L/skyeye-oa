package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: LecturesRole
 * @Description: 质评角色实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/23 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName(value = "school_lectures_role")
@UniqueField
@ApiModel(value = "质评角色实体类")
public class LecturesRole extends BaseGeneralInfo {
}
