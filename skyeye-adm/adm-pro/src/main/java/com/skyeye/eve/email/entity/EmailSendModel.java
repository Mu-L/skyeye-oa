/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.entity;

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
 * @ClassName: EmailSendModel
 * @Description: 邮件发送模板实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/9 19:41
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "email:sendModel", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "email_send_model", autoResultMap = true)
@ApiModel("邮件发送模板实体类")
public class EmailSendModel extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField("to_people")
    @ApiModelProperty(value = "收件人", required = "required")
    private String toPeople;

    @TableField("to_cc")
    @ApiModelProperty(value = "抄送人")
    private String toCc;

    @TableField("to_bcc")
    @ApiModelProperty(value = "暗送人")
    private String toBcc;

}
