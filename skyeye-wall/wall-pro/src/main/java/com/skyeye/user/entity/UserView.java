package com.skyeye.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: UserView
 * @Description: 用户访客记录实体类
 * @author: skyeye云系列--lqy
 * @date: 2025/5/5 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_user_view")
@ApiModel(value = "用户访客记录实体类")
public class UserView extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`user_id`")
    @ApiModelProperty(value = "被访问人id")
    private String userId;

    @TableField("visitor_user_id")
    @ApiModelProperty(value = "访问人id")
    private String visitorUserId;

    @TableField(exist = false)
    @Property(value = "访问人信息")
    private Map<String,Object> visitorUserMation;

    @TableField("view_time")
    @ApiModelProperty(value = "最新访问时间")
    private String viewTime;

    @TableField("view_count")
    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;
}
