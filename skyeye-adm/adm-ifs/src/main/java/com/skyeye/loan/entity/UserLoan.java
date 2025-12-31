/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: UserLoan
 * @Description: 用户借款金额实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 13:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "ifs:userLoan", value = {"id", "userId"}, cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "ifs_user_loan", autoResultMap = true)
@ApiModel("用户借款金额实体类")
public class UserLoan extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id", required = "required")
    private String userId;

    @TableField(exist = false)
    @Property(value = "用户信息")
    private Map<String, Object> userMation;

    @TableField(value = "price")
    @ApiModelProperty(value = "借款总金额", required = "double")
    private String price;

}
