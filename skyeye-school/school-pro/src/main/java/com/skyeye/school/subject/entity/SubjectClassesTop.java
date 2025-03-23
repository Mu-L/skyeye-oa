/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: SubjectClassesTop
 * @Description: 学生科目置顶表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "school_subject_top")
@ApiModel(value = "学生科目置顶表实体类")
public class SubjectClassesTop extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目表id", required = "required")
    private String subjectId;

    @TableField("sub_class_link_id")
    @ApiModelProperty(value = "科目表与班级表关系id")
    private String subClassLinkId;

}
