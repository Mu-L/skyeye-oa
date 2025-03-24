package com.skyeye.joincircle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: JoinLimit
 * @Description: 加入圈子限制实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_join_limit")
@ApiModel(value = "加入圈子限制实体层")
public class JoinLimit extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField("object_id")
    @ApiModelProperty("业务逻辑id")
    private String objectId;

    @TableField("user_id")
    @ApiModelProperty("用户id")
    private String userId;
}
