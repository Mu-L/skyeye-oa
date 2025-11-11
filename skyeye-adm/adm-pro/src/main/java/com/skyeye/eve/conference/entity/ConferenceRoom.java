/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.conference.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.eve.conference.classenum.ConferenceState;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ConferenceRoom
 * @Description: 会议室实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/6 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "assistant:conferenceRoom", cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "conference_room")
@ApiModel("会议室实体类")
public class ConferenceRoom extends BaseGeneralInfo {

    @TableField(value = "room_num")
    @Property(value = "会议室编号")
    private String roomNum;

    @TableField(value = "img")
    @ApiModelProperty(value = "会议室图片", required = "required")
    private String img;

    @TableField(value = "capacity")
    @ApiModelProperty(value = "会议室容量", required = "required,num")
    private Integer capacity;

    @TableField(value = "`position`")
    @ApiModelProperty(value = "会议室位置")
    private String position;

    @TableField(value = "equipment")
    @ApiModelProperty(value = "内部设备")
    private String equipment;

    @TableField(value = "room_admin")
    @ApiModelProperty(value = "管理人id")
    private String roomAdmin;

    @TableField(exist = false)
    @Property(value = "管理人")
    private Map<String, Object> roomAdminMation;

    @TableField(value = "state")
    @Property(value = "会议室状态", enumClass = ConferenceState.class)
    private Integer state;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
