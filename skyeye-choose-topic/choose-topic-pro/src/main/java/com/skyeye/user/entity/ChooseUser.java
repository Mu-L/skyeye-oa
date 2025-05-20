/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.user.enumclass.ChooseUserType;
import lombok.Data;

/**
 * @ClassName: ChooseUser
 * @Description: 用户实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField("accountNumber")
@RedisCacheField(name = "choose:user", cacheTime = RedisConstants.ONE_WEEK_SECONDS)
@TableName(value = "choose_user")
@ApiModel(value = "用户实体类")
@ExcelTarget("ChooseUser")
public class ChooseUser extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "account_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "账号")
    @Excel(name = "账号", width = 10, isImportField = "true_st", orderNum = "2")
    private String accountNumber;

    @TableField("password")
    @ApiModelProperty(value = "密码")
    @Excel(name = "初始密码", width = 10, isImportField = "true_st", orderNum = "3")
    private String password;

    @TableField("stu_no")
    @ApiModelProperty(value = "学号", fuzzyLike = true)
    @Excel(name = "学号", width = 10, isImportField = "true_st", orderNum = "4")
    private String stuNo;

    @TableField("`name`")
    @ApiModelProperty(value = "名字", required = "required", fuzzyLike = true)
    @Excel(name = "姓名", width = 10, isImportField = "true_st", orderNum = "5")
    private String name;

    @TableField("type")
    @ApiModelProperty(value = "用户类型", enumClass = ChooseUserType.class)
    private Integer type;

    @TableField("gender")
    @ApiModelProperty(value = "性别")
    @Excel(name = "性别", width = 10, isImportField = "true_st", orderNum = "6")
    private String gender;

    @TableField("job_title")
    @ApiModelProperty(value = "职称")
    @Excel(name = "职称", width = 10, isImportField = "true_st", orderNum = "7")
    private String jobTitle;

    @TableField("guide_capacity")
    @ApiModelProperty(value = "指导容量")
    @Excel(name = "指导容量", width = 10, isImportField = "true_st", orderNum = "8")
    private Integer guideCapacity;

    @TableField("qq")
    @ApiModelProperty(value = "QQ")
    @Excel(name = "QQ", width = 10, isImportField = "true_st", orderNum = "9")
    private String qq;

    @TableField("phone")
    @ApiModelProperty(value = "手机号")
    @Excel(name = "手机号", width = 10, isImportField = "true_st", orderNum = "10")
    private String phone;

    @TableField("topic_requirement")
    @ApiModelProperty(value = "选题要求")
    @Excel(name = "选题要求", width = 10, isImportField = "true_st", orderNum = "11")
    private String topicRequirement;

    @TableField("work_requirement")
    @ApiModelProperty(value = "工作要求")
    @Excel(name = "工作要求", width = 10, isImportField = "true_st", orderNum = "12")
    private String workRequirement;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注", width = 10, isImportField = "true_st", orderNum = "13")
    private String remark;

    @TableField(exist = false)
    @Property(value = "当前导师被选的数量")
    private Integer topicCount;

}