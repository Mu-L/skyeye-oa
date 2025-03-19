package com.skyeye.focus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: Focus
 * @Description: 视频关注实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName(value = "wall_focus")
@ApiModel(value = "视频关注实体类")
public class Focus extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("id。为空时新增，不为空时编辑")
    private String id;

    @TableField("user_id")
    @ApiModelProperty(value = "关注用户id", required = "required")
    private String videoId;

    // TODO 加一个布尔类型的字段，判断当前登录人是否关注了这个视频
    @TableField(exist = false)
    @Property(value = "当前登陆人是否关注")
    private Boolean checkUpvote;
}
