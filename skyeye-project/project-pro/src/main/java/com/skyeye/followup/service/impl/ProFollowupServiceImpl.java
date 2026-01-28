package com.skyeye.followup.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.followup.classenum.FollowupStateEnum;
import com.skyeye.followup.dao.ProFollowupDao;
import com.skyeye.followup.entity.ProFollowup;
import com.skyeye.followup.service.ProFollowupService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProFollowupServiceImpl
 * @Description: 项目跟进Service实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目跟进", groupName = "项目管理")
public class ProFollowupServiceImpl extends SkyeyeBusinessServiceImpl<ProFollowupDao, ProFollowup> implements ProFollowupService {

    @Override
    protected QueryWrapper<ProFollowup> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProFollowup> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("projectId"))) {
            // 根据项目ID过滤
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProFollowup::getProjectId), commonPageInfo.getCustomParamsMapStr("projectId"));
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(ProFollowup entity) {
        // 验证跟进标题唯一性（同一个项目内）
        QueryWrapper<ProFollowup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProFollowup::getName), entity.getName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProFollowup::getProjectId), entity.getProjectId());
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ProFollowup checkFollowup = getOne(queryWrapper, false);
        if (checkFollowup != null) {
            throw new CustomException("同一个项目内的跟进标题不能重复");
        }
    }

    @Override
    public void createPrepose(ProFollowup entity) {
        // 生成跟进编号
        Map<String, Object> business = new HashMap<>();
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);

        // 设置默认状态
        entity.setState(FollowupStateEnum.PENDING.getKey());
    }

    @Override
    public void updateFollowupState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String state = map.get("state").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        ProFollowup followup = selectById(id);
        if (followup == null) {
            outputObject.setreturnMessage("跟进记录不存在");
            return;
        }

        // 使用 UpdateWrapper 直接更新状态字段
        UpdateWrapper<ProFollowup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(ProFollowup::getState), state)
            .set(MybatisPlusUtil.toColumns(ProFollowup::getLastUpdateId), userId)
            .set(MybatisPlusUtil.toColumns(ProFollowup::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryFollowupStatistics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String projectId = map.get("projectId").toString();

        // 查询项目的所有跟进记录
        QueryWrapper<ProFollowup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProFollowup::getProjectId), projectId);
        List<ProFollowup> followupList = list(queryWrapper);

        Map<String, Object> statistics = new HashMap<>();

        // 总跟进次数
        statistics.put("totalCount", followupList.size());

        // 各状态统计
        Map<String, Long> stateCount = followupList.stream()
            .collect(Collectors.groupingBy(ProFollowup::getState, Collectors.counting()));
        statistics.put("stateStatistics", stateCount);

        // 最后跟进时间
        if (CollectionUtil.isNotEmpty(followupList)) {
            ProFollowup latestFollowup = followupList.stream()
                .max(Comparator.comparing(OperatorUserInfo::getCreateTime))
                .orElse(null);
            statistics.put("lastFollowupTime", latestFollowup.getCreateTime());
        }

        // 跟进人统计
        Map<String, Long> personCount = followupList.stream()
            .collect(Collectors.groupingBy(ProFollowup::getFollowupPersonId, Collectors.counting()));
        statistics.put("personStatistics", personCount);

        outputObject.setBean(statistics);
    }

}