/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AppReleaseStatusEnum
 * @Description: APP发布状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/01/01 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 * 待发布 → 已提交 → 审核中 → 审核通过 → 已发布
 *   ↓       ↓       ↓        ↓        ↓
 *  取消    取消     取消      失败      下架
 *   ↓       ↓       ↓        ↓        ↓
 *  取消    取消     取消     重新提交   待发布
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AppReleaseStatusEnum implements SkyeyeEnumClass {

    PENDING("pending", "待发布", "#E6A23C", "已配置发布信息，等待提交到应用商店", true, true),
    SUBMITTED("submitted", "已提交", "#409EFF", "已提交到应用商店，等待审核", true, false),
    REVIEWING("reviewing", "审核中", "#F56C6C", "应用商店正在审核应用", true, false),
    APPROVED("approved", "审核通过", "#67C23A", "应用商店审核通过，等待发布", true, false),
    PUBLISHED("published", "已发布", "#67C23A", "应用已成功发布到应用商店", true, false),
    REJECTED("rejected", "审核拒绝", "#F56C6C", "应用商店审核拒绝，需要修改后重新提交", true, false),
    FAILED("failed", "发布失败", "#F56C6C", "发布过程中出现错误，需要重新发布", true, false),
    CANCELLED("cancelled", "已取消", "#909399", "发布任务被用户取消", true, false),
    REMOVED("removed", "已下架", "#909399", "应用已从应用商店下架", true, false);

    private String key;
    private String value;
    private String color;
    private String description;
    private Boolean show;
    private Boolean isDefault;

    /**
     * 根据key获取枚举
     *
     * @param key 状态key
     * @return 对应的枚举值
     */
    public static AppReleaseStatusEnum getByKey(String key) {
        for (AppReleaseStatusEnum status : values()) {
            if (status.getKey().equals(key)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为终态状态（不会再次变化的状态）
     *
     * @param status 状态key
     * @return 是否为终态
     */
    public static boolean isFinalStatus(String status) {
        return PUBLISHED.getKey().equals(status) ||
            REJECTED.getKey().equals(status) ||
            FAILED.getKey().equals(status) ||
            CANCELLED.getKey().equals(status) ||
            REMOVED.getKey().equals(status);
    }

    /**
     * 判断是否为可编辑状态
     *
     * @param status 状态key
     * @return 是否可编辑
     */
    public static boolean isEditableStatus(String status) {
        return PENDING.getKey().equals(status) ||
            REJECTED.getKey().equals(status);
    }

    /**
     * 判断是否为可提交状态
     *
     * @param status 状态key
     * @return 是否可提交
     */
    public static boolean isSubmittableStatus(String status) {
        return PENDING.getKey().equals(status) ||
            REJECTED.getKey().equals(status);
    }

    /**
     * 判断是否为可取消状态
     *
     * @param status 状态key
     * @return 是否可取消
     */
    public static boolean isCancellableStatus(String status) {
        return PENDING.getKey().equals(status) ||
            SUBMITTED.getKey().equals(status) ||
            REVIEWING.getKey().equals(status);
    }

    /**
     * 获取状态流转的下一个可能状态
     *
     * @param currentStatus 当前状态
     * @return 下一个可能的状态数组
     */
    public static AppReleaseStatusEnum[] getNextPossibleStatuses(String currentStatus) {
        switch (currentStatus) {
            case "pending":
                return new AppReleaseStatusEnum[]{SUBMITTED, CANCELLED};
            case "submitted":
                return new AppReleaseStatusEnum[]{REVIEWING, REJECTED, CANCELLED};
            case "reviewing":
                return new AppReleaseStatusEnum[]{APPROVED, REJECTED, CANCELLED};
            case "approved":
                return new AppReleaseStatusEnum[]{PUBLISHED, FAILED};
            case "rejected":
                return new AppReleaseStatusEnum[]{PENDING, CANCELLED};
            case "published":
                return new AppReleaseStatusEnum[]{REMOVED};
            case "failed":
                return new AppReleaseStatusEnum[]{SUBMITTED, CANCELLED};
            case "cancelled":
                return new AppReleaseStatusEnum[]{PENDING};
            case "removed":
                return new AppReleaseStatusEnum[]{PENDING};
            default:
                return new AppReleaseStatusEnum[]{};
        }
    }

}
