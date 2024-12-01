package com.skyeye.exam.examSurveyDirectory.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examSurveyClass.entity.ExamSurveyClass;
import com.skyeye.exam.examSurveyClass.service.ExamSurveyClassService;
import com.skyeye.exam.examSurveyDirectory.dao.ExamSurveyDirectoryDao;
import com.skyeye.exam.examSurveyDirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examSurveyDirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Override
    public void validatorEntity(ExamSurveyDirectory examSurveyDirectory) {
        LocalDateTime realStartTime= examSurveyDirectory.getRealStartTime();
        LocalDateTime realEndTime= examSurveyDirectory.getRealEndTime();
        if(realStartTime != null && realEndTime != null){
            if(realStartTime.isAfter(realEndTime)){
                throw new CustomException("实际开始时间不能晚于实际结束时间");
            }
        }
    }

    @Override
    public void createPostpose(ExamSurveyDirectory examSurveyDirectory,String userId){
        ExamSurveyClass examSurveyClass = new ExamSurveyClass();
        String classId = examSurveyDirectory.getClassId();
        String examSurveyId = examSurveyDirectory.getId();
        examSurveyClass.setClassId(classId);
        examSurveyClass.setExamSurveyId(examSurveyId);
        examSurveyClassService.createEntity(examSurveyClass,userId);
    }

    /**
     * 分页获取所有试卷或者我的试卷
     * */
    @Override
    public void queryAllOrMyExamList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String createId;
        Object createIdObj = params.get("createId");
        if(ObjectUtil.isNotNull(createIdObj)){
            // createId不为空
            createId = createIdObj.toString();
        } else {
            createId = null;
        }
        List<ExamSurveyDirectory> bean = queryAllData()
                .stream().filter(s -> s.getWhetherDelete() == 1)
                .collect(Collectors.toList());
        if(StrUtil.isEmpty(createId)){
            outputObject.setBeans(bean);
            outputObject.settotal(bean.size());
        }
        // 获取我的试卷，根据createId查询
        else {
            List<ExamSurveyDirectory> beans = bean.stream().filter(s -> createId.equals(s.getCreateId())).collect(Collectors.toList());
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }

    @Override
    @Transactional
    public void deleteDirectoryById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        LambdaQueryWrapper<ExamSurveyDirectory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamSurveyDirectory::getId, id);
        ExamSurveyDirectory examSurveyDirectory = getOne(queryWrapper);
        examSurveyDirectory.setWhetherDelete(CommonNumConstants.NUM_ZERO);
        updateById(examSurveyDirectory);
        // 删除试卷与班级的关联
        LambdaQueryWrapper<ExamSurveyClass> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ExamSurveyClass::getExamSurveyId, id);
        ExamSurveyClass examSurveyClass = examSurveyClassService.getOne(queryWrapper1);
        examSurveyClassService.removeById(examSurveyClass.getId());
    }
}
