/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.activity.classenum.ActivityType;
import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.chtopic.classenum.TeacherResultState;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.user.entity.ChooseUser;
import lombok.Data;

/**
 * @ClassName: ChooseTopic
 * @Description: 课题实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField("title")
@RedisCacheField(name = "choose:topic", cacheTime = RedisConstants.ONE_WEEK_SECONDS)
@TableName(value = "choose_topic")
@ApiModel(value = "课题实体类")
@ExcelTarget("ChooseTopic")
public class ChooseTopic extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "课题编号", fuzzyLike = true)
    private String oddNumber;

    @TableField("title")
    @ApiModelProperty(value = "标题", fuzzyLike = true)
    @Excel(name = "题目", width = 50, isImportField = "true_st", orderNum = "2")
    private String title;

    @TableField("remark")
    @ApiModelProperty(value = "题目描述")
    @Excel(name = "简介", width = 60, isImportField = "true_st", orderNum = "3")
    private String remark;

    @TableField("data_from")
    @ApiModelProperty(value = "数据源地址")
    @Excel(name = "数据源", width = 30, isImportField = "true_st", orderNum = "4")
    private String dataFrom;

    @TableField("teacher_name")
    @ApiModelProperty(value = "出题教师")
    @Excel(name = "出题教师", width = 10, isImportField = "true_st", orderNum = "5")
    private String teacherName;

    @TableField("choose")
    @Property(value = "是否已选  1未选  2已选")
    @Excel(name = "是否已选", width = 10, replace = {"未选_1", "已选_2"}, orderNum = "6")
    private Integer choose;

    @TableField("choose_user_id")
    @Property(value = "已选用户id")
    private String chooseUserId;

    @TableField(exist = false)
    @Property(value = "用户信息")
    private ChooseUser chooseUserMation;

    @TableField(exist = false)
    @Property(value = "用户信息")
    @Excel(name = "学生", width = 10, orderNum = "7")
    private String chooseUserName;

    @TableField("activity_id")
    @Property(value = "活动id")
    private String activityId;

    @TableField(exist = false)
    @Property(value = "活动信息")
    private ChooseActivity activityMation;

    @TableField(exist = false)
    @Property(value = "选题活动")
    @Excel(name = "选题活动", width = 10, orderNum = "8")
    private String activityName;

    @TableField(exist = false)
    @Property(value = "选题活动类型", enumClass = ActivityType.class)
    @Excel(name = "选题活动类型", width = 10, replace = {"单选_1", "双选_2"}, orderNum = "9")
    private Integer activityType;

    @TableField("teacher_id")
    @ApiModelProperty(value = "导师id")
    private String teacherId;

    @TableField(exist = false)
    @Property(value = "导师")
    @Excel(name = "导师", width = 10, orderNum = "10")
    private String mentorName;

    @TableField(exist = false)
    @Property(value = "导师信息")
    private ChooseUser teacherMation;

    @TableField("teacher_result")
    @ApiModelProperty(value = "选择导师的结果", enumClass = TeacherResultState.class)
    @Excel(name = "单双选结果", width = 10, replace = {"同意_1", "拒绝_2", "待审批_3"}, orderNum = "11")
    private Integer teacherResult;
}
