/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reward.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.reward.dao.RewardPunishDao;
import com.skyeye.reward.entity.RewardPunish;
import com.skyeye.reward.service.RewardPunishService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: RewardPunishServiceImpl
 * @Description: 员工奖惩管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:41
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工奖惩信息", groupName = "员工奖惩信息", teamAuth = true)
public class RewardPunishServiceImpl extends SkyeyeBusinessServiceImpl<RewardPunishDao, RewardPunish> implements RewardPunishService {

    @Override
    protected void createPrepose(RewardPunish entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        entity.setOddNumber(oddNumber);
        entity.setIsAccounted(WhetherEnum.DISABLE_USING.getKey());
    }

    @Override
    protected QueryWrapper<RewardPunish> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<RewardPunish> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(RewardPunish::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<String> staffIds = beans.stream().map(bean -> bean.get("objectId").toString()).collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String objectId = bean.get("objectId").toString();
            bean.put("objectMation", staffMap.get(objectId));
        });
        return beans;
    }

    @Override
    public RewardPunish selectById(String id) {
        RewardPunish rewardPunish = super.selectById(id);
        iSysDictDataService.setDataMation(rewardPunish, RewardPunish::getTypeId);
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Arrays.asList(rewardPunish.getObjectId()));
        rewardPunish.setObjectMation(staffMap.get(rewardPunish.getObjectId()));
        return rewardPunish;
    }

    @Override
    public List<RewardPunish> queryUnAccountedByStaffIdAndMonth(String staffId, String accountMonth) {
        QueryWrapper<RewardPunish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RewardPunish::getObjectId), staffId);
        // 查询未计入薪资的记录：isAccounted 为未计入或为空
        // 不管 accountMonth 是否设置，只要是未计入薪资的记录，都会计入当前计算的月份
        queryWrapper.and(wrapper -> wrapper
            .eq(MybatisPlusUtil.toColumns(RewardPunish::getIsAccounted), WhetherEnum.DISABLE_USING.getKey())
            .or()
            .isNull(MybatisPlusUtil.toColumns(RewardPunish::getIsAccounted)));
        return list(queryWrapper);
    }

    @Override
    public void markAsAccountedBatch(List<String> rewardPunishIds, String accountMonth) {
        if (rewardPunishIds == null || rewardPunishIds.isEmpty()) {
            return;
        }
        UpdateWrapper<RewardPunish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, rewardPunishIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(RewardPunish::getIsAccounted), WhetherEnum.ENABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(RewardPunish::getAccountMonth), accountMonth);
        update(updateWrapper);
        clearCache(rewardPunishIds);
    }

}
