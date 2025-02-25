/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.semester.entity.Semester;
import lombok.Data;

/**
 * @ClassName: SubjectClasses
 * @Description: 科目表与班级表的关系实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/10 14:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "classesId"})
@RedisCacheField(name = "school:subjectClasses", cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "school_subject_classes")
@ApiModel(value = "科目表与班级表的关系实体类")
public class SubjectClasses extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("object_id")
    @ApiModelProperty(value = "科目数据的id", required = "required")
    private String objectId;

    @TableField("object_key")
    @ApiModelProperty(value = "科目数据的serviceClassName", required = "required")
    private String objectKey;

    @TableField(exist = false)
    @Property("科目信息")
    private Subject objectMation;

    @TableField("classes_id")
    @ApiModelProperty(value = "班级id", required = "required")
    private String classesId;

    @TableField(exist = false)
    @Property("班级信息")
    private Classes classesMation;

    @TableField("people_num")
    @Property(value = "课程班级人数")
    private Integer peopleNum;

    @TableField("source_code")
    @Property(value = "加课码")
    private String sourceCode;

    @TableField("source_code_logo")
    @Property(value = "加课码二维码")
    private String sourceCodeLogo;

    @TableField("semester_id")
    @ApiModelProperty(value = "学期id", required = "required")
    private String semesterId;

    @TableField(exist = false)
    @Property("学期信息")
    private Semester semesterMation;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "是否允许加入班级", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(value = "quit")
    @ApiModelProperty(value = "是否允许退出课程", required = "required,num", enumClass = EnableEnum.class)
    private Integer quit;

}