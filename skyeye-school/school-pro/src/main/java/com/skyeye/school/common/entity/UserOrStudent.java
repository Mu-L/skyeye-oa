/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.common.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: UserOrStudent
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/23 22:17
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel(value = "聊天历史实体类")
public class UserOrStudent {

    @Property(value = "是教师还是学生，true为学生，false为教师")
    private Boolean userOrStudent;

    @Property(value = "信息")
    private Map<String, Object> dataMation;

}
