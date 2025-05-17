/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.activity.dao.ChooseActivityDao;
import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.entity.ChooseActivityUser;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.activity.service.ChooseActivityUserService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.enumclass.ChooseUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        return chooseActivity;
    }

    @Override
    public void queryActivityList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        Map<String, Object> user = InputObject.getLogParamsStatic();
        String currentUserId = user.get("id").toString();
        Integer type = Integer.valueOf(user.get("type").toString());

        MPJLambdaWrapper<ChooseActivity> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        if (type == ChooseUserType.STUDENT.getKey() || type == ChooseUserType.TEACHER.getKey()) {
            // 学生和教师只能查看自己参加的活动
            mpjLambdaWrapper.innerJoin(ChooseActivityUser.class, ChooseActivityUser::getActivityId, ChooseActivity::getId);
            mpjLambdaWrapper.eq(ChooseActivityUser::getUserId, currentUserId);
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            mpjLambdaWrapper.like(MybatisPlusUtil.toColumns(ChooseActivity::getName), commonPageInfo.getKeyword());
        }

        List<ChooseActivity> chooseActivityList = skyeyeBaseMapper.selectJoinList(ChooseActivity.class, mpjLambdaWrapper);
        outputObject.setBeans(chooseActivityList);
        outputObject.settotal(pages.getTotal());
    }
}
