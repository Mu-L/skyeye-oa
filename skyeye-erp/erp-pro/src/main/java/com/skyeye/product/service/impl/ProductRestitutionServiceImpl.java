package com.skyeye.product.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.product.dao.ProductRestitutionDao;
import com.skyeye.product.entity.ProductRestitution;
import com.skyeye.product.service.ProductRestitutionService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "归还入库", groupName = "归还入库", manageShow = false)
public class ProductRestitutionServiceImpl extends SkyeyeBusinessServiceImpl<ProductRestitutionDao, ProductRestitution> implements ProductRestitutionService {


}
