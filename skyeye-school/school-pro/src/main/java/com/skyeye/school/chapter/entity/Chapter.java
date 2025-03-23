/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chapter.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: Chapter
 * @Description: 章节实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/25 10:59
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "name"})
@RedisCacheField(name = "school:chapter", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_chapter")
@ApiModel(value = "章节实体类")
public class Chapter extends BaseGeneralInfo {

    @TableField("section")
    @ApiModelProperty(value = "第几部分", required = "required")
    private Integer section;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的serviceClassName", required = "required")
    private String objectKey;

    @TableField(value = "subject_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目表id", required = "required")
    private String subjectId;

    @TableField(value = "annex")
    @ApiModelProperty(value = "附件")
    private String annex;

}
