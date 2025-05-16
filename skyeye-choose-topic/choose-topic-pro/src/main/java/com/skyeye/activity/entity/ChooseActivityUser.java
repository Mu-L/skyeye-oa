/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ActivityUser
 * @Description: 活动可参与用户实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "choose:activityUser", cacheTime = RedisConstants.ONE_WEEK_SECONDS)
@TableName(value = "choose_activity_user")
@ApiModel(value = "活动可参与用户实体类")
public class ChooseActivityUser extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "activity_id")
    @ApiModelProperty(value = "活动id",required = "required")
    private String activityId;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id",required = "required")
    private String userId;
}
