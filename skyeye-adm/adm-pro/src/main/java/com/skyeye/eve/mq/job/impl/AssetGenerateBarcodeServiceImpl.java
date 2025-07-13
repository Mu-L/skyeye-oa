/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.mq.job.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.assets.entity.Asset;
import com.skyeye.eve.assets.entity.AssetReport;
import com.skyeye.eve.assets.service.AssetReportService;
import com.skyeye.eve.assets.service.AssetService;
import com.skyeye.eve.assets.service.impl.AssetServiceImpl;
import com.skyeye.eve.coderule.service.ICodeRuleService;
import com.skyeye.eve.rest.barcode.BarCodeMation;
import com.skyeye.eve.rest.barcode.BarCodeMationBox;
import com.skyeye.eve.service.IBarCodeService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AssetGenerateBarcodeServiceImpl
 * @Description: 资产生成条形码的处理类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/27 21:47
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.asset-generate-barcode}",
    consumerGroup = "${topic.asset-generate-barcode}",
    selectorExpression = "${spring.profiles.active}")
public class AssetGenerateBarcodeServiceImpl implements RocketMQListener<String> {

    private static Logger LOGGER = LoggerFactory.getLogger(AssetGenerateBarcodeServiceImpl.class);

    @Autowired
    private AssetReportService assetReportService;

    @Autowired
    private AssetService assetService;

    @Value("${spring.application.name}")
    private String springApplicationName;

    @Autowired
    private IBarCodeService iBarCodeService;

    @Autowired
    private ICodeRuleService iCodeRuleService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    private static final Integer FILE_SAVE_PATH = FileConstants.FileUploadPath.ASSET_PURCHASE_GENERATE_BARCODE.getType()[0];

    @Override
    public void onMessage(String data) {
        LOGGER.info("start get Bar Code, data is {}", data);
        Map<String, Object> map = JSONUtil.toBean(data, null);
        List<Map<String, Object>> list = JSONUtil.toList(map.get("list").toString(), null);
        String className = map.get("className").toString();
        String userId = map.get("userId").toString();
        String tenantId = StrUtil.EMPTY;
        if (tenantEnable) {
            tenantId = map.get("tenantId").toString();
            TenantContext.setTenantId(tenantId);
        }
        List<String> assetIdList = list.stream().map(bean -> bean.get("assetId").toString()).distinct().collect(Collectors.toList());
        Map<String, Asset> assetMap = assetService.selectMapByIds(assetIdList);
        List<BarCodeMation> barCodeMationList = new ArrayList<>();
        // 生成条形码
        for (Map<String, Object> bean : list) {
            String assetId = bean.get("assetId").toString();
            Asset asset = assetMap.get(assetId);
            if (ObjectUtil.isEmpty(asset) || StrUtil.isEmpty(asset.getId())) {
                continue;
            }
            Integer number = Integer.parseInt(bean.get("operNumber").toString());
            List<String> codeList = iCodeRuleService.getNextCodeByClassName(AssetServiceImpl.class.getName(),
                BeanUtil.beanToMap(asset), number);
            List<AssetReport> assetReportList = new ArrayList<>();
            codeList.forEach(code -> {
                AssetReport assetReport = new AssetReport();
                assetReport.setAssetId(assetId);
                assetReport.setAssetNum(code);
                assetReportList.add(assetReport);
            });
            assetReportService.createEntity(assetReportList, userId);

            assetReportList.forEach(assetReport -> {
                String barCodePath = FileUtil.getImageBarCodePath(assetReport.getAssetNum(), FILE_SAVE_PATH);
                BarCodeMation barCodeMation = new BarCodeMation();
                barCodeMation.setCodeNum(assetReport.getAssetNum());
                barCodeMation.setObjectId(assetReport.getId());
                barCodeMation.setImagePath(barCodePath);
                barCodeMationList.add(barCodeMation);
            });
        }

        if (CollectionUtil.isEmpty(barCodeMationList)) {
            LOGGER.warn("generate asset report is Empty.");
            return;
        }
        BarCodeMationBox barCodeMationBox = new BarCodeMationBox();
        barCodeMationBox.setBarCodeList(barCodeMationList);
        barCodeMationBox.setCodeImplClass(className);
        barCodeMationBox.setSpringApplicationName(springApplicationName);
        iBarCodeService.writeBarCode(barCodeMationBox);

        LOGGER.info("end get Bar Code, data is {}", data);
    }
}
