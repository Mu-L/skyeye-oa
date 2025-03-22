package com.skyeye.school.datum.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.chapter.entity.Chapter;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Datum
 * @Description: 资料管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "school:datum", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "school_datum")
@ApiModel(value = "资料实体类")
public class Datum extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "资料名称", required = "required")
    private String name;

    @TableField("annex")
    @ApiModelProperty(value = "附件", required = "json")
    private String annex;

    @TableField("`remark`")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField("upload_datum")
    @ApiModelProperty(value = "内容", required = "required")
    private String uploadDatum;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的serviceClassName", required = "required")
    private String objectKey;

    @TableField(value = "subject_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目表id", required = "required")
    private String subjectId;

    @TableField("chapter_id")
    @ApiModelProperty(value = "所属章节Id", required = "required")
    private String chapterId;

    @TableField(exist = false)
    @ApiModelProperty(value = "章节信息", required = "json")
    private  Chapter chapterMation;

}
