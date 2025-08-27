/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.download.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.doc.code.entity.CodeSource;
import com.skyeye.doc.member.entity.DocMember;
import lombok.Data;

/**
 * @ClassName: Download
 * @Description: 下载历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/24 11:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "member_download", autoResultMap = true)
@ApiModel("下载历史实体类")
public class Download extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "member_id")
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @TableField(exist = false)
    @Property(value = "会员信息")
    private DocMember memberMation;

    @TableField(value = "code_source_id")
    @ApiModelProperty(value = "源代码id")
    private String codeSourceId;

    @TableField(exist = false)
    @Property(value = "源代码信息")
    private CodeSource codeSourceMation;

    @TableField("ip")
    @ApiModelProperty(value = " Ip地址")
    private String ip;

    @TableField("city")
    @ApiModelProperty(value = "下载的城市")
    private String city;

}
