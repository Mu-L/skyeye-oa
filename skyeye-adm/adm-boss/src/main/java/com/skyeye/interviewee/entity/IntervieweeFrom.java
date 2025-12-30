/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: IntervieweeFrom
 * @Description: 面试者来源实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/22 20:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "boss:interviewee:from", cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "boss_interviewee_from")
@ApiModel("面试者来源实体类")
public class IntervieweeFrom extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题", required = "required")
    private String name;

    @TableField(value = "from_url")
    @ApiModelProperty(value = "来源地址")
    private String fromUrl;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

}
