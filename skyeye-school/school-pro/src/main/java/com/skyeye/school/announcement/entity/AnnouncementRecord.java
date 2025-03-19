package com.skyeye.school.announcement.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: AnnouncementRecord
 * @Description: 公告收到记录管理实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@RedisCacheField(name = "school:announcementRecord", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_announcement_record")
@ApiModel(value = "公告收到记录管理实体类")
public class AnnouncementRecord extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id,没有则新增，有就编辑")
    private String id;

    @TableField("announcement_id")
    @ApiModelProperty(value = "公告id",required = "required")
    private String announcementId;

    @TableField("title")
    @ApiModelProperty(value = "公告标题")
    private String title;

    @TableField("stu_no")
    @ApiModelProperty(value = "阅读者学号")
    private String stuNo;

    @TableField("subject_classes_id")
    @ApiModelProperty(value = "阅读者的班级id")
    private String subjectClassesId;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的id")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的serviceClassName")
    private String objectKey;

    @TableField("state")
    @ApiModelProperty(value = "阅读状态，已阅读'1'/未阅读'0'")
    private Integer state;
}
