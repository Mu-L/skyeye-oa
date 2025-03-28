package com.skyeye.focus.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
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
    public void checkFocus(Video video) {
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Focus::getUserId), video.getCreateId())
                .eq(MybatisPlusUtil.toColumns(Focus::getCreateId), userId);
        video.setCheckFocus(count(queryWrapper) > 0);
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
