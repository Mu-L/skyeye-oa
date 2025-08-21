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
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.doc.code.entity.CodeVersion;
import lombok.Data;

/**
 * @ClassName: DocMemberVersion
 * @Description: 会员购买的版本实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/20 9:08
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "member_version", autoResultMap = true)
@ApiModel("会员购买的版本实体类")
public class DocMemberVersion extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "member_id")
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @TableField(exist = false)
    @Property(value = "会员信息")
    private DocMember memberMation;

    @TableField(value = "version_id")
    @ApiModelProperty(value = "版本id")
    private String versionId;

    @TableField(exist = false)
    @Property(value = "版本信息")
    private CodeVersion versionMation;

}
