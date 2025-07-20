package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.product.entity.ProductLeadChild;
import com.skyeye.product.entity.ProductReturnChild;

import java.util.List;

public interface ProductReturnChildService extends SkyeyeBusinessService<ProductReturnChild> {

    String calcOrderAllTotalPrice(List<ProductReturnChild> erpOrderItemList);

    void saveList(String id, List<ProductReturnChild> erpOrderItemList);

    void deleteByParentId(String id);

    List<ProductReturnChild> selectProductLeadChildById(String id);

    List<ProductReturnChild> selectProductLeadChildByIdList(List<String> returnIds);

    List<ProductReturnChild> selectByPId(String id);

}
