/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.app.dao.AppReleaseDao;
import com.skyeye.app.entity.AppProject;
import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.enums.AppReleaseStatusEnum;
import com.skyeye.app.service.AppProjectService;
import com.skyeye.app.service.AppReleaseService;
import com.skyeye.app.service.AppStoreService;
import com.skyeye.app.service.AppVersionService;
import com.skyeye.app.store.AppStoreServiceManager;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AppReleaseServiceImpl
 * @Description: APP版本发布信息业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 14:57
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Slf4j
@Service
@SkyeyeService(name = "APP版本发布信息", groupName = "APP版本发布模块", tenant = TenantEnum.PLATE)
public class AppReleaseServiceImpl extends SkyeyeBusinessServiceImpl<AppReleaseDao, AppRelease> implements AppReleaseService {

    @Autowired
    private AppVersionService appVersionService;

    @Autowired
    private AppStoreService appStoreService;

    @Autowired
    private AppProjectService appProjectService;

    @Autowired
    private AppStoreServiceManager appStoreServiceManager;

    @Override
    public void saveList(String versionId, String projectId, List<AppRelease> beans) {
        if (CollectionUtil.isNotEmpty(beans)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            for (AppRelease appRelease : beans) {
                appRelease.setVersionId(versionId);
                appRelease.setProjectId(projectId);
            }
            createEntity(beans, userId);
        }
    }

    @Override
    protected QueryWrapper<AppRelease> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AppRelease> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getProjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getVersionId), commonPageInfo.getHolderId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getPlatform), commonPageInfo.getType());
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        appVersionService.setMationForMap(beans, "versionId", "versionMation");
        appStoreService.setMationForMap(beans, "storeId", "storeMation");
        return beans;
    }

    @Override
    @IgnoreTenant
    public List<AppRelease> selectByVersionIdAndProjectId(String versionId, String projectId, String storeKey, String status) {
        MPJLambdaWrapper<AppRelease> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        if (StrUtil.isNotEmpty(storeKey)) {
            mpjLambdaWrapper.innerJoin(AppStore.class, AppStore::getId, AppRelease::getStoreId);
            mpjLambdaWrapper.eq(AppStore::getStoreKey, storeKey);
        }
        if (StrUtil.isNotEmpty(status)) {
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getStatus), status);
        }
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getProjectId), projectId);
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getVersionId), versionId);

        List<AppRelease> list = skyeyeBaseMapper.selectJoinList(AppRelease.class, mpjLambdaWrapper);
        return list;
    }

    @Override
    public void updateAppReleaseStateById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String state = params.get("state").toString();
        updateReleaseStatus(id, state);
    }

    @Override
    public void getLatestVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String projectKey = params.get("projectKey").toString();
        String platform = params.get("platform").toString();
        String storeKey = params.get("storeKey").toString();

        AppProject appProject = appProjectService.selectByKey(projectKey);
        if (ObjectUtil.isEmpty(appProject) || StrUtil.isBlank(appProject.getId())) {
            log.warn("项目不存在，请检查项目ID是否正确");
            return;
        }
        // 获取指定项目、平台和应用商店的最新版本
        AppVersion latestVersion = appVersionService.getLatestVersionByProjectAndPlatform(appProject.getId(), platform, storeKey);

        if (ObjectUtil.isEmpty(latestVersion)) {
            log.warn("该项目没有发布过新版本，请等待管理员发布版本");
            return;
        }

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("versionInfo", latestVersion);
        outputObject.setBean(result);
    }

    /**
     * 修改APP发布记录状态
     * 根据状态流转规则验证状态变更的合法性
     *
     * @param releaseId 发布记录ID
     * @param newStatus 新状态
     * @return 是否修改成功
     */
    public boolean updateReleaseStatus(String releaseId, String newStatus) {
        try {
            // 获取发布记录
            AppRelease appRelease = selectById(releaseId);
            if (ObjectUtil.isEmpty(appRelease) || StrUtil.isBlank(appRelease.getId())) {
                log.warn("修改发布状态失败：发布记录不存在，ID：{}", releaseId);
                return false;
            }

            String currentStatus = appRelease.getStatus();
            log.info("开始修改发布记录[{}]状态：{} -> {}", releaseId, currentStatus, newStatus);

            // 验证状态流转是否合法
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                log.warn("状态流转不合法：{} -> {}", currentStatus, newStatus);
                throw new CustomException("状态流转不合法，无法从 " + AppReleaseStatusEnum.getNameByKey(currentStatus) + " 变更为 " + AppReleaseStatusEnum.getNameByKey(newStatus));
            }

            UpdateWrapper<AppRelease> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, releaseId);
            updateWrapper.set(MybatisPlusUtil.toColumns(AppRelease::getStatus), newStatus);
            if (StrUtil.equals(AppReleaseStatusEnum.SUBMITTED.getKey(), newStatus)) {
                // 提交
                updateWrapper.set(MybatisPlusUtil.toColumns(AppRelease::getSubmitTime), DateUtil.getTimeAndToString());
            }

            // 保存更新
            boolean result = update(updateWrapper);

            if (result) {
                if (StrUtil.equals(AppReleaseStatusEnum.SUBMITTED.getKey(), newStatus)) {
                    // 提交
//                    AppVersion appVersion = appVersionService.selectById(appRelease.getVersionId());
//                    AppStore appStore = appStoreService.selectById(appRelease.getStoreId());
//                    appStoreServiceManager.submitApp(appStore.getStoreKey(), appVersion, appRelease, appStore);
                } else if (StrUtil.equals(AppReleaseStatusEnum.CANCELLED.getKey(), newStatus)) {
                    // 取消
//                    AppStore appStore = appStoreService.selectById(appRelease.getStoreId());
//                    appStoreServiceManager.cancelApp(appStore.getStoreKey(), appRelease, appStore);
                } else if (StrUtil.equals(AppReleaseStatusEnum.REMOVED.getKey(), newStatus)) {
                    // 下架
//                    AppStore appStore = appStoreService.selectById(appRelease.getStoreId());
//                    appStoreServiceManager.removeApp(appStore.getStoreKey(), appRelease, appStore);
                }
                log.info("发布记录[{}]状态修改成功：{} -> {}", releaseId, currentStatus, newStatus);
            } else {
                log.error("发布记录[{}]状态修改失败", releaseId);
            }

            return result;
        } catch (Exception e) {
            log.error("修改发布记录[{}]状态时发生异常", releaseId, e);
            throw new CustomException("修改发布状态失败：" + e.getMessage());
        }
    }

    /**
     * 验证状态流转是否合法
     *
     * @param currentStatus 当前状态
     * @param newStatus     新状态
     * @return 是否合法
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // 如果状态相同，认为是合法的（不需要变更）
        if (currentStatus.equals(newStatus)) {
            return true;
        }

        // 获取当前状态的下一个可能状态
        AppReleaseStatusEnum[] nextPossibleStatuses = AppReleaseStatusEnum.getNextPossibleStatuses(currentStatus);

        // 检查新状态是否在下一个可能状态中
        for (AppReleaseStatusEnum status : nextPossibleStatuses) {
            if (status.getKey().equals(newStatus)) {
                return true;
            }
        }

        return false;
    }

}
