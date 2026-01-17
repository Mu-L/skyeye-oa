/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.afterseal.classenum.AfterSealState;
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
        String totalCommissionStr = StrUtil.isEmpty(dispatch.getInstallFee()) ? "0" : dispatch.getInstallFee();

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

        // 计算总工时（等于所有个人工时的总和）
        String totalWorkHours = commissionList.stream()
            .map(ProjectInstallerCommission::getWorkHours)
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

            // 按工单分组，分别计算每个工单的平均日提，然后取平均值
            // 示例：如果工人在两个工单工作
            //   - 工单A：提成100元，工时2天 → 平均日提 = 50元/天
            //   - 工单B：提成300元，工时3天 → 平均日提 = 100元/天
            // 平均日提 = (50 + 100) / 2 = 75元/天
            // 注意：只计算当前安装员参与的工单，而不是所有工单
            Map<String, List<ProjectInstallerCommission>> installerDispatchGroupMap = installerCommissions.stream()
                .collect(Collectors.groupingBy(ProjectInstallerCommission::getDispatchId));

            List<String> avgDailyCommissionList = new ArrayList<>();
            for (List<ProjectInstallerCommission> dispatchCommissions : installerDispatchGroupMap.values()) {
                String dispatchCommission = dispatchCommissions.stream()
                    .map(ProjectInstallerCommission::getInstallerCommission)
                    .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));
                String dispatchWorkHours = dispatchCommissions.stream()
                    .map(ProjectInstallerCommission::getWorkHours)
                    .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

                if (CalculationUtil.compareTo(dispatchWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0) {
                    String dispatchAvgDailyCommission = CalculationUtil.divide(dispatchCommission, dispatchWorkHours, CommonNumConstants.NUM_TWO);
                    avgDailyCommissionList.add(dispatchAvgDailyCommission);
                }
            }

            // 计算所有工单的平均日提的算术平均值
            String avgDailyCommission = "0";
            if (!avgDailyCommissionList.isEmpty()) {
                String sum = avgDailyCommissionList.stream()
                    .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));
                avgDailyCommission = CalculationUtil.divide(sum, String.valueOf(avgDailyCommissionList.size()), CommonNumConstants.NUM_TWO);
            }

            Map<String, Object> installerSummary = new HashMap<>();
            installerSummary.put("installerId", installerId);
            installerSummary.put("installerName", installerInfoMap.getOrDefault(installerId, new HashMap<>()).getOrDefault("name", "未知"));
            installerSummary.put("totalCommission", installerTotalCommission);
            installerSummary.put("totalWorkHours", installerTotalWorkHours);
            installerSummary.put("avgDailyCommission", avgDailyCommission);
            installerSummaryList.add(installerSummary);
        }

        // 按提成排序计算排名（降序：提成多的在前）
        installerSummaryList.sort((a, b) -> {
            String commissionA = a.get("totalCommission").toString();
            String commissionB = b.get("totalCommission").toString();
            // 降序排序：如果A > B，返回负数（A排在前面）；如果A < B，返回正数（A排在后面）
            return -CalculationUtil.compareTo(commissionA, commissionB, CommonNumConstants.NUM_TWO, RoundingMode.UP);
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
            // 项目明细中的总工时 = 该工单下所有安装员的个人工时之和
            String dispatchTotalWorkHours = dispatchCommissions.stream()
                .map(ProjectInstallerCommission::getWorkHours)
                .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

            String dispatchAvgWorkHours = CalculationUtil.compareTo(dispatchTotalWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0
                ? CalculationUtil.divide(dispatchTotalPrice, dispatchTotalWorkHours, CommonNumConstants.NUM_TWO)
                : "0";

            Map<String, Object> projectDetail = new HashMap<>();
            projectDetail.put("dispatchId", dispatchId);
            projectDetail.put("oddNumber", dispatch.getOddNumber());
            projectDetail.put("totalPrice", dispatchTotalPrice);
            projectDetail.put("totalWorkHours", dispatchTotalWorkHours);
            projectDetail.put("avgWorkHours", dispatchAvgWorkHours);

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

        // 查询该工单下所有提成记录
        QueryWrapper<ProjectInstallerCommission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProjectInstallerCommission::getDispatchId), dispatchId);
        List<ProjectInstallerCommission> commissionList = list(queryWrapper);

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

        // 计算该工单的总工时（等于所有安装员的个人工时之和）
        String totalWorkHours = commissionList.stream()
            .map(ProjectInstallerCommission::getWorkHours)
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

        // 获取工单的安装费用
        String totalPrice = dispatch.getInstallFee() != null ? dispatch.getInstallFee() : "0";

        // 计算平均工时（总工价/总工时）
        String avgWorkHours = CalculationUtil.compareTo(totalWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0
            ? CalculationUtil.divide(totalPrice, totalWorkHours, CommonNumConstants.NUM_TWO)
            : "0";

        // 该工单下各安装员的提成明细
        List<Map<String, Object>> installerDetailList = new ArrayList<>();
        for (ProjectInstallerCommission commission : commissionList) {
            String installerId = commission.getInstallerId();
            Map<String, Object> installerDetail = new HashMap<>();
            installerDetail.put("installerId", installerId);
            installerDetail.put("installerName", installerInfoMap.getOrDefault(installerId, new HashMap<>()).getOrDefault("name", "未知"));
            installerDetail.put("commission", commission.getInstallerCommission());
            installerDetail.put("workHours", commission.getWorkHours());
            installerDetail.put("totalWorkHours", totalWorkHours); // 使用计算出来的总工时（所有安装员的个人工时之和）
            installerDetail.put("commissionRate", commission.getCommissionRate());
            installerDetailList.add(installerDetail);
        }

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("dispatchId", dispatchId);
        result.put("dispatchNumber", dispatch.getOddNumber());
        result.put("location", dispatch.getAbsoluteAddress());
        result.put("totalPrice", totalPrice);
        result.put("totalWorkHours", totalWorkHours);
        result.put("avgWorkHours", avgWorkHours);
        result.put("installerDetailList", installerDetailList);

        outputObject.setBean(result);
    }

    /**
     * 查询安装员考核大屏数据
     */
    @Override
    public void queryInstallerDashboard(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String startDate = params.get("startDate").toString();
        String endDate = params.get("endDate").toString();
        String status = params.get("status").toString();

        // 查询所有提成记录（根据时间范围筛选）
        QueryWrapper<ProjectInstallerCommission> commissionQueryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate)) {
            commissionQueryWrapper.apply("DATE(create_time) >= {0}", startDate)
                .apply("DATE(create_time) <= {0}", endDate);
        }
        List<ProjectInstallerCommission> allCommissionList = list(commissionQueryWrapper);

        if (CollectionUtil.isEmpty(allCommissionList)) {
            Map<String, Object> result = new HashMap<>();
            result.put("summary", new HashMap<>());
            result.put("commissionRanking", new ArrayList<>());
            result.put("workHoursRanking", new ArrayList<>());
            result.put("avgDailyCommissionRanking", new ArrayList<>());
            outputObject.setBean(result);
            return;
        }

        // 获取所有工单ID
        Set<String> dispatchIds = allCommissionList.stream()
            .map(ProjectInstallerCommission::getDispatchId)
            .collect(Collectors.toSet());

        // 查询工单信息，用于状态筛选
        Map<String, AfterSeal> dispatchMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(dispatchIds)) {
            List<AfterSeal> dispatchList = afterSealService.listByIds(new ArrayList<>(dispatchIds));
            dispatchMap = dispatchList.stream()
                .collect(Collectors.toMap(AfterSeal::getId, d -> d));
        }

        // 根据项目状态筛选提成记录
        List<ProjectInstallerCommission> filteredCommissionList = new ArrayList<>();
        if ("completed".equals(status)) {
            // 已完成：工单状态为已完工
            for (ProjectInstallerCommission commission : allCommissionList) {
                AfterSeal dispatch = dispatchMap.get(commission.getDispatchId());
                if (dispatch != null && AfterSealState.COMPLATE.getKey().equals(dispatch.getState())) {
                    filteredCommissionList.add(commission);
                }
            }
        } else if ("uncompleted".equals(status)) {
            // 未完成：工单状态不是已完工
            for (ProjectInstallerCommission commission : allCommissionList) {
                AfterSeal dispatch = dispatchMap.get(commission.getDispatchId());
                if (dispatch != null && !AfterSealState.COMPLATE.getKey().equals(dispatch.getState())) {
                    filteredCommissionList.add(commission);
                }
            }
        } else {
            // 全部
            filteredCommissionList = allCommissionList;
        }

        if (CollectionUtil.isEmpty(filteredCommissionList)) {
            Map<String, Object> result = new HashMap<>();
            result.put("summary", new HashMap<>());
            result.put("commissionRanking", new ArrayList<>());
            result.put("workHoursRanking", new ArrayList<>());
            result.put("avgDailyCommissionRanking", new ArrayList<>());
            outputObject.setBean(result);
            return;
        }

        // 按安装员分组统计
        Map<String, List<ProjectInstallerCommission>> installerGroupMap = filteredCommissionList.stream()
            .collect(Collectors.groupingBy(ProjectInstallerCommission::getInstallerId));

        // 获取所有安装员信息
        Set<String> installerIds = installerGroupMap.keySet();
        Map<String, Map<String, Object>> installerInfoMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(installerIds)) {
            installerInfoMap = iAuthUserService.queryUserNameList(new ArrayList<>(installerIds));
        }

        // 计算总体数据
        String totalCommission = filteredCommissionList.stream()
            .map(ProjectInstallerCommission::getInstallerCommission)
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));

        String totalWorkHours = filteredCommissionList.stream()
            .map(ProjectInstallerCommission::getWorkHours)
            .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

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

            // 按工单分组，分别计算每个工单的平均日提，然后取平均值
            Map<String, List<ProjectInstallerCommission>> installerDispatchGroupMap = installerCommissions.stream()
                .collect(Collectors.groupingBy(ProjectInstallerCommission::getDispatchId));

            List<String> avgDailyCommissionList = new ArrayList<>();
            for (List<ProjectInstallerCommission> dispatchCommissions : installerDispatchGroupMap.values()) {
                String dispatchCommission = dispatchCommissions.stream()
                    .map(ProjectInstallerCommission::getInstallerCommission)
                    .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));
                String dispatchWorkHours = dispatchCommissions.stream()
                    .map(ProjectInstallerCommission::getWorkHours)
                    .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_FOUR, a, b));

                if (CalculationUtil.compareTo(dispatchWorkHours, "0", CommonNumConstants.NUM_FOUR, RoundingMode.UP) > 0) {
                    String dispatchAvgDailyCommission = CalculationUtil.divide(dispatchCommission, dispatchWorkHours, CommonNumConstants.NUM_TWO);
                    avgDailyCommissionList.add(dispatchAvgDailyCommission);
                }
            }

            String avgDailyCommission = "0";
            if (!avgDailyCommissionList.isEmpty()) {
                String sum = avgDailyCommissionList.stream()
                    .reduce("0", (a, b) -> CalculationUtil.add(CommonNumConstants.NUM_TWO, a, b));
                avgDailyCommission = CalculationUtil.divide(sum, String.valueOf(avgDailyCommissionList.size()), CommonNumConstants.NUM_TWO);
            }

            Map<String, Object> installerSummary = new HashMap<>();
            installerSummary.put("installerId", installerId);
            installerSummary.put("installerName", installerInfoMap.getOrDefault(installerId, new HashMap<>()).getOrDefault("name", "未知"));
            installerSummary.put("totalCommission", installerTotalCommission);
            installerSummary.put("totalWorkHours", installerTotalWorkHours);
            installerSummary.put("avgDailyCommission", avgDailyCommission);
            installerSummaryList.add(installerSummary);
        }

        // 生成排名数据
        // 提成排名（按提成金额降序）
        List<Map<String, Object>> commissionRanking = new ArrayList<>(installerSummaryList);
        commissionRanking.sort((a, b) -> {
            String commissionA = a.get("totalCommission").toString();
            String commissionB = b.get("totalCommission").toString();
            return -CalculationUtil.compareTo(commissionA, commissionB, CommonNumConstants.NUM_TWO, RoundingMode.UP);
        });

        // 工时排名（按工时降序）
        List<Map<String, Object>> workHoursRanking = new ArrayList<>(installerSummaryList);
        workHoursRanking.sort((a, b) -> {
            String workHoursA = a.get("totalWorkHours").toString();
            String workHoursB = b.get("totalWorkHours").toString();
            return -CalculationUtil.compareTo(workHoursA, workHoursB, CommonNumConstants.NUM_FOUR, RoundingMode.UP);
        });

        // 平均日提排名（按平均日提降序）
        List<Map<String, Object>> avgDailyCommissionRanking = new ArrayList<>(installerSummaryList);
        avgDailyCommissionRanking.sort((a, b) -> {
            String avgA = a.get("avgDailyCommission").toString();
            String avgB = b.get("avgDailyCommission").toString();
            return -CalculationUtil.compareTo(avgA, avgB, CommonNumConstants.NUM_TWO, RoundingMode.UP);
        });

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("totalCommission", totalCommission);
        summaryMap.put("totalWorkHours", totalWorkHours);
        summaryMap.put("avgWorkHours", avgWorkHours);
        result.put("summary", summaryMap);
        result.put("commissionRanking", commissionRanking);
        result.put("workHoursRanking", workHoursRanking);
        result.put("avgDailyCommissionRanking", avgDailyCommissionRanking);

        outputObject.setBean(result);
    }

}

