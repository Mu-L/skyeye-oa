/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.activity.dao.ChooseActivityDao;
import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.entity.ChooseActivityUser;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.activity.service.ChooseActivityUserService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.chtopic.service.ChooseTopicService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivityServiceImpl
 * @Description: 选题活动管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "选题活动管理", groupName = "选题活动管理")
public class ChooseActivityServiceImpl extends SkyeyeBusinessServiceImpl<ChooseActivityDao, ChooseActivity> implements ChooseActivityService {

    @Autowired
    private ChooseActivityUserService chooseActivityUserService;

    @Autowired
    private ChooseTopicService chooseTopicService;

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ChooseActivity> wrapper) {
        Map<String, Object> logParams = inputObject.getLogParams();
        // 老师角色和学生角色可以查看自己创建的活动，管理员角色可以查看所有活动
        if (Integer.valueOf(logParams.get("type").toString()).equals(CommonNumConstants.NUM_TWO)
                || Integer.valueOf(logParams.get("type").toString()).equals(CommonNumConstants.NUM_THREE)){
            wrapper.eq(MybatisPlusUtil.toColumns(ChooseActivity::getCreateId), logParams.get("id"));
        }
    }

    public void validatorEntity(ChooseActivity entity) {
        super.validatorEntity(entity);
        if (DateUtil.compare(entity.getEndTime(), entity.getStartTime())) {
            throw new CustomException("结束时间不能早于开始时间");
        }
    }

    @Override
    public void deletePreExecution(ChooseActivity entity) {
        super.deletePreExecution(entity);
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        if (!currentUserId.equals(entity.getCreateId())) {
            throw new CustomException("该活动不是你创建的，你没有权限删除它");
        }
    }

    @Override
    public void deletePostpose(String id) {
        super.deletePostpose(id);
        chooseActivityUserService.deleteByActivityId(id);
    }

    @Override
    public ChooseActivity selectById(String id) {
        ChooseActivity chooseActivity = super.selectById(id);
        List<ChooseTopic> chooseTopicList = chooseTopicService.queryListByActivityId(chooseActivity.getId());
        chooseActivity.setChooseTopicList(chooseTopicList);
        return chooseActivity;
    }

    @Override
    public void queryMyJoinActivityList(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        List<ChooseActivityUser> chooseActivityUserList = chooseActivityUserService.queryListByUserId(currentUserId);
        if (CollectionUtil.isEmpty(chooseActivityUserList)) {
            return;
        }
        List<String> activityIdList = chooseActivityUserList.stream().map(ChooseActivityUser::getActivityId).distinct().collect(Collectors.toList());
        QueryWrapper<ChooseActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, activityIdList);
        List<ChooseActivity> beans = list(queryWrapper);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 校验活动是否在运行中
     *
     * @param activity
     * @return
     */
    @Override
    public boolean checkActivityIsRun(ChooseActivity activity){
        String currentTime = DateUtil.getTimeAndToString();
        return DateUtil.compare(activity.getStartTime(), currentTime) && DateUtil.compare(currentTime, activity.getEndTime());
    }

    /**
     * 校验活动是否开始过
     *
     * @param activity
     * @return
     */
    @Override
    public boolean checkActivityIsStart(ChooseActivity activity){
        String currentTime = DateUtil.getTimeAndToString();
        return DateUtil.compare(activity.getStartTime(), currentTime);
    }

}
