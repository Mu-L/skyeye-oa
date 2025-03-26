package com.skyeye.school.groups.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@RedisCacheField(name = "school:Groups", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_groups")
@ApiModel(value = "学生分组实体类")
public class Groups extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("group_name")
    @ApiModelProperty(value = "组名称",required = "required")
    private String groupName;

    @TableField("state")
    @ApiModelProperty(value = "组状态(1已解散，0未解散)")
    private Integer state;

    @TableField("group_barcode")
    @ApiModelProperty(value = "加组二维码")
    private String groupBarcode;

    @TableField("gr_code_url")
    @Property(value = "扫码加组时的二维码地址")
    private String grCodeUrl;

    @TableField("groups_information_id")
    @ApiModelProperty(value = "学生分组信息id", required = "required")
    private String groupsInformationId;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生分组信息")
    private GroupsInformation groupsInformationMation;
}
