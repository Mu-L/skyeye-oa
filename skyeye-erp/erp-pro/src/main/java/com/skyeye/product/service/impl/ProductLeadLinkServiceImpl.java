package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.dao.ProductLeadLinkDao;
import com.skyeye.product.entity.ProductLeadLink;
import com.skyeye.product.entity.ProductLeadLinkCode;
import com.skyeye.product.service.ProductLeadLinkCodeService;
import com.skyeye.product.service.ProductLeadLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@SkyeyeService(name = "借出出库申请关联的产品价格信息", groupName = "借出出库申请关联的产品价格信息", manageShow = false)
public class ProductLeadLinkServiceImpl extends SkyeyeLinkDataServiceImpl<ProductLeadLinkDao, ProductLeadLink> implements ProductLeadLinkService {

    @Autowired
    private ProductLeadLinkCodeService productLeadLinkCodeService;

    @Override
    public void saveLinkList(String pId, List<ProductLeadLink> beans) {
        super.saveLinkList(pId, beans);
        // 保存关联的条形码
        List<ProductLeadLinkCode> productLeadLinkCodes = new ArrayList<>();
        beans.forEach(bean -> {
            if (CollectionUtil.isNotEmpty(bean.getProductLeadCodeList())) {
                bean.getProductLeadCodeList().forEach(normsCode -> {
                    ProductLeadLinkCode assetPurchaseLinkCode = new ProductLeadLinkCode();
                    assetPurchaseLinkCode.setNormsCode(normsCode);
                    assetPurchaseLinkCode.setArticleId(bean.getArticleId());
                    productLeadLinkCodes.add(assetPurchaseLinkCode);
                });
            }
        });
        if (CollectionUtil.isNotEmpty(productLeadLinkCodes)) {
            productLeadLinkCodeService.saveList(pId, productLeadLinkCodes);
        }
    }

    @Override
    public List<ProductLeadLink> selectByLeadLinkMation(String id) {
        QueryWrapper<ProductLeadLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadLink::getParentId), id);
        List<ProductLeadLink> list = list(queryWrapper);
        List<String> parentIds = list.stream().map(ProductLeadLink::getId).collect(Collectors.toList());
        Map<String, List<ProductLeadLinkCode>> linkCodeMap = productLeadLinkCodeService.selectByParentIds(parentIds);
        for (ProductLeadLink productLeadLink : list) {
            productLeadLink.setLeadCodeMation(linkCodeMap.get(productLeadLink.getId()));
        }
        return list;
    }
}
