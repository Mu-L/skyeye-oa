/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.patrol.dao.PatrolRecordDao;
import com.skyeye.patrol.entity.PatrolRecord;
import com.skyeye.patrol.service.PatrolItemService;
import com.skyeye.patrol.service.PatrolRecordService;
import com.skyeye.patrol.service.PatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PatrolRecordServiceImpl
 * @Description: 巡检记录服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检记录", groupName = "巡检记录", flowable = true)
public class PatrolRecordServiceImpl extends SkyeyeBusinessServiceImpl<PatrolRecordDao, PatrolRecord> implements PatrolRecordService {

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private PatrolItemService patrolItemService;

    @Override
    protected QueryWrapper<PatrolRecord> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatrolRecord> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolRecord::getTaskId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("planId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolRecord::getPlanId), commonPageInfo.getCustomParamsMapStr("planId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置任务信息
        patrolTaskService.setMationForMap(beans, "taskId", "taskMation");
        // 设置项目信息
        patrolItemService.setMationForMap(beans, "itemId", "itemMation");
        return beans;
    }

    @Override
    public void createPrepose(PatrolRecord entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public PatrolRecord selectById(String id) {
        PatrolRecord patrolRecord = super.selectById(id);
        if (patrolRecord == null) {
            return null;
        }
        // 设置任务信息
        patrolTaskService.setDataMation(patrolRecord, PatrolRecord::getTaskId);
        // 设置项目信息
        patrolItemService.setDataMation(patrolRecord, PatrolRecord::getItemId);
        return patrolRecord;
    }

}

