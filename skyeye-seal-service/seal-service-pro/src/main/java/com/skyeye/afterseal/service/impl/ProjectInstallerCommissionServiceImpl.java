/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.afterseal.classenum.SealSignState;
import com.skyeye.afterseal.classenum.SealSignWorkUnit;
import com.skyeye.afterseal.dao.ProjectInstallerCommissionDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.entity.ProjectInstallerCommission;
import com.skyeye.afterseal.entity.SealSign;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.afterseal.service.ProjectInstallerCommissionService;
import com.skyeye.afterseal.service.SealSignService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ProjectInstallerCommissionServiceImpl
 * @Description: 安装员提成服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24 12:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "安装员提成", groupName = "工单管理")
public class ProjectInstallerCommissionServiceImpl extends SkyeyeBusinessServiceImpl<ProjectInstallerCommissionDao, ProjectInstallerCommission> implements ProjectInstallerCommissionService {

    @Autowired
    private SealSignService sealSignService;

    @Autowired
    private AfterSealService afterSealService;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setNameForMap(beans, "installerId", "installerName");
        return beans;
    }

    /**
     * 计算提成（报工审核通过后自动调用）
     * 根据工单ID，查询该工单下所有已审核通过的工时记录，按安装员汇总，计算提成
     */
    @Override
    public void calculateCommission(String dispatchId) {
        if (StrUtil.isEmpty(dispatchId)) {
            throw new CustomException("工单ID不能为空");
        }

        // 1. 从工单表获取安装费用（总提成）
        AfterSeal dispatch = afterSealService.selectById(dispatchId);
        if (dispatch == null) {
            throw new CustomException("工单不存在");
        }
        String totalCommissionStr = dispatch.getInstallFee();
        if (StrUtil.isEmpty(totalCommissionStr) || "0".equals(totalCommissionStr)) {
            throw new CustomException("工单未设置安装费用，无法计算提成");
        }

        // 2. 查询该工单下所有已审核通过的工时记录（SealSign）
        QueryWrapper<SealSign> worktimeQueryWrapper = new QueryWrapper<>();
        worktimeQueryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getObjectId), dispatchId);
        worktimeQueryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getState), SealSignState.APPROVED.getKey());
        List<SealSign> worktimeList = sealSignService.list(worktimeQueryWrapper);

        if (CollectionUtil.isEmpty(worktimeList)) {
            return;
        }

        // 3. 按安装员汇总工时（同一人多次报工累计）
        // 注意：这里使用 signId 作为安装员ID
        Map<String, String> installerWorktimeMap = worktimeList.stream()
            .collect(Collectors.groupingBy(SealSign::getSignId, Collectors.reducing("0", sign -> {
                        // 将工时转换为天数（根据 workUnit 转换）
                        String workHours = sign.getWorkHours();
                        String workUnit = sign.getWorkUnit();
                        if (StrUtil.isEmpty(workHours)) {
                            return "0";
                        }
                        // 根据单位转换为天数
                        if (SealSignWorkUnit.HOUR.getKey().equals(workUnit)) {
                            // 小时转换为天：除以8
                            return CalculationUtil.divide(workHours, "8", CommonNumConstants.NUM_FOUR);
                        } else if (SealSignWorkUnit.DAY.getKey().equals(workUnit)) {
                            // 天：直接使用
                            return workHours;
                        } else {
                            // 其他单位，默认按天处理
                            return workHours;
                        }
                    },
                    (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b)
                )
            ));

        // 4. 计算总工时
        String totalWorktime = installerWorktimeMap.values().stream()
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

        if (CalculationUtil.compareTo(totalWorktime, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) <= 0) {
            return;
        }

        // 5. 计算每个人的提成并保存/更新
        installerWorktimeMap.forEach((installerId, worktime) -> {
            // 计算提成比例（保留4位小数）
            String commissionRate = CalculationUtil.divide(worktime, totalWorktime, CommonNumConstants.NUM_FOUR);
            // 计算个人提成（保留2位小数）
            String installerCommission = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, totalCommissionStr, commissionRate);

            // 查询是否已存在提成记录
            ProjectInstallerCommission existCommission = getOne(new QueryWrapper<ProjectInstallerCommission>()
                .eq(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getDispatchId), dispatchId)
                .eq(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getInstallerId), installerId));

            if (existCommission != null) {
                // 更新提成记录
                UpdateWrapper<ProjectInstallerCommission> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, existCommission.getId());
                updateWrapper.set(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getInstallerCommission), installerCommission);
                updateWrapper.set(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getWorkHours), worktime);
                updateWrapper.set(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getTotalWorkHours), totalWorktime);
                updateWrapper.set(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getCommissionRate), commissionRate);
                update(updateWrapper);
            } else {
                // 创建提成记录
                ProjectInstallerCommission commission = new ProjectInstallerCommission();
                commission.setProjectId(dispatch.getProjectId());
                commission.setDispatchId(dispatchId);
                commission.setInstallerId(installerId);
                commission.setInstallerCommission(installerCommission);
                commission.setWorkHours(worktime);
                commission.setTotalWorkHours(totalWorktime);
                commission.setCommissionRate(commissionRate);
                String userId = InputObject.getLogParamsStatic().get("id").toString();
                createEntity(commission, userId);
            }
        });
    }

    /**
     * 获取提成统计数据
     */
    @Override
    public void queryCommissionStatistics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String projectId = params.get("projectId").toString();

        // 查询该项目下所有提成记录
        QueryWrapper<ProjectInstallerCommission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getProjectId), projectId);
        List<ProjectInstallerCommission> commissionList = list(queryWrapper);

        if (CollectionUtil.isEmpty(commissionList)) {
            outputObject.setBean(new HashMap<>());
            return;
        }

        // 按安装员分组统计
        Map<String, List<ProjectInstallerCommission>> installerGroupMap = commissionList.stream()
            .collect(Collectors.groupingBy(ProjectInstallerCommission::getInstallerId));

        // 按工单（项目）分组统计
        Map<String, List<ProjectInstallerCommission>> dispatchGroupMap = commissionList.stream()
            .collect(Collectors.groupingBy(ProjectInstallerCommission::getDispatchId));

        // 获取所有安装员信息
        Set<String> installerIds = installerGroupMap.keySet();
        Map<String, Map<String, Object>> installerInfoMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(installerIds)) {
            installerInfoMap = iAuthUserService.queryUserNameList(new ArrayList<>(installerIds));
        }

        // 计算汇总数据
        String totalCommission = commissionList.stream()
            .map(ProjectInstallerCommission::getInstallerCommission)
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));

        // 计算总工时
        String totalWorkHours = commissionList.stream()
            .map(ProjectInstallerCommission::getTotalWorkHours)
            .distinct()
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

        // 计算平均工时
        String avgWorkHours = CalculationUtil.compareTo(totalWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0
            ? CalculationUtil.divide(totalCommission, totalWorkHours, CommonNumConstants.NUM_TWO)
            : "0";

        // 计算安装员汇总数据
        List<Map<String, Object>> installerSummaryList = new ArrayList<>();
        for (Map.Entry<String, List<ProjectInstallerCommission>> entry : installerGroupMap.entrySet()) {
            String installerId = entry.getKey();
            List<ProjectInstallerCommission> installerCommissions = entry.getValue();

            String installerTotalCommission = installerCommissions.stream()
                .map(ProjectInstallerCommission::getInstallerCommission)
                .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));

            String installerTotalWorkHours = installerCommissions.stream()
                .map(ProjectInstallerCommission::getWorkHours)
                .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

            String avgDailyCommission = CalculationUtil.compareTo(installerTotalWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0
                ? CalculationUtil.divide(installerTotalCommission, installerTotalWorkHours, CommonNumConstants.NUM_TWO)
                : "0";

            Map<String, Object> installerSummary = new HashMap<>();
            installerSummary.put("installerId", installerId);
            installerSummary.put("installerName", installerInfoMap.getOrDefault(installerId, new HashMap<>()).getOrDefault("name", "未知"));
            installerSummary.put("totalCommission", installerTotalCommission);
            installerSummary.put("totalWorkHours", installerTotalWorkHours);
            installerSummary.put("avgDailyCommission", avgDailyCommission);
            installerSummaryList.add(installerSummary);
        }

        // 按提成排序计算排名
        installerSummaryList.sort((a, b) -> {
            String commissionA = a.get("totalCommission").toString();
            String commissionB = b.get("totalCommission").toString();
            return CalculationUtil.compareTo(commissionB, commissionA, CommonNumConstants.NUM_TWO, RoundingMode.UP);
        });

        // 生成项目明细数据
        List<Map<String, Object>> projectDetailList = new ArrayList<>();
        for (Map.Entry<String, List<ProjectInstallerCommission>> entry : dispatchGroupMap.entrySet()) {
            String dispatchId = entry.getKey();
            List<ProjectInstallerCommission> dispatchCommissions = entry.getValue();

            // 获取工单信息
            AfterSeal dispatch = afterSealService.selectById(dispatchId);
            if (dispatch == null) {
                continue;
            }

            String dispatchTotalPrice = dispatch.getInstallFee() != null ? dispatch.getInstallFee() : "0";
            String dispatchTotalWorkHours = dispatchCommissions.stream()
                .map(ProjectInstallerCommission::getTotalWorkHours)
                .findFirst()
                .orElse("0");

            String dispatchAvgWorkHours = CalculationUtil.compareTo(dispatchTotalWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0
                ? CalculationUtil.divide(dispatchTotalPrice, dispatchTotalWorkHours, CommonNumConstants.NUM_TWO)
                : "0";

            String status = "未完成";

            Map<String, Object> projectDetail = new HashMap<>();
            projectDetail.put("dispatchId", dispatchId);
            projectDetail.put("location", dispatch.getOddNumber() != null ? dispatch.getOddNumber() : "-");
            projectDetail.put("totalPrice", dispatchTotalPrice);
            projectDetail.put("totalWorkHours", dispatchTotalWorkHours);
            projectDetail.put("avgWorkHours", dispatchAvgWorkHours);
            projectDetail.put("status", status);

            // 按安装员组织数据
            Map<String, Map<String, String>> installerDataMap = new HashMap<>();
            for (ProjectInstallerCommission commission : dispatchCommissions) {
                String installerId = commission.getInstallerId();
                Map<String, String> installerData = new HashMap<>();
                installerData.put("commission", commission.getInstallerCommission());
                installerData.put("workHours", commission.getWorkHours());
                installerDataMap.put(installerId, installerData);
            }
            projectDetail.put("installerDataMap", installerDataMap);
            projectDetailList.add(projectDetail);
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("totalCommission", totalCommission);
        summaryMap.put("totalWorkHours", totalWorkHours);
        summaryMap.put("avgWorkHours", avgWorkHours);
        result.put("summary", summaryMap);
        result.put("installerSummaryList", installerSummaryList);
        result.put("totalCommissionRanking", installerSummaryList);
        result.put("projectDetailList", projectDetailList);

        outputObject.setBean(result);
    }

    /**
     * 根据工单ID查询提成统计
     */
    @Override
    public void queryCommissionStatisticsByDispatchId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String dispatchId = params.get("dispatchId").toString();

        if (StrUtil.isEmpty(dispatchId)) {
            throw new CustomException("工单ID不能为空");
        }

        // 查询该工单下所有提成记录
        QueryWrapper<ProjectInstallerCommission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getDispatchId), dispatchId);
        List<ProjectInstallerCommission> commissionList = list(queryWrapper);

        if (CollectionUtil.isEmpty(commissionList)) {
            outputObject.setBean(new HashMap<>());
            return;
        }

        // 获取工单信息
        AfterSeal dispatch = afterSealService.selectById(dispatchId);
        if (dispatch == null) {
            throw new CustomException("工单不存在");
        }

        // 获取所有安装员ID
        Set<String> installerIds = commissionList.stream()
            .map(ProjectInstallerCommission::getInstallerId)
            .collect(Collectors.toSet());

        // 获取安装员信息
        Map<String, Map<String, Object>> installerInfoMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(installerIds)) {
            installerInfoMap = iAuthUserService.queryUserNameList(new ArrayList<>(installerIds));
        }

        // 计算该工单的总提成
        String totalCommission = commissionList.stream()
            .map(ProjectInstallerCommission::getInstallerCommission)
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));

        // 获取该工单的总工时（所有提成记录的总工时应该相同，取第一个）
        String totalWorkHours = commissionList.stream()
            .map(ProjectInstallerCommission::getTotalWorkHours)
            .findFirst()
            .orElse("0");

        // 获取工单的安装费用
        String totalPrice = dispatch.getInstallFee() != null ? dispatch.getInstallFee() : "0";

        // 计算平均工时（总工价/总工时）
        String avgWorkHours = CalculationUtil.compareTo(totalWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0
            ? CalculationUtil.divide(totalPrice, totalWorkHours, CommonNumConstants.NUM_TWO)
            : "0";

        // 判断完成状态
        String status = "未完成";

        // 该工单下各安装员的提成明细
        List<Map<String, Object>> installerDetailList = new ArrayList<>();
        for (ProjectInstallerCommission commission : commissionList) {
            String installerId = commission.getInstallerId();
            Map<String, Object> installerDetail = new HashMap<>();
            installerDetail.put("installerId", installerId);
            installerDetail.put("installerName", installerInfoMap.getOrDefault(installerId, new HashMap<>()).getOrDefault("name", "未知"));
            installerDetail.put("commission", commission.getInstallerCommission());
            installerDetail.put("workHours", commission.getWorkHours());
            installerDetail.put("totalWorkHours", commission.getTotalWorkHours());
            installerDetail.put("commissionRate", commission.getCommissionRate());
            installerDetailList.add(installerDetail);
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("dispatchId", dispatchId);
        result.put("dispatchNumber", dispatch.getOddNumber() != null ? dispatch.getOddNumber() : "-");
        result.put("location", dispatch.getOddNumber() != null ? dispatch.getOddNumber() : "-");
        result.put("totalPrice", totalPrice);
        result.put("totalCommission", totalCommission);
        result.put("totalWorkHours", totalWorkHours);
        result.put("avgWorkHours", avgWorkHours);
        result.put("status", status);
        result.put("installerDetailList", installerDetailList);

        outputObject.setBean(result);
    }

}

