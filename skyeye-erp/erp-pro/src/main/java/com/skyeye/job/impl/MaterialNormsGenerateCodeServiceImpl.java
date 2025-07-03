/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.coderule.service.ICodeRuleService;
import com.skyeye.eve.rest.barcode.BarCodeMation;
import com.skyeye.eve.rest.barcode.BarCodeMationBox;
import com.skyeye.eve.service.IBarCodeService;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.MaterialService;
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
 * @ClassName: MaterialNormsGenerateCodeServiceImpl
 * @Description: 批量生成条形码的处理类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 8:47
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.material-norms-code-generate-barcode}",
    consumerGroup = "${topic.material-norms-code-generate-barcode}",
    selectorExpression = "${spring.profiles.active}")
public class MaterialNormsGenerateCodeServiceImpl implements RocketMQListener<String> {

    private static Logger LOGGER = LoggerFactory.getLogger(MaterialNormsGenerateCodeServiceImpl.class);

    @Autowired
    private IBarCodeService iBarCodeService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsCodeService materialNormsCodeService;

    @Autowired
    private ICodeRuleService iCodeRuleService;

    @Value("${spring.application.name}")
    private String springApplicationName;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        LOGGER.info("start material norms get Bar Code, data is {}", data);
        Map<String, Object> map = JSONUtil.toBean(data, null);
        List<Map<String, Object>> list = JSONUtil.toList(map.get("list").toString(), null);
        String className = map.get("className").toString();
        String tenantId = StrUtil.EMPTY;
        if (tenantEnable) {
            tenantId = map.get("tenantId").toString();
            TenantContext.setTenantId(tenantId);
        }

        List<String> materialIdList = list.stream().map(bean -> bean.get("materialId").toString()).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        // 生成条形码的总数量
        Integer number = 0;
        for (Map<String, Object> bean : list) {
            String materialId = bean.get("materialId").toString();
            Material material = materialMap.get(materialId);
            if (material.getItemCode() == MaterialItemCode.DISABLE.getKey()) {
                continue;
            }
            number += Integer.parseInt(bean.get("operNumber").toString());
        }
        if (number == 0) {
            LOGGER.warn("material norms code number is Zero!!");
            return;
        }
        // 获取指定数量的编码
        List<String> codeList = iCodeRuleService.getNextCodeByClassName(className,
            MapUtil.newHashMap(), number);
        if (CollectionUtil.isEmpty(codeList)) {
            LOGGER.warn("codeList is Null");
            return;
        }
        // 保存条形码信息
        List<MaterialNormsCode> materialNormsCodeList = new ArrayList<>();
        String createTime = DateUtil.getTimeAndToString();
        int startIndex = 0;
        for (Map<String, Object> bean : list) {
            String materialId = bean.get("materialId").toString();
            String normsId = bean.get("normsId").toString();
            Material material = materialMap.get(materialId);
            if (material.getItemCode() == MaterialItemCode.DISABLE.getKey()) {
                continue;
            }
            int operNumber = Integer.parseInt(bean.get("operNumber").toString());
            for (int i = startIndex; i < startIndex + operNumber; i++) {
                MaterialNormsCode materialNormsCode = new MaterialNormsCode();
                materialNormsCode.setCodeNum(codeList.get(i));
                materialNormsCode.setMaterialId(materialId);
                materialNormsCode.setNormsId(normsId);
                materialNormsCode.setInDepot(MaterialNormsCodeInDepot.NOT_IN_STOCK.getKey());
                materialNormsCode.setCreateTime(createTime);
                materialNormsCodeList.add(materialNormsCode);
            }
            startIndex += operNumber;
        }
        materialNormsCodeService.createEntity(materialNormsCodeList, StrUtil.EMPTY);

        // 生成条形码logo
        List<BarCodeMation> barCodeMationList = new ArrayList<>();
        materialNormsCodeList.forEach(materialNormsCode -> {
            String barCodePath = FileUtil.getImageBarCodePath(materialNormsCode.getCodeNum(),
                FileConstants.FileUploadPath.ERP_MATERIAL_NORMS_CODE.getType()[0]);
            BarCodeMation barCodeMation = new BarCodeMation();
            barCodeMation.setCodeNum(materialNormsCode.getCodeNum());
            barCodeMation.setObjectId(materialNormsCode.getId());
            barCodeMation.setImagePath(barCodePath);
            barCodeMationList.add(barCodeMation);
        });

        BarCodeMationBox barCodeMationBox = new BarCodeMationBox();
        barCodeMationBox.setBarCodeList(barCodeMationList);
        barCodeMationBox.setCodeImplClass(className);
        barCodeMationBox.setSpringApplicationName(springApplicationName);
        iBarCodeService.writeBarCode(barCodeMationBox);

        LOGGER.info("end material norms get Bar Code, data is {}", data);
    }
}
