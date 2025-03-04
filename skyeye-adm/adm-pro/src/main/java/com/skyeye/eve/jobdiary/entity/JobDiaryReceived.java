/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.jobdiary.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.eve.jobdiary.classenum.ReadState;
import lombok.Data;

/**
 * @ClassName: JobDiaryReceived
 * @Description: 工作日志接收人实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/24 11:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "job:diaryReceived", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "job_diary_received")
@ApiModel("工作日志接收人实体类")
public class JobDiaryReceived extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = ReadState.class)
    private Integer state;

    @TableField(value = "diary_id")
    @Property(value = "日志id")
    private String diaryId;

    @TableField(value = "received_id")
    @Property(value = "接收人id")
    private String receivedId;

}
