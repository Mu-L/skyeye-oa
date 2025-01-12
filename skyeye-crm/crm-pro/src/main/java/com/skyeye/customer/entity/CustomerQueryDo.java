/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.customer.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.search.CommonPageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: CustomerQueryDo
 * @Description: 客户列表查询条件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/24 16:23
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("客户列表查询条件实体类")
public class CustomerQueryDo extends CommonPageInfo implements Serializable {

    @ApiModelProperty(value = "根据不同的参数查询不同类型的客户。例如：我创建的，我负责的等")
    private String type;

    /**
     * 未跟单天数
     */
    private String noDocumentaryDayNum;

}
