package com.skyeye.exam.examanchencheckbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanchencheckbox.dao.ExamAnChenCheckboxDao;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnChenCheckboxServiceImpl
 * @Description: 答卷 矩阵多选题管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 矩阵多选题", groupName = "答卷 矩阵多选题")
public class ExamAnChenCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnChenCheckboxDao, ExamAnChenCheckbox> implements ExamAnChenCheckboxService {


    @Override
    public void queryExamAnChenCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnChenCheckbox> examAnChenCheckboxList = list(queryWrapper);
        outputObject.setBean(examAnChenCheckboxList);
        outputObject.settotal(examAnChenCheckboxList.size());
    }

    @Override
    public List<ExamAnChenCheckbox> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnChenCheckbox> selectAnChenCheckboxByQuId(String id) {
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getQuId), id);
        return list(queryWrapper);
    }
}
