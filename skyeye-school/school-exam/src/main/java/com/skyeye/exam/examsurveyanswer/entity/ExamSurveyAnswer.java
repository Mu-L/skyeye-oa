package com.skyeye.exam.examsurveyanswer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.exam.examananswer.entity.ExamAnAnswer;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchenfbk.entity.ExamAnChenFbk;
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;
import com.skyeye.exam.examanenumqu.entity.ExamAnEnumqu;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanorder.entity.ExamAnOrder;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import com.skyeye.exam.examanyesno.entity.ExamAnYesno;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.major.entity.Major;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyAnswer
 * @Description: 试卷回答信息表实体类
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "exam_survey_answer")
@ApiModel("试卷回答信息表实体类")
public class ExamSurveyAnswer extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("survey_id")
    @ApiModelProperty(value = "试卷ID", required = "required")
    private String surveyId;

    @TableField("bg_an_date")
    @ApiModelProperty(value = "回答开始时间")
    private String bgAnDate;

    @TableField("end_an_date")
    @ApiModelProperty(value = "回答结束时间")
    private String endAnDate;

    @TableField("complete_num")
    @ApiModelProperty(value = "回答的题数")
    private Integer completeNum;

    @TableField("complete_item_num")
    @ApiModelProperty(value = "回答的题项目数 ---- 表示有些题下面会有多重回答")
    private Integer completeItemNum;

    @TableField("data_source")
    @ApiModelProperty(value = "数据来源  0网调  1录入数据 2移动数据 3导入数据", required = "required")
    private Integer dataSource;

    @TableField("handle_state")
    @ApiModelProperty(value = "审核状态  0未处理 1通过 2不通过", defaultValue = "0")
    private Integer handleState;

    @TableField("ip_addr")
    @ApiModelProperty(value = "回答者IP")
    private String ipAddr;

    @TableField("addr")
    @ApiModelProperty(value = "回答者是详细地址")
    private String addr;

    @TableField("city")
    @ApiModelProperty(value = "回答者城市 ")
    private String city;

    @TableField("is_complete")
    @ApiModelProperty(value = "是否完成  1完成 0未完成")
    private Integer isComplete;

    @TableField("is_effective")
    @ApiModelProperty(value = "是否是有效数据  1有效  0无效")
    private Integer isEffective;

    @TableField("qu_num")
    @ApiModelProperty(value = "回答的题数", required = "required")
    private Integer quNum;

    @TableField("total_time")
    @ApiModelProperty(value = "用时")
    private String totalTime;

    @TableField("state")
    @ApiModelProperty(value = "教师是否阅卷  1.否  2.是")
    private Integer state;

    @TableField("mark_fraction")
    @ApiModelProperty(value = "最后得分")
    private Float markFraction;

    @TableField("mark_people")
    @ApiModelProperty(value = "阅卷人")
    private String markPeople;

    @TableField("mark_start_time")
    @ApiModelProperty(value = "开始阅卷时间")
    private String markStartTime;

    @TableField("mark_end_time")
    @ApiModelProperty(value = "结束阅卷时间")
    private String markEndTime;

    @TableField("student_number")
    @ApiModelProperty(value = "学号", required = "required")
    private String studentNumber;

    @TableField("school_id")
    @ApiModelProperty(value = "学校ID")
    private String schoolId;

    @TableField("faculty_id")
    @ApiModelProperty(value = "所属学院")
    private String facultyId;

    @TableField("major_id")
    @ApiModelProperty(value = "所属专业")
    private String majorId;

    @TableField("subject_id")
    @ApiModelProperty(value = "所属科目")
    private String subjectId;

    @TableField("class_id")
    @ApiModelProperty(value = "所属班级")
    private String classId;

    @TableField(exist = false)
    @Property(value = "所属专业")
    private Major majorMation;

    @TableField(exist = false)
    @Property(value = "所属学院")
    private Faculty facultyMation;

    @TableField(exist = false)
    @Property(value = "学校信息")
    private School schoolMation;

    @TableField(exist = false)
    @Property(value = "试卷信息")
    private ExamSurveyDirectory surveyMation;

    @TableField(exist = false)
    @Property(value = "试卷信息")
    private List<ExamSurveyDirectory> surveysMation;

    @TableField(exist = false)
    @Property(value = "学生信息")
    private Map<String, Object> stuMation;

    @TableField(exist = false)
    @Property(value = "学生信息")
    private UserOrStudent userMation;

    @TableField(exist = false)
    @Property(value = "老师信息")
    private UserOrStudent teacherMation;

    @TableField(exist = false)
    @Property(value = "单选题信息")
    private List<ExamAnRadio> examAnRadioList;

    @TableField(exist = false)
    @Property(value = "评分题信息")
    private List<ExamAnScore> examAnScoreList;

    @TableField(exist = false)
    @Property(value = "判断题信息")
    private List<ExamAnYesno> examAnYesnoList;

    @TableField(exist = false)
    @Property(value = "问答题信息")
    private List<ExamAnAnswer> examAnAnswerList;

    @TableField(exist = false)
    @Property(value = "多选题信息")
    private List<ExamAnCheckbox> examAnCheckboxList;

    @TableField(exist = false)
    @Property(value = "矩阵多选题信息")
    private List<ExamAnChenCheckbox> examAnChenCheckboxList;

    @TableField(exist = false)
    @Property(value = "矩阵填空题信息")
    private List<ExamAnChenFbk> examAnChenFbkList;

    @TableField(exist = false)
    @Property(value = "矩阵单选题信息")
    private List<ExamAnChenRadio> examAnChenRadioList;

    @TableField(exist = false)
    @Property(value = "矩阵评分题信息")
    private List<ExamAnChenScore> examAnChenScoreList;

    @TableField(exist = false)
    @Property(value = "复合矩阵单选题信息")
    private List<ExamAnCompChenRadio> examAnCompChenRadioList;

    @TableField(exist = false)
    @Property(value = "多行填空题信息")
    private List<ExamAnDfillblank> examAnDfillblankList;

    @TableField(exist = false)
    @Property(value = "枚举题信息")
    private List<ExamAnEnumqu> examAnEnumquList;

    @TableField(exist = false)
    @Property(value = "填空题信息")
    private List<ExamAnFillblank> examAnFillblankList;

    @TableField(exist = false)
    @Property(value = "评分题信息")
    private List<ExamAnOrder> examAnOrderList;
}