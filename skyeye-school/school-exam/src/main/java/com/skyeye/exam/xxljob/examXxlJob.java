package com.skyeye.exam.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Joiner;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamXxlJob
 * @Description: 试卷打分
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/15 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class examXxlJob {

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ExamSurveyAnswerService examSurveyAnswerService;

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IQuartzService iQuartzService;

    private static Logger log = LoggerFactory.getLogger(examXxlJob.class);


    @XxlJob("createExam")
    public void createExam() {
        String param = XxlJobHelper.getJobParam();
        Map<String, Object> paramMap = JSONUtil.toBean(JSONUtil.toJsonStr(param), null);
        String userId = paramMap.get("userId").toString();
        String examId = paramMap.get("objectId").toString();
        log.info("get paramMap:userId" + userId + "examId" + examId);
        try {
            ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(examId);
            String subjectId = examSurveyDirectory.getSubjectId();
            // 查询班级信息
            List<SubjectClasses> subjectClassesList = subjectClassesService.querySubjectClassesByObjectId(subjectId);
            if (CollectionUtil.isEmpty(subjectClassesList)) {
                return;
            }
            List<String> subjectClassesIdList = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
            // 查询班级的所有学生
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkIds(subjectClassesIdList);
            List<String> allStuNo = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
            // 查询学生的答题信息
            List<ExamSurveyAnswer> examSurveyAnswerList = examSurveyAnswerService.queryListByStuNoListAndExamId(allStuNo, examId);
            List<String> haveStuNoList = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getStudentNumber).collect(Collectors.toList());
            // 找出没有答题的学生学号
            List<String> notHaveStuNoList = allStuNo.stream().filter(stuNo -> !haveStuNoList.contains(stuNo)).collect(Collectors.toList());
            log.info("notHaveStuNoList:{}", Arrays.toString(notHaveStuNoList.toArray()));
            if (CollectionUtil.isNotEmpty(notHaveStuNoList)) {
                // 根据学号获取学生的用户信息
                List<Map<String, Object>> userList = iUserService.queryListBuStudentNumberList(Joiner.on(CommonCharConstants.COMMA_MARK).join(notHaveStuNoList));
                log.info("userList:{}", Arrays.toString(userList.toArray()));
                // 过滤出学号和id
                Map<String, String> stuNoIdMap = userList.stream().collect(Collectors.toMap(user -> user.get("studentNumber").toString(), user -> user.get("id").toString()));
                // 创造零分答题信息
                List<ExamSurveyAnswer> examSurveyAnswerList1 = new ArrayList<>();
                for (String s : notHaveStuNoList) {
                    ExamSurveyAnswer examSurveyAnswer = new ExamSurveyAnswer();
                    examSurveyAnswer.setSurveyId(examId);
                    examSurveyAnswer.setCompleteNum(CommonNumConstants.NUM_ZERO);
                    examSurveyAnswer.setCompleteItemNum(CommonNumConstants.NUM_ZERO);
                    examSurveyAnswer.setDataSource(CommonNumConstants.NUM_ZERO);
                    examSurveyAnswer.setHandleState(CommonNumConstants.NUM_ONE);
                    examSurveyAnswer.setIsComplete(CommonNumConstants.NUM_ZERO);
                    examSurveyAnswer.setQuNum(CommonNumConstants.NUM_ZERO);
                    examSurveyAnswer.setCreateId(stuNoIdMap.getOrDefault(s, "not exits stuNo duty data"));
                    examSurveyAnswer.setState(CommonNumConstants.NUM_ONE);
                    examSurveyAnswer.setMarkFraction(CommonNumConstants.NUM_ZERO.floatValue());
                    examSurveyAnswer.setStudentNumber(s);
                    examSurveyAnswerList1.add(examSurveyAnswer);
                }
                log.info("examSurveyAnswerList1:{}", Arrays.toString(examSurveyAnswerList1.toArray()));
                examSurveyAnswerService.createEntity(examSurveyAnswerList1, userId);
            }
        } finally {
            log.info("考试---删除任务---开始");
            iQuartzService.stopAndDeleteTaskQuartz(examId);// 删除任务
            log.info("考试---删除任务---结束");
        }
    }

}
