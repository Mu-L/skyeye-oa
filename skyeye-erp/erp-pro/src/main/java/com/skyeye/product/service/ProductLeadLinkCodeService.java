package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.product.entity.ProductLeadLinkCode;

import java.util.List;
import java.util.Map;

public interface ProductLeadLinkCodeService extends SkyeyeBusinessService<ProductLeadLinkCode> {
    void saveList(String pId, List<ProductLeadLinkCode> productLeadLinkCodes);

    void deleteByParentId(String parentId);

    Map<String, List<ProductLeadLinkCode>> selectByParentIds(List<String> parentIds);
}
