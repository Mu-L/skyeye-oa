package com.skyeye.exam.examanquestion.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ExamAnQuestion
 * @Description: 答卷 试题答案相关信息表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "Exam:question")
@TableName(value = "exam_an_question")
@ApiModel("答卷 试题答案相关信息表实体类")
public class ExamAnQuestion extends CommonInfo {
    @TableId("answer_id")
    @ApiModelProperty(value = "答卷id。为空时新增，不为空时编辑",required = "required")
    private String answerId;

    @TableField("qu_id")
    @ApiModelProperty(value = "问题id。为空时新增，不为空时编辑",required = "required")
    private String quId;

    @TableField("user_id")
    @ApiModelProperty(value = "学生id。为空时新增，不为空时编辑",required = "required")
    private String userId;

    @TableField("en_file_url")
    @ApiModelProperty(value = "学生上传的图片")
    private String enFileUrl;
}
