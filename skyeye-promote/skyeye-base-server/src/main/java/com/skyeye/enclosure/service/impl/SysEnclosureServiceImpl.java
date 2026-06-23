/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enclosure.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.catalog.entity.Catalog;
import com.skyeye.catalog.entity.CatalogBusinessQueryDo;
import com.skyeye.catalog.service.CatalogService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.BytesUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.enclosure.dao.SysEnclosureDao;
import com.skyeye.enclosure.entity.Enclosure;
import com.skyeye.enclosure.service.SysEnclosureService;
import com.skyeye.exception.CustomException;
import com.skyeye.sdk.catalog.service.CatalogSdkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysEnclosureServiceImpl extends SkyeyeBusinessServiceImpl<SysEnclosureDao, Enclosure> implements SysEnclosureService, CatalogSdkService {

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private CatalogService catalogService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CatalogBusinessQueryDo pageInfo = inputObject.getParams(CatalogBusinessQueryDo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = sysEnclosureDao.queryEnclosureList(pageInfo);
        beans.forEach(bean -> {
            String size = BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("size").toString()));
            bean.put("size", size);
        });
        catalogService.setMationForMap(beans, "catalogId", "catalogMation");
        return beans;
    }

    /**
     * 根据ids(逗号隔开)获取多个附件信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryEnclosureInfo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = sysEnclosureDao.queryEnclosureInfo(map.get("enclosureInfoIds").toString());
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取我的附件树
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryEnclosureTree(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        List<Map<String, Object>> enclosureList = sysEnclosureDao.queryEnclosureTree(userId);
        // 获取目录
        List<Catalog> catalogs = catalogService.getCatalogs(StrUtil.EMPTY, getServiceClassName(), true, userId);
        for (Catalog catalog : catalogs) {
            enclosureList.add(BeanUtil.beanToMap(catalog));
        }

        enclosureList = enclosureList.stream()
            .sorted(Comparator.comparing(bean -> bean.get("objectType").toString(), Comparator.naturalOrder())).collect(Collectors.toList());
        // 转为树
        List<Tree<String>> treeNodes = TreeUtil.build(enclosureList, String.valueOf(CommonNumConstants.NUM_ZERO), new TreeNodeConfig(),
            (treeNode, tree) -> {
                tree.setId(treeNode.get("id").toString());
                tree.setParentId(treeNode.get("parentId").toString());
                tree.setName(treeNode.get("name").toString());
                String objectType = treeNode.get("objectType").toString();
                if (StrUtil.equals(objectType, "catalog")) {
                    tree.putExtra("isParent", true);
                }
                tree.putExtra("objectType", objectType);
            });
        if (CollectionUtil.isNotEmpty(treeNodes)) {
            outputObject.setBeans(treeNodes);
            outputObject.settotal(treeNodes.size());
        }
    }

    /**
     * 移动附件到指定分类
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void moveEnclosureToCatalog(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String ids = params.get("ids").toString();
        String catalogId = params.get("catalogId").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        List<String> idList = Arrays.stream(ids.split(","))
            .map(String::trim).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            throw new CustomException("请选择要移动的附件.");
        }

        if (!StrUtil.equals(catalogId, String.valueOf(CommonNumConstants.NUM_ZERO))) {
            Catalog catalog = catalogService.selectById(catalogId);
            if (catalog == null) {
                throw new CustomException("目标分类不存在.");
            }
        }

        UpdateWrapper<Enclosure> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, idList);
        updateWrapper.eq(MybatisPlusUtil.toColumns(Enclosure::getCreateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Enclosure::getCatalog), catalogId);
        update(updateWrapper);
        clearCache(idList);
    }

}
