/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.activity.classenum.ActivityType;
import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chtopic.classenum.TeacherResultState;
import com.skyeye.chtopic.dao.ChooseTopicDao;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.chtopic.service.ChooseTopicService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.entity.ChooseUser;
import com.skyeye.user.enumclass.ChooseUserType;
import com.skyeye.user.service.ChooseUserService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ChooseTopicServiceImpl
 * @Description: 课题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "课题管理", groupName = "课题管理")
public class ChooseTopicServiceImpl extends SkyeyeBusinessServiceImpl<ChooseTopicDao, ChooseTopic> implements ChooseTopicService {

    @Autowired
    private ChooseUserService chooseUserService;

    @Autowired
    private ChooseActivityService chooseActivityService;

    @Override
    protected QueryWrapper<ChooseTopic> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ChooseTopic> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        chooseUserService.setMationForMap(beans, "chooseUserId", "chooseUserMation");
        chooseUserService.setMationForMap(beans, "teacherId", "teacherMation");
        chooseActivityService.setMationForMap(beans, "activityId", "activityMation");
        return beans;
    }

    @Override
    public void importChooseTopic(InputObject inputObject, OutputObject outputObject) {
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            String activityId = inputObject.getParams().get("activityId").toString();
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                ImportParams reportModelAttrParams = new ImportParams();
                reportModelAttrParams.setStartSheetIndex(0);
                List<ChooseTopic> chooseTopicList;
                try {
                    chooseTopicList = ExcelImportUtil.importExcel(file.getInputStream(), ChooseTopic.class, reportModelAttrParams);
                } catch (Exception ee) {
                    throw new CustomException(ee);
                }
                List<ChooseTopic> insertList = checkChooseTopicListExit(chooseTopicList);
                if (CollectionUtil.isEmpty(insertList)) {
                    return;
                }
                insertList.forEach(bean -> {
                    Map<String, Object> business = BeanUtil.beanToMap(bean);
                    bean.setChoose(1);
                    bean.setActivityId(activityId);
                    bean.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business));
                });
                createEntity(insertList, StrUtil.EMPTY);
            }
        }
    }

    /**
     * 上传课题列表时检查数据
     *
     * @param chooseTopicList
     * @return 数据库不存在的数据
     */
    private List<ChooseTopic> checkChooseTopicListExit(List<ChooseTopic> chooseTopicList) {
        if (CollectionUtil.isEmpty(chooseTopicList)) {
            return Collections.emptyList();
        }
        // 过滤标题为空的数据
        List<ChooseTopic> insertList = chooseTopicList.stream()
            .filter(chooseTopic -> StrUtil.isNotEmpty(chooseTopic.getTitle())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(insertList)) {
            return Collections.emptyList();
        }
        // 获取标题集合
        List<String> titilList = insertList.stream().map(ChooseTopic::getTitle).collect(Collectors.toList());
        String titleColumn = MybatisPlusUtil.toColumns(ChooseTopic::getTitle);
        // 查询数据库已经存在的标题
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(titleColumn);
        queryWrapper.in(titleColumn, titilList);
        List<ChooseTopic> exitChooseTopicList = list(queryWrapper);
        if (CollectionUtil.isEmpty(exitChooseTopicList)) {
            return insertList;
        }
        List<String> exitsTitle = exitChooseTopicList.stream().map(ChooseTopic::getTitle).collect(Collectors.toList());
        // 过滤掉已存在的数据
        List<ChooseTopic> resultList = insertList.stream().filter(chooseTopic -> !exitsTitle.contains(chooseTopic.getTitle())).collect(Collectors.toList());
        return resultList;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void chooseTopicById(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String id = inputObject.getParams().get("id").toString();
        String teacherId = inputObject.getParams().get("teacherId").toString();
        ChooseTopic chooseTopic = selectById(id);
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该数据不存在");
        }
        if (StrUtil.isEmpty(chooseTopic.getActivityId())) {
            throw new CustomException("该课题未关联活动,课题未发布");
        }
        // 准备修改数据
        UpdateWrapper<ChooseTopic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        boolean activityIsRun = chooseActivityService.checkActivityIsRun(chooseActivity);
        if (activityIsRun) {
            // 选题活动正在进行中，一定会进行选题操作
            if (chooseTopic.getChoose() == CommonNumConstants.NUM_TWO && !StrUtil.equals(chooseTopic.getChooseUserId(), userId)) {
                throw new CustomException("该课题已被选择");
            }
            QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), chooseTopic.getActivityId());
            queryWrapper.ne(CommonConstants.ID, id);
            ChooseTopic one = getOne(queryWrapper, false);
            if (ObjectUtil.isNotEmpty(one)) {
                throw new CustomException("您已选题，请勿重复选题");
            }
            // 设置选题标志
            updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_TWO);
            updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
        } else if (StrUtil.isEmpty(teacherId)) {
            // 选题活动已结束/未开始,并且不是修改导师
            throw new CustomException("该活动未开始或已结束");
        }
        if (StrUtil.isNotEmpty(teacherId) && chooseActivityService.checkActivityIsStart(chooseActivity)) {
            // 选题活动只要开始了(活动结束也可以)，都可以修改导师
            if (!checkTeacherId(teacherId)) {
                throw new CustomException("导师不存在或该角色不是教师");
            }
            if (checkTeacherOverLimit(teacherId, chooseTopic.getActivityId())) {
                throw new CustomException("已超过该导师的最大选择次数，请选择其他导师");
            }
            updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), teacherId);
            // 设置导师审核状态，如果活动为单选类型，则导师直接同意，否则待导师审核
            updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult),
                Objects.equals(chooseActivity.getType(), ActivityType.SINGLE.getKey()) ? TeacherResultState.AGREE.getKey() : TeacherResultState.WAITE.getKey());
        }
        update(updateWrapper);
        refreshCache(id);
    }

    /**
     * 检查导师选择次数是否大于等于8次(不包含8)
     *
     * @param teacherId  教师id
     * @param activityId 活动id
     * @return ture:大于等于8次  false:小于8次
     */
    private boolean checkTeacherOverLimit(String teacherId, String activityId) {
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID)
            .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), teacherId)
            .eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId)
            .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), TeacherResultState.AGREE.getKey());
        long count = count(queryWrapper);
        ChooseUser chooseUser = chooseUserService.selectById(teacherId);
        int maxCount = chooseUser.getGuideCapacity() == null ? CommonNumConstants.NUM_EIGHT : chooseUser.getGuideCapacity();
        return count >= maxCount;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cnacleChooseTopicById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ChooseTopic chooseTopic = selectById(id);
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该数据不存在");
        }
        if (StrUtil.isEmpty(chooseTopic.getActivityId())) {
            throw new CustomException("该课题未关联活动,课题未发布");
        }
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        boolean activityIsRun = chooseActivityService.checkActivityIsRun(chooseActivity);
        if (!activityIsRun) {
            throw new CustomException("该活动未开始或已结束,不可取消选课");
        }
        if (chooseTopic.getChoose() == CommonNumConstants.NUM_ONE) {
            throw new CustomException("该课题未被选择");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        if (!StrUtil.equals(userId, chooseTopic.getChooseUserId())) {
            throw new CustomException("您无法取消他人选择的课题");
        }
        UpdateWrapper<ChooseTopic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_ONE);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), StrUtil.EMPTY);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), null);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), StrUtil.EMPTY);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void exportChooseTopic(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();
        exportExcel(activityId);
    }

    private void exportExcel(String activityId) {
        HttpServletResponse response = InputObject.getResponse();
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        // 导出数据
        List<ChooseTopic> list = list(queryWrapper);
        chooseUserService.setDataMation(list, ChooseTopic::getChooseUserId);
        chooseUserService.setDataMation(list, ChooseTopic::getTeacherId);
        chooseActivityService.setDataMation(list, ChooseTopic::getActivityId);
        list.forEach(bean -> {
            if (ObjectUtil.isNotEmpty(bean.getChooseUserMation())) {
                bean.setChooseUserName(bean.getChooseUserMation().getName());
            }
            if (ObjectUtil.isNotEmpty(bean.getActivityMation())) {
                bean.setActivityName(bean.getActivityMation().getName());
                bean.setActivityType(bean.getActivityMation().getType());
            }
            if (ObjectUtil.isNotEmpty(bean.getTeacherMation())) {
                bean.setMentorName(bean.getTeacherMation().getName());
            }
        });
        //.xls格式
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        //前端存在跨域不成功，设置可访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        //设置不要缓存
        response.setHeader("Pragma", "No-cache");
        try {
            response.setHeader("Content-disposition", "attachment;filename=chooseTopicResult.xls");
            //设置sheet名
            ExportParams params = new ExportParams();
            params.setSheetName("学生选题结果表");
            // 这里需要设置不关闭流
            Workbook workbook = ExcelExportUtil.exportExcel(params, ChooseTopic.class, list);
            //输出流
            OutputStream outStream = response.getOutputStream();
            //浏览器下载
            workbook.write(outStream);
            //关闭流
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<ChooseTopic> queryListByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)) {
            return new ArrayList<>();
        }
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        List<ChooseTopic> chooseTopicList = list(queryWrapper);
        chooseUserService.setDataMation(chooseTopicList, ChooseTopic::getChooseUserId);
        chooseUserService.setDataMation(chooseTopicList, ChooseTopic::getTeacherId);
        return chooseTopicList;
    }

    @Override
    public void queryChooseMeTopicList(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        String activityId = inputObject.getParams().get("activityId").toString();
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(activityId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), currentUserId);
        List<ChooseTopic> beans = list(queryWrapper);
        chooseUserService.setDataMation(beans, ChooseTopic::getChooseUserId);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void changeResultForTeacher(InputObject inputObject, OutputObject outputObject) {
        Integer teacherResult = Integer.valueOf(inputObject.getParams().get("teacherResult").toString());
        String currentUserId = inputObject.getLogParams().get("id").toString();
        ChooseTopic chooseTopic = selectById(inputObject.getParams().get("id").toString());
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该课题不存在");
        }
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        if (Objects.equals(chooseActivity.getType(), ActivityType.SINGLE.getKey())) {
            throw new CustomException("该课题属于单选类型活动，不可修改");
        }
        if (!StrUtil.equals(currentUserId, chooseTopic.getTeacherId())) {
            throw new CustomException("该课题信息你不是指导老师，不可修改");
        }
        // 判断入参是否合法，合法则修改，非法则报错
        if (teacherResult.equals(TeacherResultState.AGREE.getKey()) ||
            teacherResult.equals(TeacherResultState.NOT_AGREE.getKey())) {
            chooseTopic.setTeacherResult(teacherResult);
            super.updateEntity(chooseTopic, currentUserId);
        } else {
            throw new CustomException("非法状态");
        }
        boolean overLimit = checkTeacherOverLimit(currentUserId, chooseActivity.getId());
        if (overLimit) {
            // 超过数量(8个)限制，其他同学自动拒绝
            UpdateWrapper<ChooseTopic> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), currentUserId)
                .eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), chooseActivity.getId())
                .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), TeacherResultState.WAITE.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), TeacherResultState.NOT_AGREE.getKey());
            List<ChooseTopic> list = list(updateWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                List<String> chooseTopicIdList = list.stream().map(ChooseTopic::getId).collect(Collectors.toList());
                update(updateWrapper);
                refreshCache(chooseTopicIdList);
            }
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelTeacherResult(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        ChooseTopic chooseTopic = selectById(inputObject.getParams().get("id").toString());
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该课题不存在");
        }
        if (StrUtil.isEmpty(chooseTopic.getChooseUserId())) {
            throw new CustomException("该课题未关联学生，不可取消");
        }
        if (!StrUtil.equals(currentUserId, chooseTopic.getChooseUserId())) {
            throw new CustomException("该课题信息你不是选择的学生，不可取消");
        }
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        if (chooseActivity.getType() == ActivityType.UN_SINGLE.getKey() && chooseTopic.getTeacherResult() == TeacherResultState.AGREE.getKey()) {
            throw new CustomException("多选类型活动，导师同意后不可取消选择导师");
        }
        chooseTopic.setTeacherId(StrUtil.EMPTY);
        chooseTopic.setTeacherResult(null);
        super.updateEntity(chooseTopic, currentUserId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void chooseTeacher(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String currentUserId = inputObject.getLogParams().get("id").toString();
        ChooseTopic chooseTopic = selectById(params.get("id").toString());
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该课题不存在");
        }
        if (StrUtil.isEmpty(chooseTopic.getActivityId())) {
            throw new CustomException("该课题未关联活动，不可选择导师");
        }
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        if (!chooseActivityService.checkActivityIsStart(chooseActivity)) {
            throw new CustomException("该活动未开始，不可选择导师");
        }
        if (StrUtil.isEmpty(chooseTopic.getChooseUserId())) {
            throw new CustomException("该课题未关联学生，不可选择导师");
        }
        if (!StrUtil.equals(currentUserId, chooseTopic.getChooseUserId())) {
            throw new CustomException("你未选择该课题，不可选择导师");
        }
        if (Objects.equals(chooseActivity.getType(), ActivityType.UN_SINGLE.getKey()) && chooseTopic.getTeacherResult() == TeacherResultState.AGREE.getKey()) {
            throw new CustomException("多选类型选题活动，导师同意后不可选择导师");
        }
        if (!checkTeacherId(params.get("teacherId").toString())) {
            throw new CustomException("导师不存在或该角色不是教师");
        }
        if (checkTeacherOverLimit(params.get("teacherId").toString(), chooseActivity.getId())) {
            throw new CustomException("超出导师选择数量限制，请选择其他导师");
        }
        chooseTopic.setTeacherId(params.get("teacherId").toString());
        chooseTopic.setTeacherResult(chooseActivity.getType() == ActivityType.SINGLE.getKey() ? TeacherResultState.AGREE.getKey() : TeacherResultState.WAITE.getKey());
        super.updateEntity(chooseTopic, currentUserId);
    }

    /**
     * 判断导师id是否存在
     *
     * @param teacherId
     * @return ture  存在 false 不存在
     */
    private boolean checkTeacherId(String teacherId) {
        if (StrUtil.isEmpty(teacherId)) {
            return false;
        }
        ChooseUser chooseUser = chooseUserService.selectById(teacherId);
        return ObjectUtil.isNotEmpty(chooseUser) && Objects.equals(chooseUser.getType(), ChooseUserType.TEACHER.getKey());
    }

    @Override
    public void deleteByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)) {
            return;
        }
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        remove(queryWrapper);
    }
}
