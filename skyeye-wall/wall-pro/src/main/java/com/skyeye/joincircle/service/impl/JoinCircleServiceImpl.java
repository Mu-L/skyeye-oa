/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.joincircle.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.circle.service.CircleService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.joincircle.dao.JoinCircleDao;
import com.skyeye.joincircle.entity.JoinCircle;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: JoinCircleServiceImpl
 * @Description: 加入圈子服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加入圈子管理", groupName = "加入圈子管理")
public class JoinCircleServiceImpl extends SkyeyeBusinessServiceImpl<JoinCircleDao, JoinCircle> implements JoinCircleService {

    @Autowired
    private CircleService circleService;

    @Autowired
    private UserService userService;

    @Override
    public String createEntity(JoinCircle joinCircle, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), joinCircle.getCircleId())
            .eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId);
        long count = count(queryWrapper);
        if (count > 0) {
            return StrUtil.EMPTY;
        }
        return super.createEntity(joinCircle, userId);
    }

    @Override
    public void validatorEntity(JoinCircle joinCircle) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        joinCircle.setCreateId(userId);
        joinCircle.setCreateTime(DateUtil.getTimeAndToString());
    }

    @Override
    public void createPostpose(JoinCircle joinCircle, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), joinCircle.getCircleId());
        long count = count(queryWrapper);
        circleService.updateJoinNum(joinCircle.getCircleId(), (int) count);
    }

    @Override
    public void deletePreExecution(JoinCircle joinCircle) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(joinCircle.getCreateId())) {
            throw new CustomException("无权限!");
        }
    }

    @Override
    public void deletePostpose(JoinCircle joinCircle) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), joinCircle.getCircleId());
        long count = count(queryWrapper);
        circleService.updateJoinNum(joinCircle.getCircleId(), (int) count);
    }

    @Override
    public JoinCircle selectByCircleId(String circleId, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId);
        JoinCircle joinCircle = getOne(queryWrapper);
        return ObjectUtil.isEmpty(joinCircle) ? new JoinCircle(): joinCircle;
    }

    @Override
    public void deleteJoinByCircleId(String circleId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        remove(queryWrapper);
    }

    @Override
    public void queryJoinUserByCircleId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String circleId = commonPageInfo.getObjectId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(JoinCircle::getCreateTime));
        List<JoinCircle> joinCircleList = list(queryWrapper);
        userService.setDataMation(joinCircleList, JoinCircle::getCreateId);
        outputObject.setBeans(joinCircleList);
        outputObject.settotal(page.getTotal());
    }
}
