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
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
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
    @ApiModelProperty(value = "用户类型  1.管理员2.学生")
    private Integer type;

}