/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivityUserServiceImpl
 * @Description: 活动可参与的学生信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "活动可参与的学生信息管理", groupName = "活动可参与的学生信息管理")
public class ChooseActivityUserServiceImpl extends SkyeyeBusinessServiceImpl<ChooseActivityUserDao, ChooseActivityUser> implements ChooseActivityUserService {

    @Override
    public void insertActivityUser(InputObject inputObject, OutputObject outputObject) {
        ChooseActivityUserList inputObjectParams = inputObject.getParams(ChooseActivityUserList.class);
        List<ChooseActivityUser> chooseActivityUserList = inputObjectParams.getChooseActivityUserList();
        // 过滤掉重复的数据
        List<ChooseActivityUser> chooseActivityUserListDistinct = chooseActivityUserList.stream().distinct().collect(Collectors.toList());
        // 获取活动id和用户id
        List<String> activityIdList = chooseActivityUserListDistinct.stream().map(ChooseActivityUser::getActivityId).collect(Collectors.toList());
        List<String> userIdList = chooseActivityUserListDistinct.stream().map(ChooseActivityUser::getUserId).collect(Collectors.toList());
        // 查询入参涉及到的所有活动和活动下的可参与人信息
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityIdList);
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseActivityUser::getUserId), userIdList);
        List<ChooseActivityUser> list = list(queryWrapper);
        // 过滤重复的数据，并根据活动id分组
        Map<String, List<ChooseActivityUser>> activityUserListMap = list.stream().collect(Collectors.groupingBy(ChooseActivityUser::getActivityId));
        // 过滤掉数据库已经存在的数据
        List<ChooseActivityUser> insertActivityList = chooseActivityUserListDistinct.stream().filter(activityUser -> {
            if (activityUserListMap.containsKey(activityUser.getActivityId())) {
                for (ChooseActivityUser user : activityUserListMap.get(activityUser.getActivityId())) {
                    return !user.getUserId().equals(activityUser.getUserId());
                }
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
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ChooseActivityUser> wrapper) {
        super.getQueryWrapper(inputObject, wrapper);
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())){
            wrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), commonPageInfo.getObjectId());
        }
    }

    @Override
    public List<ChooseActivityUser> queryListByUserId(String userId){
        if (StrUtil.isEmpty(userId)){
            return new ArrayList<>();
        }
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getUserId), userId);
        return list(queryWrapper);
    }

    @Override
    public void deleteByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)){
            return;
        }
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityId);
        remove(queryWrapper);
    }
}
