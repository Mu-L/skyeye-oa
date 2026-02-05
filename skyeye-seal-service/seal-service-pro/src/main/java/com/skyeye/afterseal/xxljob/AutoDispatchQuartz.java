/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dispatch.classenum.DispatchRuleCodeEnum;
import com.skyeye.dispatch.entity.SealDispatchConfig;
import com.skyeye.dispatch.service.SealDispatchConfigService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.ordertype.entity.SealOrderType;
import com.skyeye.ordertype.service.SealOrderTypeAllowStaffService;
import com.skyeye.ordertype.service.SealOrderTypeService;
import com.skyeye.worker.entity.SealWorker;
import com.skyeye.worker.service.SealWorkerService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoDispatchQuartz
 * @Description: 工单自动派单定时任务
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class AutoDispatchQuartz {

    /**
     * 租户服务，用于多租户场景下遍历所有租户
     */
    @Autowired
    private ITenantService iTenantService;

    /**
     * 是否开启多租户
     */
    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    /**
     * 派单规则配置服务
     */
    @Autowired
    private SealDispatchConfigService sealDispatchConfigService;

    /**
     * 工单服务
     */
    @Autowired
    private AfterSealService afterSealService;

    /**
     * 工人/服务人员服务
     */
    @Autowired
    private SealWorkerService sealWorkerService;

    /**
     * 工单类型服务
     */
    @Autowired
    private SealOrderTypeService sealOrderTypeService;

    /**
     * 工单类型允许的接单人服务
     */
    @Autowired
    private SealOrderTypeAllowStaffService sealOrderTypeAllowStaffService;

    /**
     * MQ消息服务，用于派工后发送通知
     */
    @Autowired
    private IJobMateMationService iJobMateMationService;

    /**
     * 执行自动派单
     * 建议执行频率：每5分钟执行一次
     * 实际派单仅在派单规则配置的 auto_dispatch_start_time ~ auto_dispatch_end_time 时间段内执行
     */
    @XxlJob("sealAutoDispatchQuartz")
    public void sealAutoDispatchQuartz() {
        log.info("开始执行工单自动派单");
        try {
            if (tenantEnable) {
                // 多租户模式：遍历每个租户执行自动派单
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    String tenantId = tenant.get("id").toString();
                    TenantContext.setTenantId(tenantId);
                    try {
                        executeAutoDispatch();
                    } catch (Exception e) {
                        log.warn("租户[{}]自动派单执行异常", tenantId, e);
                    }
                });
            } else {
                executeAutoDispatch();
            }
        } catch (Exception e) {
            log.warn("工单自动派单执行失败", e);
        }
        log.info("工单自动派单执行结束");
    }

    /**
     * 执行当前租户的自动派单逻辑
     * 1. 校验是否在配置的派单时间窗口内
     * 2. 查询待派工状态的工单
     * 3. 批量构建派单上下文（工人、工单类型、工单数等）
     * 4. 按规则为每个工单选择服务人员并派单
     */
    public void executeAutoDispatch() {
        SealDispatchConfig config = sealDispatchConfigService.getConfigForTenant();
        if (config == null) return;

        // 校验自动派单时间窗口
        String startTime = config.getAutoDispatchStartTime();
        String endTime = config.getAutoDispatchEndTime();
        if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) return;

        String currentHHmm = DateUtil.getHmTimeAndToString();
        if (currentHHmm.compareTo(startTime) < 0 || currentHHmm.compareTo(endTime) > 0) return;

        // 查询待派工状态的工单
        QueryWrapper<AfterSeal> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.BE_DISPATCHED.getKey());
        List<AfterSeal> orders = afterSealService.list(qw);
        if (CollectionUtil.isEmpty(orders)) return;

        // 批量查询并缓存派单所需数据，避免循环内重复查询
        DispatchContext ctx = buildDispatchContext(orders);

        int dispatched = 0;
        for (AfterSeal order : orders) {
            String workerUserId = selectWorkerForOrder(order, config, ctx);
            if (StrUtil.isNotEmpty(workerUserId)) {
                try {
                    dispatchOrderToWorker(order, workerUserId);
                    updateWorkerOrderCount(ctx, workerUserId, 1);
                    dispatched++;
                } catch (Exception e) {
                    log.warn("自动派单失败, orderId={}, workerId={}, err={}", order.getId(), workerUserId, e.getMessage());
                }
            }
        }
        if (dispatched > 0) {
            log.info("自动派单完成, 成功派单数={}", dispatched);
        }
    }

    /**
     * 派单上下文，缓存批量查询结果，避免循环内重复查询
     */
    private static class DispatchContext {
        /**
         * 所有工人/服务人员列表
         */
        List<SealWorker> allWorkers;
        /**
         * 工单类型ID -> 工单类型
         */
        Map<String, SealOrderType> orderTypeMap;
        /**
         * 工单类型ID -> 该类型允许的接单人列表
         */
        Map<String, List<com.skyeye.ordertype.entity.SealOrderTypeAllowStaff>> allowStaffMap;
        /**
         * 主账号对应的工人（用于 main_account 规则）
         */
        SealWorker mainAccountWorker;
        /**
         * 工人userId -> 当前待接单+进行中工单数
         */
        Map<String, Long> workerOrderCountMap;
    }

    /**
     * 构建派单上下文，批量查询工人、工单类型、允许接单人、工人工单数等数据
     *
     * @param orders 待派工工单列表
     * @return 派单上下文
     */
    private DispatchContext buildDispatchContext(List<AfterSeal> orders) {
        DispatchContext ctx = new DispatchContext();
        // 查询所有工人
        ctx.allWorkers = sealWorkerService.list();
        if (CollectionUtil.isEmpty(ctx.allWorkers)) return ctx;

        List<String> allUserIds = ctx.allWorkers.stream().map(SealWorker::getUserId).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());

        // 批量查询工单类型及允许接单人
        List<String> orderTypeIds = orders.stream().map(AfterSeal::getOrderTypeId).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        ctx.orderTypeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(orderTypeIds)) {
            List<SealOrderType> types = sealOrderTypeService.selectByIds(orderTypeIds.toArray(new String[0]));
            if (CollectionUtil.isNotEmpty(types)) {
                types.forEach(t -> ctx.orderTypeMap.put(t.getId(), t));
            }
            ctx.allowStaffMap = sealOrderTypeAllowStaffService.selectByOrderTypeIds(orderTypeIds);
        } else {
            ctx.allowStaffMap = new HashMap<>();
        }

        // 主账号工人（用于 main_account 规则兜底）
        String adminId = CommonConstants.ADMIN_USER_ID;
        ctx.mainAccountWorker = StrUtil.isNotEmpty(adminId) ? sealWorkerService.selectByUserId(adminId) : null;

        // 批量统计每个工人当前工单数
        ctx.workerOrderCountMap = afterSealService.batchCountWorkerCurrentOrderCount(allUserIds);
        return ctx;
    }

    /**
     * 根据派单规则为工单选择服务人员
     * 1. 按规则顺序（waterfall/and）获取候选人
     * 2. 按封顶接单量过滤
     * 3. 若开启均匀指派，按工单数升序排序
     *
     * @param order  待派工单
     * @param config 派单配置
     * @param ctx    派单上下文
     * @return 选中的服务人员userId，若无合适人选返回null
     */
    protected String selectWorkerForOrder(AfterSeal order, SealDispatchConfig config, DispatchContext ctx) {
        List<Map<String, Object>> rules = config.getSystemRules();
        if (CollectionUtil.isEmpty(rules)) return null;

        // 按规则顺序获取候选人，waterfall 取第一个有结果的规则，and 取交集
        List<String> candidateUserIds = null;
        for (Map<String, Object> rule : rules) {
            Object enabled = rule.get("enabled");
            if (enabled != null && !Boolean.TRUE.equals(enabled)) continue;

            String code = (String) rule.get("code");
            List<String> ids = getCandidatesByRule(order, code, ctx);
            if (CollectionUtil.isEmpty(ids)) continue;

            String mode = (String) rule.getOrDefault("mode", "waterfall");
            if ("waterfall".equals(mode)) {
                candidateUserIds = ids;
                break;
            }
            if ("and".equals(mode)) {
                candidateUserIds = candidateUserIds == null ? ids : ids.stream().filter(candidateUserIds::contains).collect(Collectors.toList());
            }
        }
        if (CollectionUtil.isEmpty(candidateUserIds)) return null;

        // 封顶接单量过滤：排除已达封顶的工人
        Integer cap = config.getCapOrderQuantity();
        if (cap != null && cap > 0) {
            candidateUserIds = candidateUserIds.stream()
                .filter(uid -> getWorkerOrderCount(uid, ctx) < cap)
                .collect(Collectors.toList());
        }
        if (CollectionUtil.isEmpty(candidateUserIds)) return null;

        // 均匀指派：按工单数升序，优先派给工单最少的工人
        if (config.getEvenAssignmentEnabled() != null && config.getEvenAssignmentEnabled() == 1) {
            candidateUserIds.sort(Comparator.comparingLong(uid -> getWorkerOrderCount(uid, ctx)));
        }
        return candidateUserIds.get(0);
    }

    /**
     * 从上下文中获取工人当前工单数
     */
    private long getWorkerOrderCount(String userId, DispatchContext ctx) {
        if (ctx.workerOrderCountMap == null) return 0;
        Long c = ctx.workerOrderCountMap.get(userId);
        return c != null ? c : 0;
    }

    /**
     * 派单成功后更新上下文中该工人的工单数，保证同批次内均匀指派准确
     */
    private void updateWorkerOrderCount(DispatchContext ctx, String userId, long delta) {
        if (ctx.workerOrderCountMap != null && StrUtil.isNotEmpty(userId)) {
            long cur = getWorkerOrderCount(userId, ctx);
            ctx.workerOrderCountMap.put(userId, cur + delta);
        }
    }

    /**
     * 根据规则编码获取候选人列表
     *
     * @param order 工单
     * @param code  规则编码：order_type/service_region/main_account 等
     * @param ctx   派单上下文
     * @return 候选人userId列表
     */
    private List<String> getCandidatesByRule(AfterSeal order, String code, DispatchContext ctx) {
        if (CollectionUtil.isEmpty(ctx.allWorkers)) return Collections.emptyList();
        List<String> allUserIds = ctx.allWorkers.stream().map(SealWorker::getUserId).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(allUserIds)) return Collections.emptyList();

        if (DispatchRuleCodeEnum.ORDER_TYPE.getKey().equals(code)) {
            return getOrderTypeCandidates(order, ctx);
        }
        if (DispatchRuleCodeEnum.SERVICE_REGION.getKey().equals(code)) {
            return getServiceRegionCandidates(order, ctx.allWorkers);
        }
        if (DispatchRuleCodeEnum.MAIN_ACCOUNT.getKey().equals(code)) {
            return getMainAccountCandidates(ctx);
        }
        return allUserIds;
    }

    /**
     * 按工单类型规则获取候选人：工单类型允许的接单人
     */
    private List<String> getOrderTypeCandidates(AfterSeal order, DispatchContext ctx) {
        String orderTypeId = order.getOrderTypeId();
        if (StrUtil.isEmpty(orderTypeId))
            return ctx.allWorkers.stream().map(SealWorker::getUserId).collect(Collectors.toList());

        SealOrderType orderType = ctx.orderTypeMap != null ? ctx.orderTypeMap.get(orderTypeId) : null;
        if (orderType == null) return ctx.allWorkers.stream().map(SealWorker::getUserId).collect(Collectors.toList());

        if (orderType.getIsAllowAllStaff() != null && orderType.getIsAllowAllStaff() == 1) {
            return ctx.allWorkers.stream().map(SealWorker::getUserId).collect(Collectors.toList());
        }

        List<com.skyeye.ordertype.entity.SealOrderTypeAllowStaff> allowList = ctx.allowStaffMap != null ? ctx.allowStaffMap.get(orderTypeId) : null;
        if (CollectionUtil.isEmpty(allowList)) return Collections.emptyList();

        Set<String> staffIds = allowList.stream().map(com.skyeye.ordertype.entity.SealOrderTypeAllowStaff::getStaffId).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        return ctx.allWorkers.stream().map(SealWorker::getUserId).filter(staffIds::contains).collect(Collectors.toList());
    }

    /**
     * 按服务区域规则获取候选人：工单地址与工人区域匹配（区>市>省）
     */
    private List<String> getServiceRegionCandidates(AfterSeal order, List<SealWorker> allWorkers) {
        String provinceId = order.getProvinceId();
        String cityId = order.getCityId();
        String areaId = order.getAreaId();

        List<String> result = new ArrayList<>();
        for (SealWorker w : allWorkers) {
            if (StrUtil.isNotEmpty(areaId) && StrUtil.isNotEmpty(w.getAreaId()) && areaId.equals(w.getAreaId())) {
                result.add(w.getUserId());
            } else if (StrUtil.isNotEmpty(cityId) && StrUtil.isNotEmpty(w.getCityId()) && cityId.equals(w.getCityId())) {
                result.add(w.getUserId());
            } else if (StrUtil.isNotEmpty(provinceId) && StrUtil.isNotEmpty(w.getProvinceId()) && provinceId.equals(w.getProvinceId())) {
                result.add(w.getUserId());
            }
        }
        return result.isEmpty() ? allWorkers.stream().map(SealWorker::getUserId).collect(Collectors.toList()) : result;
    }

    /**
     * 主账号规则：返回主账号对应的工人（若主账号不是工人则返回空）
     */
    private List<String> getMainAccountCandidates(DispatchContext ctx) {
        if (ctx.mainAccountWorker == null) return Collections.emptyList();
        return Collections.singletonList(ctx.mainAccountWorker.getUserId());
    }

    /**
     * 执行派单：更新工单状态、指派服务人员，并发送派工通知MQ
     *
     * @param order         工单
     * @param serviceUserId 服务人员userId
     */
    private void dispatchOrderToWorker(AfterSeal order, String serviceUserId) {
        UpdateWrapper<AfterSeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, order.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.PENDING_ORDERS.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), serviceUserId);
        updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getCooperationUserId), "[]");
        updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getServiceTime), DateUtil.getTimeAndToString());
        afterSealService.update(updateWrapper);
        afterSealService.refreshCache(order.getId());

        Map<String, Object> notice = new HashMap<>();
        notice.put("serviceId", order.getId());
        notice.put("type", MqConstants.JobMateMationJobType.WATI_WORKER_SEND.getJobType());
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(cn.hutool.json.JSONUtil.toJsonStr(notice));
        jobMateMation.setUserId(order.getCreateId());
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

}
