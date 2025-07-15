package com.skyeye.eve.servey.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.answer.service.DwAnAnswerService;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import com.skyeye.eve.chen.service.*;
import com.skyeye.eve.enumqu.service.DwAnEnumquService;
import com.skyeye.eve.multifllblank.service.DwAnDfillblankService;
import com.skyeye.eve.multifllblank.service.DwAnFillblankService;
import com.skyeye.eve.order.service.DwAnOrderService;
import com.skyeye.eve.radio.service.DwAnRadioService;
import com.skyeye.eve.score.service.DwAnScoreService;
import com.skyeye.eve.servey.dao.DwSurveyAnswerDao;
import com.skyeye.eve.servey.entity.DwSurveyAnswer;
import com.skyeye.eve.servey.entity.DwSurveyDirectory;
import com.skyeye.eve.servey.service.DwSurveyAnswerService;
import com.skyeye.eve.servey.service.DwSurveyDirectoryService;
import com.skyeye.eve.yesno.service.DwAnYesnoService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "问卷回答信息表管理", groupName = "问卷回答信息表管理")
public class DwSurveyAnswerServiceImpl extends SkyeyeBusinessServiceImpl<DwSurveyAnswerDao, DwSurveyAnswer> implements DwSurveyAnswerService {

    @Autowired
    private DwAnRadioService dwAnRadioService;
    @Autowired
    private DwAnCheckboxService dwAnCheckboxService;
    @Autowired
    private DwAnScoreService dwAnScoreService;
    @Autowired
    private DwAnOrderService dwAnOrderService;
    @Autowired
    private DwAnChenRadioService dwAnChenRadioService;
    @Autowired
    private DwAnChenCheckboxService dwAnChenCheckboxService;
    @Autowired
    private DwAnYesnoService dwAnYesnoService;
    @Autowired
    private DwAnAnswerService dwAnAnswerService;
    @Autowired
    private DwAnChenFbkService dwAnChenFbkService;
    @Autowired
    private DwAnChenScoreService dwAnChenScoreService;
    @Autowired
    private DwAnCompChenRadioService dwAnCompChenRadioService;
    @Autowired
    private DwAnDfillblankService dwAnDfillblankService;
    @Autowired
    private DwAnEnumquService dwAnEnumquService;
    @Autowired
    private DwAnFillblankService dwAnFillblankService;
    @Autowired
    private DwSurveyAnswerService dwSurveyAnswerService;
    @Autowired
    private DwSurveyDirectoryService dwSurveyDirectoryService;

    @Override
    protected void createPrepose(DwSurveyAnswer entity) {
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
    }

    @Override
    protected void updatePrepose(DwSurveyAnswer entity) {
        //  将时间差转换为总小时数（浮点数）
        String surveyId = entity.getSurveyId();
        Integer size = dwAnRadioService.selectRadioBySurveyId(surveyId).size();
        Integer size1 = dwAnScoreService.selectBySurveyId(surveyId).size();
        Integer size2 = dwAnYesnoService.selectBySurveyId(surveyId).size();
        Integer size3 = dwAnAnswerService.selectBySurveyId(surveyId).size();
        Integer size4 = dwAnCheckboxService.slectBySurveyId(surveyId).size();
        Integer size5 = dwAnChenCheckboxService.selectBySurveyId(surveyId).size();
        Integer size6 = dwAnChenFbkService.selectBySurveyId(surveyId).size();
        Integer size7 = dwAnChenRadioService.selectBySurveyId(surveyId).size();
        Integer size8 = dwAnChenScoreService.selectBySurveyId(surveyId).size();
        Integer size9 = dwAnCompChenRadioService.selectBySurveyId(surveyId).size();
        Integer size10 = dwAnDfillblankService.selectBySurveyId(surveyId).size();
        Integer size11 = dwAnEnumquService.selectBySurveyId(surveyId).size();
        Integer size12 = dwAnFillblankService.selectBySurveyId(surveyId).size();
        Integer size13 = dwAnOrderService.selectBySurveyId(surveyId).size();
        Integer total = size + size1 + size2 + size3 + size4 + size5 + size6 + size7 + size8 + size9 + size10 + size11 + size12 + size13;
        entity.setCompleteNum(total);
        String endAnDate = entity.getEndAnDate();
        if (StrUtil.isNotEmpty(endAnDate)) {
            entity.setIsComplete(CommonNumConstants.NUM_ONE);
        }
        if (entity.getHandleState().equals(CommonNumConstants.NUM_ONE) && entity.getState().equals(CommonNumConstants.NUM_TWO)) {
            Integer fraction = selectFractionBySurveyId(entity.getSurveyId());
            entity.setMarkFraction(fraction);
        }
    }

