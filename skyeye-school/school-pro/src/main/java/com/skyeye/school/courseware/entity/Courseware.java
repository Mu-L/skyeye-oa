/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.school.chapter.entity.Chapter;
import lombok.Data;

/**
 * @ClassName: Courseware
 * @Description: 互动课件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 9:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "school:courseware", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_courseware")
@ApiModel(value = "互动课件实体类")
public class Courseware extends BaseGeneralInfo {

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的serviceClassName", required = "required")
    private String objectKey;

    @TableField("chapter_id")
    @ApiModelProperty(value = "所属章节id", required = "required")
    private String chapterId;

    @TableField(exist = false)
    @Property(value = "所属章节信息")
    private Chapter chapterMation;

    @TableField(exist = false)
    @Property(value = "学生学习的状态，学生身份获取互动课件时需要这个字段")
    private String state;

    @TableField("annex")
    @ApiModelProperty(value = "附件")
    private String annex;

}
