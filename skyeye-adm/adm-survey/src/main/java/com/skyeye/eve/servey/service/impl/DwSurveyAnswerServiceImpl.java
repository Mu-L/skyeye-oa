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
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.servey.dao.DwSurveyAnswerDao;
import com.skyeye.eve.servey.entity.DwSurveyAnswer;
import com.skyeye.eve.servey.entity.DwSurveyDirectory;
import com.skyeye.eve.servey.service.DwSurveyAnswerService;
import com.skyeye.eve.servey.service.DwSurveyDirectoryService;
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
    private DwSurveyDirectoryService dwSurveyDirectoryService;

    @Override
    protected void createPrepose(DwSurveyAnswer entity) {
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        String Ip = ToolUtil.getIpByRequest(PutObject.getRequest());
        entity.setIp(Ip);

        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
    }

    @Override
    protected void createPostpose(DwSurveyAnswer entity, String userId) {
        dwSurveyDirectoryService.addAnswerNum(entity.getSurveyId());
    }

    @Override
    protected void updatePrepose(DwSurveyAnswer entity) {
        //  将时间差转换为总小时数（浮点数）
        String endAnDate = entity.getEndAnDate();
        if (StrUtil.isNotEmpty(endAnDate)) {
            entity.setIsComplete(CommonNumConstants.NUM_ONE);
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
    public List<DwSurveyAnswer> queryWhetherExamIngByStuId(String userId, String id) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), id);
        return list(queryWrapper);
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

    @Override
    public void querySurveyAnswerByDirectoryIdAndUserId(InputObject inputObject, OutputObject outputObject) {
        // 试卷Id
        String SurveyId = inputObject.getParams().get("id").toString();
        // 用户Id
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), SurveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwSurveyAnswer::getCreateTime));
        List<DwSurveyAnswer> dwSurveyAnswerList = list(queryWrapper);
        // 获取最新一条记录
        DwSurveyAnswer dwSurveyAnswer = dwSurveyAnswerList.get(CommonNumConstants.NUM_ZERO);
        String surveyId = dwSurveyAnswer.getSurveyId();
        DwSurveyDirectory dwSurveyDirectory = dwSurveyDirectoryService.selectDirectoryAndAnswerById(surveyId, userId, dwSurveyAnswer.getId());
        dwSurveyAnswer.setSurveyMation(dwSurveyDirectory);
        outputObject.setBean(dwSurveyAnswer);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public DwSurveyAnswer querySurveyAnswerByRuleCode(String machineCode, String id) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getMachineCode), machineCode);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), id);
        return getOne(queryWrapper);
    }

    @Override
    public DwSurveyAnswer querySurveyAnswerByIp(String ip, String id) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getIp), ip);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), id);
        return getOne(queryWrapper);
    }

    @Override
    public List<DwSurveyAnswer> querySurveyAnswerNumById(String id) {
        QueryWrapper<DwSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyAnswer::getSurveyId), id);
        return list(queryWrapper);
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
