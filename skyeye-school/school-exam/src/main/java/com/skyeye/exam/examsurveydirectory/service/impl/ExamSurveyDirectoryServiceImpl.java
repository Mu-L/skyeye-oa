package com.skyeye.exam.examsurveydirectory.service.impl;

import cn.hutool.core.io.unit.DataUnit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.Examquestion.dao.QuestionDao;
import com.skyeye.eve.Examquestion.entity.Question;
import com.skyeye.eve.Examquestion.service.QuestionService;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveyclass.service.ExamSurveyClassService;
import com.skyeye.exam.examsurveydirectory.dao.ExamSurveyDirectoryDao;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyDirectoryServiceImpl
 * @Description: 试卷管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷管理", groupName = "试卷管理")
public class ExamSurveyDirectoryServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyDirectoryDao, ExamSurveyDirectory> implements ExamSurveyDirectoryService {

    @Autowired
    private ExamSurveyClassService examSurveyClassService;

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    @Autowired
    private ExamSurveyMarkExamService examSurveyMarkExamService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExamSurveyAnswerService examSurveyAnswerService;

    @Autowired
    private QuestionDao questionDao;

    @Override
    public QueryWrapper<ExamSurveyDirectory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 我创建的
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void setUpExamDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ExamSurveyDirectory examSurveyDirectory = selectById(id);
        if (examSurveyDirectory != null) {
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ZERO)) {
                String belongId = examSurveyDirectory.getId();
                List<Question> questions = questionService.QueryQuestionByBelongId(belongId);
                if (!questions.isEmpty() && questions.size() > 0) {
                    //总分数
                    int fraction = 0;
                    //题目总数
                    int questionNum = 0;
                    for (Question question : questions) {
                        int questionType = Integer.parseInt(question.getQuType().toString());
                        if (questionType != 16 && questionType != 17) {
                            fraction += Integer.parseInt(question.getFraction().toString());
                            questionNum++;
                        }
                    }
                    examSurveyDirectory.setSurveyState(CommonNumConstants.NUM_ONE);
                    examSurveyDirectory.setFraction(fraction);
                    examSurveyDirectory.setSurveyQuNum(questionNum);
                } else {
                    throw new CustomException("该试卷没有调查项，无法发布试卷。");
                }
            } else {
                throw new CustomException("该试卷已发布，请刷新数据。");
            }
        } else {
            throw new CustomException("该试卷信息不存在。");
        }
    }

    @Override
    public ExamSurveyDirectory takeExam(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        //是否可以参加考试，true：可以；false：不可以
        boolean yesOrNo = false;
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String id = map.get("id").toString();
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(id);
        if (examSurveyDirectory != null) {//判断试卷是否存在
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {//判断试卷是否发布
                if (!ToolUtil.isBlank(userId)) {//判断用户是否登录
                    ExamSurveyAnswer examSurveyAnswer = examSurveyAnswerService.queryWhetherExamIngByStuId(userId, id);
                    if (examSurveyAnswer != null) {//判断用户是否已经参加过该考试
                        throw new CustomException("您已参加过该考试");
                    } else {
                        yesOrNo = true;
                    }
                } else {
                    throw new CustomException("您不具备该考试权限");
                }
            } else {
                throw new CustomException("该试卷未发布");
            }
        } else {
            throw new CustomException("该试卷不存在");
        }
        if (yesOrNo) {
            return examSurveyDirectory;
        } else {
            throw new CustomException("您不具备该考试权限");
        }
    }

    @Override
    public void copyExamDirectory(InputObject inputObject, OutputObject outputObject) {
        ExamSurveyDirectory examSurveyDirectories = new ExamSurveyDirectory();
        Map<String, Object> map = inputObject.getParams();
        String quId = map.get("id").toString();//试卷id
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String surveyId = ToolUtil.getSurFaceId();
        examSurveyDirectories.setId(surveyId);
        examSurveyDirectories.setSid(ToolUtil.randomStr(6, 12));
        examSurveyDirectories.setSurveyModel(1);
        examSurveyDirectories.setCreateId(userId);
        examSurveyDirectories.setCreateTime(DateUtil.getTimeAndToString());
        List<Question> questionList = Collections.singletonList(questionDao.selectById(quId));
        for (Question question : questionList) {
            question.setCopyFromId(quId);
            question.setId(ToolUtil.getSurFaceId());
            question.setCreateTime(DateUtil.getTimeAndToString());
            question.setBelongId(surveyId);
            questionService.copyQuestionListMation(question);
        }
//        questionService.createEntity(questions, userId);


    }

    @Override
    public void createPrepose(ExamSurveyDirectory examSurveyDirectory) {
        LocalDateTime realStartTime = examSurveyDirectory.getRealStartTime();
        LocalDateTime realEndTime = examSurveyDirectory.getRealEndTime();
        if (realStartTime != null && realEndTime != null) {
            if (realStartTime.isAfter(realEndTime)) {
                throw new CustomException("实际开始时间不能晚于实际结束时间");
            }
        }
    }

    @Override
    protected void createPostpose(ExamSurveyDirectory entity, String userId) {
        String id = entity.getId();
        String classId = entity.getClassId();
        examSurveyClassService.createExamSurveyClass(id, classId, userId);
        examSurveyMarkExamService.createExamSurveyMarkExam(id, userId);
    }

    @Override
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_TWO);
        update(updateWrapper);
    }

}
