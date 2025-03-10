/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.circle.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.circle.dao.CircleDao;
import com.skyeye.circle.entity.Circle;
import com.skyeye.circle.service.CircleService;
import com.skyeye.circleview.service.CircleViewService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.joincircle.entity.JoinCircle;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.post.service.PostService;
import com.skyeye.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CircleServiceImpl
 * @Description: 圈子服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "圈子管理", groupName = "圈子管理")
public class CircleServiceImpl extends SkyeyeBusinessServiceImpl<CircleDao, Circle> implements CircleService {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private CircleViewService circleViewService;

    @Autowired
    private JoinCircleService joinCircleService;

    @Override
    public void validatorEntity(Circle circle) {
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Circle::getTitle), circle.getTitle());
        if (ObjectUtil.isNotEmpty(getOne(queryWrapper))) {
            throw new CustomException("标题重复");
        }
    }

    @Override
    public void createPrepose(Circle circle) {
        circle.setViewNum(CommonNumConstants.NUM_ZERO);
        circle.setNum(CommonNumConstants.NUM_ZERO);
    }

    @Override
    public void deletePreExecution(Circle circle) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(circle.getCreateId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void deletePostpose(String id) {
        postService.deleteByCircleId(id);
        materialService.deleteByCircleId(id);
        circleViewService.deleteCircleViewByCircleId(id);
        joinCircleService.deleteJoinByCircleId(id);
    }

    @Override
    public Circle selectById(String id) {
        Circle circle = super.selectById(id);
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        circle.setIsJoin(joinCircleService.checkIsJoinCircle(userId, id));
        userService.setDataMation(circle, Circle::getCreateId);
        return circle;
    }

    @Override
    public void updateViewNum(String circleId, Integer count) {
        UpdateWrapper<Circle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, circleId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Circle::getViewNum), count);
        update(updateWrapper);
    }

    @Override
    public void updateJoinNum(String circleId, Integer joinNum) {
        UpdateWrapper<Circle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, circleId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Circle::getNum), joinNum);
        update(updateWrapper);
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = queryCircleList(inputObject);
        userService.setMationForMap(beans, "createId","createMation");
        return beans;
    }

    private List<Map<String,Object>> queryCircleList(InputObject inputObject){
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Circle::getCreateTime));
        List<Circle> bean = new ArrayList<>();
        if(params.containsKey("objectId") && StrUtil.isNotEmpty(params.get("objectId").toString())){
            String objectId = params.get("objectId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Circle::getCreateId), objectId);
            bean = list(queryWrapper);
            // 获取我加入的圈子记录
            List<JoinCircle> joinCircles = joinCircleService.queryMyJoinCircle(objectId);
            List<String> circleIds = joinCircles.stream().map(JoinCircle::getCircleId).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(circleIds)){
                for (String circleId : circleIds) {
                    Circle circle = selectById(circleId);
                    bean.add(circle);
                }
            }
        }
        if(CollectionUtil.isEmpty(bean)){
            bean = queryAllData();
        }
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(bean), null);
        return beans;
    }
}
