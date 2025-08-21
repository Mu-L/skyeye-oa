/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.doc.member.enums.MemberPlanetEnterEnum;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DocMember
 * @Description: 文档会员实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 22:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField(value = {"phone"})
@RedisCacheField(name = "code:docMember")
@TableName(value = "member", autoResultMap = true)
@ApiModel("文档会员实体类")
public class DocMember extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "姓名", required = "required")
    private String name;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(value = "phone")
    @ApiModelProperty(value = "联系电话", required = "required,phone")
    private String phone;

    @TableField(value = "password")
    @ApiModelProperty(value = "密码", required = "required")
    private String password;

    @TableField("pwd_num_enc")
    @Property(value = "用户密码加密次数")
    private Integer pwdNumEnc;

    @TableField(value = "join_time")
    @ApiModelProperty(value = "加入时间", required = "required")
    private String joinTime;

    @TableField(value = "planet_num")
    @ApiModelProperty(value = "星球编号")
    private String planetNum;

    @TableField(value = "planet_enter")
    @ApiModelProperty(value = "成为会员的方式", enumClass = MemberPlanetEnterEnum.class, required = "required,num")
    private Integer planetEnter;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "num", defaultValue = "1")
    private Integer enabled;

    @TableField(exist = false)
    @Property(value = "用户token")
    private String userToken;

    @TableField(value = "level_id")
    @ApiModelProperty(value = "会员等级id")
    private String levelId;

    @TableField(exist = false)
    @Property(value = "会员等级信息")
    private DocMemverLevel levelMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "会员购买的版本id", required = "json")
    private List<String> versionIds;

    @TableField(exist = false)
    @Property(value = "会员购买的版本信息")
    private List<DocMemberVersion> versionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "会员购买的源代码包id", required = "json")
    private List<String> packageIds;

    @TableField(exist = false)
    @Property(value = "会员购买的源代码包信息")
    private List<DocMemberPackage> packageList;
}
