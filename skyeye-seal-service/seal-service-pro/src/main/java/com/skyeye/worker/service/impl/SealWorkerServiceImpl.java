/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worker.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.worker.dao.SealWorkerDao;
import com.skyeye.worker.entity.SealWorker;
import com.skyeye.worker.service.SealWorkerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SealWorkerServiceImpl
 * @Description: 工人信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工人管理", groupName = "工人管理")
public class SealWorkerServiceImpl extends SkyeyeBusinessServiceImpl<SealWorkerDao, SealWorker> implements SealWorkerService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "userId", "userMation");
        return beans;
    }

    @Override
    public void validatorEntity(SealWorker entity) {
        super.validatorEntity(entity);
        QueryWrapper<SealWorker> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealWorker::getUserId), entity.getUserId());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        SealWorker one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("该账号已为工人帐号.");
        }
    }

    @Override
    public SealWorker selectById(String id) {
        SealWorker sealWorker = super.selectById(id);
        iAuthUserService.setDataMation(sealWorker, SealWorker::getUserId);
        if (CollectionUtil.isNotEmpty(sealWorker.getUserMation())) {
            sealWorker.setName(sealWorker.getUserMation().get("name").toString());
        }
        return sealWorker;
    }

    @Override
    public void queryAllSealWorkerList(InputObject inputObject, OutputObject outputObject) {
        List<SealWorker> sealWorkerList = list();
        List<String> userIds = sealWorkerList.stream().map(SealWorker::getUserId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        iAuthUserService.setDataMation(sealWorkerList, SealWorker::getUserId);
        outputObject.setBeans(sealWorkerList);
        outputObject.settotal(sealWorkerList.size());
    }

    @Override
    public SealWorker selectByUserId(String userId) {
        QueryWrapper<SealWorker> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealWorker::getUserId), userId);
        SealWorker sealWorker = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(sealWorker)) {
            iAuthUserService.setDataMation(sealWorker, SealWorker::getUserId);
            if (CollectionUtil.isNotEmpty(sealWorker.getUserMation())) {
                sealWorker.setName(sealWorker.getUserMation().get("name").toString());
            }
        }
        return sealWorker;
    }

}
