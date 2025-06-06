package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.entity.ErpOrderHead;
import lombok.Data;

@Data
@TableName(value = "erp_depothead", autoResultMap = true)
@ApiModel("借出出库单实体类")
public class ProductLeadOutStock extends ErpOrderHead {
}
