/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.doc.document.entity.Document;
import com.skyeye.doc.member.entity.DocMember;
import lombok.Data;

/**
 * 文档浏览历史
 */
@Data
@TableName(value = "document_browse_history", autoResultMap = true)
@ApiModel("文档浏览历史")
public class DocumentBrowseHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "member_id")
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @TableField(exist = false)
    @Property(value = "会员信息")
    private DocMember memberMation;

    @TableField(value = "document_id")
    @ApiModelProperty(value = "文档id")
    private String documentId;

    @TableField(exist = false)
    @Property(value = "文档信息")
    private Document documentMation;

    @TableField(value = "view_count")
    @Property(value = "浏览次数")
    private Integer viewCount;

    @TableField(value = "last_view_time")
    @Property(value = "最近浏览时间")
    private String lastViewTime;

    @TableField("ip")
    @Property(value = "IP地址")
    private String ip;

    @TableField("city")
    @Property(value = "城市")
    private String city;

}
