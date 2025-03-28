/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.joincircle.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.circle.service.CircleService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.joincircle.dao.JoinCircleDao;
import com.skyeye.joincircle.entity.JoinCircle;
import com.skyeye.joincircle.entity.JoinLimit;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.joincircle.service.JoinLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private JoinLimitService joinLimitService;

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
    public void createPrepose(JoinCircle entity) {
        super.createPrepose(entity);
        boolean isAllow = joinLimitService.checkIsAllowJoin(entity.getCircleId(), entity.getCreateId());
        if (!isAllow) {
            throw new CustomException("加入圈子失败，您已达到加入次数限制");
        }
    }

    @Override
    public void createPostpose(JoinCircle joinCircle, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), joinCircle.getCircleId());
        long count = count(queryWrapper);
        circleService.updateJoinNum(joinCircle.getCircleId(), (int) count);
    }

    @Override
    public void deletePostpose(JoinCircle joinCircle) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), joinCircle.getCircleId());
        long count = count(queryWrapper);
        circleService.updateJoinNum(joinCircle.getCircleId(), (int) count);
        JoinLimit joinLimit = new JoinLimit();
        joinLimit.setObjectId(joinCircle.getCircleId());
        joinLimit.setUserId(userId);
        joinLimitService.createEntity(joinLimit, null);
    }

    @Override
    public JoinCircle selectByCircleId(String circleId, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId);
        JoinCircle joinCircle = getOne(queryWrapper);
        return ObjectUtil.isEmpty(joinCircle) ? new JoinCircle() : joinCircle;
    }

    @Override
    public void deleteJoinByCircleId(String circleId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        remove(queryWrapper);
    }

    /**
     * 检验当前登录人是否加入改圈子
     */
    @Override
    public Boolean checkIsJoinCircle(String circleId, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId);
        return count(queryWrapper) > 0;
    }

    @Override
    public List<JoinCircle> queryMyJoinCircle(String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId)
                .orderByDesc(MybatisPlusUtil.toColumns(JoinCircle::getCreateTime));
        return list(queryWrapper);
    }

    @Override
    public void deleteJoinCircleByCircleId(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String circleId = inputObject.getParams().get("circleId").toString();
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId);
        JoinCircle one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("无权限");
        }
        deleteById(one.getId());
    }

    /**
     * 检测当前登录人是否加入圈子
     */
    @Override
    public Map<String, Boolean> checkIsJoinCircle(List<String> circleIds, String userId) {
        QueryWrapper<JoinCircle> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(JoinCircle::getCircleId), circleIds)
                .eq(MybatisPlusUtil.toColumns(JoinCircle::getCreateId), userId);
        List<JoinCircle> joinCircles = list(queryWrapper); // 加入的圈子记录
        List<String> joinCircleIds = joinCircles.stream().map(JoinCircle::getCircleId).collect(Collectors.toList());
        Map<String, Boolean> map = new HashMap<>();
        for (String circleId : circleIds) {
            map.put(circleId, joinCircleIds.contains(circleId));
        }
        // 当前登录人是否加入圈子 是true 否false
        return map;
    }
}
