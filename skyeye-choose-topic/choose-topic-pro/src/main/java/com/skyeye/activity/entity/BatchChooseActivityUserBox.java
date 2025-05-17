/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.entity;

import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ActivityUserList
 * @Description: 活动可参与用户列表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class BatchChooseActivityUserBox extends CommonInfo {

    @ApiModelProperty(value = "活动id", required = "required")
    private String activityId;

    @ApiModelProperty(value = "用户编号", required = "required,json")
    private List<String> userNoList;
}
