package com.skyeye.focus.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.focus.dao.FocusDao;
import com.skyeye.focus.entity.Focus;
import com.skyeye.focus.service.FocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * @ClassName: FocusServiceImpl
 * @Description: 视频标签业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "视频关注管理", groupName = "视频关注管理")
public class FocusServiceImpl extends SkyeyeBusinessServiceImpl<FocusDao, Focus> implements FocusService {

    @Autowired
    private FocusService focusService;


    /**
     * 添加视频标签
     */
    @Override
    public void createPrepose(Focus entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setVideoId(userId);
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        //对比数据库里面的数据
        queryWrapper.eq(MybatisPlusUtil.toColumns(Focus::getVideoId),entity.getVideoId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Focus::getCreateId),entity.getCreateId());
        //获取唯一的数据
        Focus one = getOne(queryWrapper);
        //对比
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("已关注");
        }

    }



}