    @Override
    public DwSurveyAnswer selectById(String id) {
        DwSurveyAnswer dwSurveyAnswer = super.selectById(id);
        String surveyId = dwSurveyAnswer.getSurveyId();
        String createId = dwSurveyAnswer.getCreateId();
        DwSurveyDirectory dwSurveyDirectory = dwSurveyDirectoryService.selectBySurAndStuIds(surveyId, createId, id);
        dwSurveyAnswer.setSurveyMation(dwSurveyDirectory);
        return dwSurveyAnswer;
    }

    @Override
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        String createId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateId), createId);
        List<DwSurveyAnswer> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public DwSurveyAnswer queryWhetherExamIngByStuId(String userId, String id) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), id);
        return getOne(queryWrapper);
    }

    @Override
    public List<DwSurveyAnswer> querySurveyAnswer(String surveyId, String answerId, String userId) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), surveyId);
        queryWrapper.eq(CommonConstants.ID, answerId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateId), userId);
        return list(queryWrapper);
    }

    @Override
    public void queryNoOrYesSurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String state = map.get("state").toString();
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getState), state);
        List<DwSurveyAnswer> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String surveyId = commonPageInfo.getHolderId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), surveyId);
        List<DwSurveyAnswer> list = list(queryWrapper);
        iAuthUserService.setName(list, "createId", "createName");
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer page = commonPageInfo.getPage();
        Integer limit = commonPageInfo.getLimit();
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getState), CommonNumConstants.NUM_TWO);
        extracted(outputObject, queryWrapper, commonPageInfo, page, limit);
    }

    @Override
    public void queryFilterToBeReviewedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer page = commonPageInfo.getPage();
        Integer limit = commonPageInfo.getLimit();
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getState), CommonNumConstants.NUM_ONE);
        extracted(outputObject, queryWrapper, commonPageInfo, page, limit);
    }

    @Override
    public List<DwSurveyAnswer> querySurveyAnswerByBelongId(String dwDirectoryId) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), dwDirectoryId);
        return list(queryWrapper);
    }

    @Override
    public Integer selectFractionBySurveyId(String surveyId) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), surveyId);
        List<DwSurveyAnswer> dwSurveyAnswerList = list(queryWrapper);
        Integer sum = dwSurveyAnswerList.stream().mapToInt(DwSurveyAnswer::getMarkFraction).sum();
        return sum;
    }

    private void extracted(OutputObject outputObject, QueryWrapper<DwSurveyAnswer> queryWrapper, CommonPageInfo commonPageInfo, Integer page, Integer limit) {
        List<DwSurveyAnswer> beans = list(queryWrapper); // 获取所有的已批阅信息
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                DwSurveyDirectory surveyMation = examSurveyAnswer.getSurveyMation();
                return StrUtil.contains(surveyMation.getSurveyName(), commonPageInfo.getKeyword());
            }).collect(Collectors.toList());
        }
        int fromIndex = (page - 1) * limit;
        if (fromIndex >= beans.size()) {
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        }
        int toIndex = Math.min(fromIndex + limit, beans.size());
        outputObject.setBeans(beans.subList(fromIndex, toIndex));
        outputObject.settotal(beans.size());
    }

}
