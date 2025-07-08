package com.skyeye.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.classenum.ProductLeadOrReturnFromType;
import com.skyeye.product.dao.ProductLeadDao;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.entity.ProductLeadChild;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadChildService;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "借出申请", groupName = "借出申请", flowable = true)
public class ProductLeadServiceImpl extends SkyeyeFlowableServiceImpl<ProductLeadDao, ProductLead> implements ProductLeadService {

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private ProductLeadChildService productLeadChildService;

    @Autowired
    private ProductLeadOutStockService productLeadOutStockService;

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageData(inputObject);
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public void createPrepose(ProductLead entity) {
        super.createPrepose(entity);
        getTotalPrice(entity);
    }

    @Override
    protected void updatePrepose(ProductLead entity) {
        super.updatePrepose(entity);
        getTotalPrice(entity);
    }

    private void getTotalPrice(ProductLead entity) {
        String totalPrice = productLeadChildService.calcOrderAllTotalPrice(entity.getErpOrderItemList());
        entity.setTotalPrice(totalPrice);
    }

    @Override
    public void writeChild(ProductLead entity, String userId) {
        productLeadChildService.saveList(entity.getId(), entity.getErpOrderItemList());
        super.writeChild(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        productLeadChildService.deleteByParentId(id);
    }

    @Override
    public ProductLead selectById(String id) {
        ProductLead productLead = super.selectById(id);
        List<ProductLeadChild> productLeadChildren = productLeadChildService.selectProductLeadChildById(id);
        productLead.setErpOrderItemList(productLeadChildren);
        return productLead;
    }

    @Override
    public void productLeadToContractOutStock(InputObject inputObject, OutputObject outputObject) {
        ProductLeadOutStock productLeadOutStock = inputObject.getParams(ProductLeadOutStock.class);
        productLeadOutStock.setFromId(productLeadOutStock.getId());
        productLeadOutStock.setFromTypeId(ProductLeadOrReturnFromType.LOANOUT.getKey());
        productLeadOutStock.setId(null);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        productLeadOutStockService.createEntity(productLeadOutStock, userId);
    }

}
