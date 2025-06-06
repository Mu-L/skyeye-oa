package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.product.entity.ProductLeadChild;

import java.util.List;

public interface ProductLeadChildService extends SkyeyeBusinessService<ProductLeadChild> {

    String calcOrderAllTotalPrice(List<ProductLeadChild> productLeadChildList);

    void saveList(String id, List<ProductLeadChild> productLeadChildList);

    void deleteByParentId(String id);

    List<ProductLeadChild> selectProductLeadChildById(String id);
}
