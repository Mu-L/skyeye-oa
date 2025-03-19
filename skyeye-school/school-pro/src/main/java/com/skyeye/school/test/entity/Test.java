package com.skyeye.school.test.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;
/**
 *
 * 实体类
 *
 * */

@Data
//@UniqueField(value = {"id","name"})  // 唯一性校验
// @RedisCacheField(name = "school:student",cacheTime = RedisConstants.A_YEAR_SECONDS * 4 )
// 缓存时间四年
@TableName(value = "test")
@ApiModel(value = "测试实体类")
public class Test extends OperatorUserInfo {
//CommonInfo
    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("name")
    @ApiModelProperty(value ="学生姓名",required ="required")
    private String name;

    @TableField("age")
//    required 是否为必填项
    @ApiModelProperty(value ="学生年龄",required ="required")
    private Integer age;

    @TableField("chengji")
    @ApiModelProperty(value ="学生成绩")
    private double chengji;



}
