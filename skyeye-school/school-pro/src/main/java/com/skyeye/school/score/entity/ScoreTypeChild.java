/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ScoreTypeChild
 * @Description: 成绩类型子表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("school_score_type_child")
@ApiModel(value = "成绩类型子表实体类")
public class ScoreTypeChild extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField("sub_class_link_id")
    @ApiModelProperty(value = "科目表与班级表关系id", required = "required")
    private String subClassLinkId;

    @TableField("name")
    @ApiModelProperty(value = "名称")
    private String name;

    @TableField("name_link_id")
    @ApiModelProperty(value = "名称关联的业务对象id")
    private String nameLinkId;

    @TableField("name_link_key")
    @ApiModelProperty(value = "名称关联的业务对象的key")
    private String nameLinkKey;

    @TableField(exist = false)
    @ApiModelProperty(value = "名称关联的业务对象")
    private String nameLinkMation;

    @TableField("proportion")
    @ApiModelProperty(value = "占比(80即占比80%)")
    private String proportion;

    @TableField("parent_id")
    @Property(value = "父级id(成绩类型主表id)")
    private String parentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "成绩列表")
    private List<Score> scoreList;

    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<ScoreTypeChild> children;
}
