/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.activity.dao.ChooseActivityUserDao;
import com.skyeye.activity.entity.ChooseActivityUser;
import com.skyeye.activity.entity.ChooseActivityUserList;
import com.skyeye.activity.service.ChooseActivityUserService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.dao.ChooseUserDao;
import com.skyeye.user.entity.ChooseUser;
import com.skyeye.user.enumclass.ChooseUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivityUserServiceImpl
 * @Description: 活动可参与的用户信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "活动可参与的用户信息管理", groupName = "活动可参与的用户信息管理")
public class ChooseActivityUserServiceImpl extends SkyeyeBusinessServiceImpl<ChooseActivityUserDao, ChooseActivityUser> implements ChooseActivityUserService {

    @Autowired
    private ChooseUserDao chooseUserDao;

    @Override
    public void insertActivityUser(InputObject inputObject, OutputObject outputObject) {
        ChooseActivityUserList inputObjectParams = inputObject.getParams(ChooseActivityUserList.class);
        List<ChooseActivityUser> chooseActivityUserList = inputObjectParams.getChooseActivityUserList();
        // 过滤掉重复的数据
        List<ChooseActivityUser> chooseActivityUserListDistinct = chooseActivityUserList.stream().distinct().collect(Collectors.toList());
        // 获取活动id和用户id
        List<String> activityIdList = chooseActivityUserListDistinct.stream().map(ChooseActivityUser::getActivityId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(activityIdList)) {
            throw new CustomException("活动id不能为空");
        }
        List<String> userIdList = chooseActivityUserListDistinct.stream().map(ChooseActivityUser::getUserId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIdList)) {
            throw new CustomException("用户id不能为空");
        }
        // 查询入参涉及到的活动和活动下的可参与人信息是否已经存在
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityIdList.get(0));
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseActivityUser::getUserId), userIdList);
        List<ChooseActivityUser> list = list(queryWrapper);
        List<String> inActivityUserIdList = list.stream().map(ChooseActivityUser::getUserId).distinct().collect(Collectors.toList());
        // 过滤掉数据库已经存在的数据
        List<ChooseActivityUser> insertActivityList = chooseActivityUserListDistinct.stream().filter(activityUser -> {
            if (inActivityUserIdList.contains(activityUser.getUserId())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(insertActivityList)) {
            return;
        }
        super.createEntity(insertActivityList, inputObject.getLogParams().get("id").toString());
        outputObject.setBeans(insertActivityList);
        outputObject.settotal(insertActivityList.size());
    }

    @Override
    public void deleteByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)) {
            return;
        }
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityId);
        remove(queryWrapper);
    }

    @Override
    public void queryActivityUserList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        Map<String, Object> user = InputObject.getLogParamsStatic();
        String currentUserId = user.get("id").toString();
        Integer type = Integer.valueOf(user.get("type").toString());
        if (type == ChooseUserType.STUDENT.getKey()) {
            throw new RuntimeException("学生用户不能查看活动信息");
        }

        MPJLambdaWrapper<ChooseUser> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper.innerJoin(ChooseActivityUser.class, ChooseActivityUser::getUserId, ChooseUser::getId);
        mpjLambdaWrapper.eq(ChooseActivityUser::getActivityId, commonPageInfo.getObjectId());
        mpjLambdaWrapper.eq(ChooseUser::getType, commonPageInfo.getType());

        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            mpjLambdaWrapper.like(MybatisPlusUtil.toColumns(ChooseUser::getStuNo), commonPageInfo.getKeyword());
        }

        List<ChooseUser> chooseUserList = chooseUserDao.selectJoinList(ChooseUser.class, mpjLambdaWrapper);
        outputObject.setBeans(chooseUserList);
        outputObject.settotal(pages.getTotal());
    }
}
