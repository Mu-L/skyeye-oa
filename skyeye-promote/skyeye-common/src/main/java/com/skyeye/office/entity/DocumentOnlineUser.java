package com.skyeye.office.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @ClassName: DocumentOnlineUser
 * @Description: 文档在线用户实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Data
@TableName(value = "sys_document_online_user")
@EqualsAndHashCode(callSuper = false)
@ApiModel("文档在线用户实体类")
public class DocumentOnlineUser extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @ApiModelProperty(value = "文档ID", required = "required")
    private String documentId;

    @ApiModelProperty(value = "用户ID", required = "required")
    private String userId;

    @ApiModelProperty(value = "登录时间", required = "required")
    private Date loginTime;

    @ApiModelProperty(value = "最后活跃时间", required = "required")
    private Date lastActiveTime;
} 