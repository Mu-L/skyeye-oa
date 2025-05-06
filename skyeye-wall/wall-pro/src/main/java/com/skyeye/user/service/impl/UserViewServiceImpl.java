package com.skyeye.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.user.dao.UserViewDao;
import com.skyeye.user.entity.User;
import com.skyeye.user.entity.UserView;
import com.skyeye.user.service.UserService;
import com.skyeye.user.service.UserViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: UserViewServiceImpl
 * @Description: 用户访客记录服务层实现类
 * @author: skyeye云系列--lqy
 * @date: 2025/5/5 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户访客记录管理", groupName = "用户访客记录管理")
public class UserViewServiceImpl extends SkyeyeBusinessServiceImpl<UserViewDao, UserView> implements UserViewService {

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private UserService userService;

    @Override
    public void queryUserVisitors(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = commonPageInfo.getHolderId();
        if(StrUtil.isEmpty(userId)){
            throw new CustomException("用户id不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<UserView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserView::getUserId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(UserView::getViewTime))
                .orderByDesc(MybatisPlusUtil.toColumns(UserView::getViewCount));
        List<UserView> userViews = list(queryWrapper);
        if(CollectionUtil.isNotEmpty(userViews)){
            return;
        }
        // 获取访问者id
        for (UserView userView : userViews) {
            User user = userService.selectById(userView.getVisitorUserId());
            if(StrUtil.isEmpty(user.getId())){
                iAuthUserService.setDataMation(userView,UserView::getVisitorUserId);
            }else {
                userService.setDataMation(userView,UserView::getVisitorUserId);
            }
        }
        outputObject.setBeans(userViews);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void deleteAllUserVisitors(InputObject inputObject, OutputObject outputObject) {
        String userId=  InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<UserView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserView::getUserId), userId);
        remove(queryWrapper);
    }

    @Override
    protected void deletePreExecution(UserView entity) {
        super.deletePreExecution(entity);
        String userId=  InputObject.getLogParamsStatic().get("id").toString();
        if(!userId.equals(entity.getUserId())){
            throw new CustomException("无权限");
        }
    }
}
