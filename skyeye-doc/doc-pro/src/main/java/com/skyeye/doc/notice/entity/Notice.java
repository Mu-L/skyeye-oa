/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: Notice
 * @Description: 消息通知实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/21 20:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "doc:notice", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "notice", autoResultMap = true)
@ApiModel("消息通知实体类")
public class Notice extends BaseGeneralInfo {

    @TableField("receive_id")
    @ApiModelProperty(value = "接收人ID", required = "required")
    private String receiveId;

    @TableField("is_read")
    @ApiModelProperty(value = "是否已读", enumClass = WhetherEnum.class, required = "required")
    private Integer isRead;

}
