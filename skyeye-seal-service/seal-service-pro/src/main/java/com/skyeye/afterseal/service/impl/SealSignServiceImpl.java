/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.classenum.SealSignState;
import com.skyeye.afterseal.dao.SealSignDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.entity.SealSign;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.afterseal.service.SealSignService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealSignServiceImpl
 * @Description: 工人签到报工信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工人签到报工信息", groupName = "工单管理")
public class SealSignServiceImpl extends SkyeyeBusinessServiceImpl<SealSignDao, SealSign> implements SealSignService {

    @Autowired
    private AfterSealService afterSealService;

    @Override
    protected QueryWrapper<SealSign> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SealSign> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getObjectId), commonPageInfo.getObjectId());
        if (StrUtil.equals(commonPageInfo.getType(), "current")) {
            // 查询当前用户的签到记录
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getSignId), userId);
        }
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setNameForMap(beans, "auditUserId", "auditUserName");
        iAuthUserService.setNameForMap(beans, "signId", "signName");
        return beans;
    }

    @Override
    protected void validatorEntity(SealSign entity) {
        AfterSeal afterSeal = afterSealService.selectById(entity.getObjectId());
        // 只有"待签到"或"待完成"状态的工单才能进行签到
        if (!StrUtil.equals(afterSeal.getState(), AfterSealState.BE_SIGNED.getKey())
            && !StrUtil.equals(afterSeal.getState(), AfterSealState.BE_COMPLETED.getKey())) {
            throw new CustomException("该工单状态不允许签到。");
        }

        // 检查一个人一天只能签到一次
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String today = DateUtil.getYmdTimeAndToString(); // 获取今天的日期，格式：yyyy-MM-dd

        QueryWrapper<SealSign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getObjectId), entity.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getSignId), userId);
        queryWrapper.like(MybatisPlusUtil.toColumns(SealSign::getSignTime), today);
        queryWrapper.last("LIMIT 1");

        List<SealSign> existSignList = list(queryWrapper);
        if (existSignList != null && !existSignList.isEmpty()) {
            throw new CustomException("您今天已经签到过了，一天只能签到一次。");
        }
    }

    @Override
    protected void createPrepose(SealSign entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setSignId(userId);
        entity.setSignTime(DateUtil.getTimeAndToString());
        // 设置默认状态为"待报工"
        entity.setState(SealSignState.PENDING_WORK_REPORT.getKey());
    }

    @Override
    protected void createPostpose(SealSign entity, String userId) {
        // 修改工单信息为【待完成】
        afterSealService.updateStateById(entity.getObjectId(), AfterSealState.BE_COMPLETED.getKey());
    }

    /**
     * 报工：更新工时信息，状态改为"待审核"
     */
    public void reportWork(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> entity = inputObject.getParams();
        String id = entity.get("id").toString();
        String workHours = entity.get("workHours").toString();
        String workUnit = entity.get("workUnit").toString();

        SealSign existEntity = selectById(id);
        if (existEntity == null) {
            outputObject.setreturnMessage("签到记录不存在");
            return;
        }

        // 只有"待报工" 或者 "已驳回"状态才能报工
        if (!SealSignState.PENDING_WORK_REPORT.getKey().equals(existEntity.getState())
            && !SealSignState.REJECTED.getKey().equals(existEntity.getState())) {
            outputObject.setreturnMessage("当前状态不允许报工");
            return;
        }

        // 使用 UpdateWrapper 更新工时信息
        UpdateWrapper<SealSign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getWorkHours), workHours);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getWorkUnit), workUnit);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getState), SealSignState.PENDING_AUDIT.getKey());
        update(updateWrapper);

        // 清除缓存
        clearCache(id);
    }

    /**
     * 审核：更新审核信息，状态改为"已通过"或"已驳回"
     */
    public void auditSign(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> entity = inputObject.getParams();
        String id = entity.get("id").toString();
        String state = entity.get("state").toString();
        String auditRemark = entity.get("auditRemark").toString();

        // 只有"待审核"状态才能审核
        SealSign existEntity = selectById(id);
        if (existEntity == null) {
            outputObject.setreturnMessage("签到记录不存在");
            return;
        }

        if (!SealSignState.PENDING_AUDIT.getKey().equals(existEntity.getState())) {
            outputObject.setreturnMessage("当前状态不允许审核");
            return;
        }

        // 验证审核状态值
        if (!state.equals(SealSignState.APPROVED.getKey()) && !state.equals(SealSignState.REJECTED.getKey())) {
            outputObject.setreturnMessage("审核结果无效");
            return;
        }

        // 使用 UpdateWrapper 更新审核信息
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        UpdateWrapper<SealSign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getAuditRemark), auditRemark);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getAuditTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getAuditUserId), userId);
        update(updateWrapper);

        // 清除缓存
        clearCache(id);
    }

}
