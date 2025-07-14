/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.machinprocedure.dao.MachinProcedureAcceptChildDao;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptChild;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptChildService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MachinProcedureAcceptChildServiceImpl
 * @Description: 工序验收子单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/25 17:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工序验收子单据", groupName = "工序验收", manageShow = false)
public class MachinProcedureAcceptChildServiceImpl extends SkyeyeBusinessServiceImpl<MachinProcedureAcceptChildDao, MachinProcedureAcceptChild> implements MachinProcedureAcceptChildService {

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<MachinProcedureAcceptChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptChild::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<MachinProcedureAcceptChild> selectByParentId(String parentId) {
        QueryWrapper<MachinProcedureAcceptChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptChild::getParentId), parentId);
        List<MachinProcedureAcceptChild> machinProcedureAcceptChildList = list(queryWrapper);
        return machinProcedureAcceptChildList;
    }

    @Override
    public void saveList(String parentId, List<MachinProcedureAcceptChild> machinProcedureAcceptChildList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(machinProcedureAcceptChildList)) {
            for (MachinProcedureAcceptChild machinProcedureAcceptChild : machinProcedureAcceptChildList) {
                machinProcedureAcceptChild.setParentId(parentId);
            }
            createEntity(machinProcedureAcceptChildList, StrUtil.EMPTY);
        }
    }

    @Override
    public List<MachinProcedureAcceptChild> queryListByParentId(List<String> acceptIdList) {
        if (CollectionUtil.isEmpty(acceptIdList)){
            return new ArrayList<>();
        }
        QueryWrapper<MachinProcedureAcceptChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureAcceptChild::getParentId), acceptIdList);
        return list(queryWrapper);
    }
}
