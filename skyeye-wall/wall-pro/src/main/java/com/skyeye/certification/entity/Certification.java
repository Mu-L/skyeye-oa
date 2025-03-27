/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.certification.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.certification.classenum.StateEnum;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: Certification
 * @Description: 学生信息认证实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "wall:certification", value = {"id", "userId"}, cacheTime = RedisConstants.TEN_DAY_SECONDS)
@TableName(value = "wall_certification")
@ApiModel(value = "学生认证实体类")
public class Certification extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("user_id")
    @Property(value = "用户id")
    private String userId;

    @TableField("`name`")
    @ApiModelProperty(value = "姓名", required = "required", fuzzyLike = true)
    private String name;

    @TableField("campus")
    @ApiModelProperty(value = "校区", required = "required")
    private String campus;

    @TableField("faculty_id")
    @ApiModelProperty(value = "学院", required = "required")
    private String facultyId;

    @TableField("major_id")
    @ApiModelProperty(value = "专业", required = "required")
    private String majorId;

    @TableField("class_id")
    @ApiModelProperty(value = "班级", required = "required")
    private String classId;

    @TableField("join_time")
    @ApiModelProperty(value = "入学时间", required = "required")
    private String joinTime;

    @TableField("year")
    @ApiModelProperty(value = "那一届", required = "required")
    private String year;

    @TableField("sex")
    @ApiModelProperty(value = "性别", required = "required")
    private Integer sex;

    @TableField("idcard")
    @ApiModelProperty(value = "身份证号", required = "required")
    private String idCard;

    @TableField("idcard_type")
    @ApiModelProperty(value = "身份证类型，证件类型，参考#IDCardType", required = "required")
    private Integer idcardType;

    @TableField("type")
    @ApiModelProperty(value = "学生类别，参考#StudentType", required = "required")
    private Integer type;

    @TableField("status")
    @ApiModelProperty(value = "学生状态，参考#StudentState", required = "required")
    private Integer status;


    @TableField("student_number")
    @ApiModelProperty(value = "学号", required = "required")
    private String studentNumber;

    @TableField("id_photo_front")
    @ApiModelProperty(value = "身份证正面照片", required = "required")
    private String idPhotoFront;

    @TableField("id_photo_other")
    @ApiModelProperty(value = "身份证反面照片", required = "required")
    private String idPhotoOther;

    @TableField("student_id_photo")
    @ApiModelProperty(value = "学生证照片", required = "required")
    private String studentIdPhoto;

    @TableField("state")
    @ApiModelProperty(value = "状态", enumClass = StateEnum.class)
    private Integer state;
}