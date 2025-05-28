/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.entity.ErpOrderHead;
import lombok.Data;

/**
 * @ClassName: ProductLeadPut
 * @Description: 借出出库实体类
 * --otherState：这里表示【采购入库单入库状态】参考#DepotPutState
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/26 15:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_depothead", autoResultMap = true)
@ApiModel("借出出库实体类")
public class ProductLeadPut extends ErpOrderHead {

}
