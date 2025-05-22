package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.product.entity.ProductLeadLink;

import java.util.List;

public interface ProductLeadLinkService extends SkyeyeLinkDataService<ProductLeadLink> {
    List<ProductLeadLink> selectByLeadLinkMation(String id);
}
