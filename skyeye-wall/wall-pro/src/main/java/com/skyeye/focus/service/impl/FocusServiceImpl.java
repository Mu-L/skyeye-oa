package com.skyeye.focus.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.focus.dao.FocusDao;
import com.skyeye.focus.entity.Focus;
import com.skyeye.focus.service.FocusService;
import com.skyeye.user.service.UserService;
import com.skyeye.video.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName: FocusServiceImpl
 * @Description: 视频标签业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "视频用户关注管理", groupName = "视频用户关注管理")
public class FocusServiceImpl extends SkyeyeBusinessServiceImpl<FocusDao, Focus> implements FocusService {

    @Autowired
    private UserService userService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        return queryFoucusList(inputObject);
    }

    private List<Map<String, Object>> queryFoucusList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = commonPageInfo.getObjectId();
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Focus::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Focus::getCreateTime));
        List<Focus> list = list(queryWrapper);
        if(ObjectUtil.isEmpty(list)){
            return Collections.emptyList();
        }
        List<Focus> bean = list.stream().map(focus -> {
            focus.setCheckFocus(true);
            if (userService.checkCreateIdIsStudent(focus.getUserId())) {
                iAuthUserService.setDataMation(focus, Focus::getUserId);
            } else {
                userService.setDataMation(focus, Focus::getUserId);
            }
            return focus;
        }).collect(Collectors.toList());
        return JSONUtil.toList(JSONUtil.toJsonStr(bean), null);
    }

    @Override
    public boolean checkFocus(String createId) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if(StrUtil.isEmpty(userToken)){
            return false;
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Focus::getUserId),createId )
                .eq(MybatisPlusUtil.toColumns(Focus::getCreateId), userId);
        return count(queryWrapper) > 0;
    }

    @Override
    public Map<String,Boolean> checkFocus(List<String> videoCreateIds) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        String userId;
        if(StrUtil.isEmpty(userToken)){
            userId = StrUtil.EMPTY;
        }else {
            userId = InputObject.getLogParamsStatic().get("id").toString();
        }
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Focus::getUserId), videoCreateIds)
                .eq(MybatisPlusUtil.toColumns(Focus::getCreateId), userId);
        // 关注用户的记录
        List<Focus> focusList = list(queryWrapper);
        List<String> focusCreateIds = focusList.stream().map(Focus::getUserId).collect(Collectors.toList());
        Map<String,Boolean> map = new HashMap<>();
        for(String createId : videoCreateIds){
            map.put(createId,focusCreateIds.contains(createId));
        }
        return map;
    }

    @Override
    public void deleteFocusByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = params.get("userId").toString();
        String createId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Focus::getUserId), userId)
                .eq(MybatisPlusUtil.toColumns(Focus::getCreateId), createId);
        remove(queryWrapper);
    }
}
