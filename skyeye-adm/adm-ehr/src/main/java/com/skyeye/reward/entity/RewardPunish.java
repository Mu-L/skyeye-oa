/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reward.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: RewardPunish
 * @Description: 员工奖惩信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/20 20:46
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "ehr:rewardPunish", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "sys_staff_reward_punish")
@ApiModel("员工奖惩信息实体类")
public class RewardPunish extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "reward_punish_time")
    @ApiModelProperty(value = "奖惩时间", required = "required")
    private String rewardPunishTime;

    @TableField(value = "price")
    @ApiModelProperty(value = "奖惩金额", required = "double", defaultValue = "0")
    private String price;

    @TableField(value = "content")
    @ApiModelProperty(value = "奖惩事件描述", required = "required")
    private String content;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "奖惩分类id，参考数据字典", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "奖惩分类信息")
    private Map<String, Object> typeMation;

    @TableField(value = "award_unit")
    @ApiModelProperty(value = "授予单位", required = "required")
    private String awardUnit;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(员工id)", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key(员工key)", required = "required")
    private String objectKey;

    @TableField(value = "link_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联得其他业务数据得id(工单id)")
    private String linkId;

    @TableField(value = "link_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联得其他业务数据得key(工单key)")
    private String linkKey;

    @TableField(value = "is_accounted")
    @ApiModelProperty(value = "是否已计入薪资", enumClass = WhetherEnum.class)
    private Integer isAccounted;

    @TableField(value = "account_month")
    @ApiModelProperty(value = "计入薪资的年月（如：2025-09）")
    private String accountMonth;

}
