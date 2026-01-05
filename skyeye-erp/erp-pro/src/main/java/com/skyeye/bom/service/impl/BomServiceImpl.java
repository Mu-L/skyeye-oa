/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.bom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.dao.BomDao;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.entity.BomProcedureConsumables;
import com.skyeye.bom.service.BomChildService;
import com.skyeye.bom.service.BomProcedureConsumablesService;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.procedure.service.WayProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ErpBomServiceImpl
 * @Description: bom清单服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "bom清单管理", groupName = "bom清单管理")
public class BomServiceImpl extends SkyeyeBusinessServiceImpl<BomDao, Bom> implements BomService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private BomChildService bomChildService;

    @Autowired
    private BomProcedureConsumablesService bomProcedureConsumablesService;

    @Autowired
    private WayProcedureService wayProcedureService;

    @Override
    public QueryWrapper<Bom> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Bom> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);

        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        return beans;
    }

    @Override
    public void queryBomHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String versionNo = commonPageInfo.getCustomParamsMapStr("versionNo");
        if (StrUtil.isEmpty(versionNo)) {
            return;
        }

        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Bom> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getVersionNo), versionNo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Bom::getLargeVersion));
        List<Bom> bomList = list(queryWrapper);
        outputObject.setBeans(bomList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public String createEntity(Bom entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(Bom entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    public void validatorEntity(Bom entity) {
        super.validatorEntity(entity);
        BomChild checkMaterial = entity.getBomChildList().stream().filter(bomChild -> StrUtil.equals(entity.getMaterialId(), bomChild.getMaterialId())).findFirst().orElse(null);
        if (ObjectUtil.isNotEmpty(checkMaterial)) {
            throw new CustomException("子件清单中不能包含父件信息");
        }
        entity.getBomChildList().forEach(bomChild -> {
            String needNum = StrUtil.isEmpty(bomChild.getNeedNum())
                ? CommonNumConstants.NUM_ZERO.toString()
                : bomChild.getNeedNum();
            if (CalculationUtil.compareTo(needNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) == 0) {
                throw new CustomException("子件数量不能为0");
            }
        });
        entity.setConsumablesPrice(bomChildService.calcConsumablesPrice(entity.getBomChildList()));
        entity.setProcedurePrice(bomChildService.calcProcedurePrice(entity.getBomChildList()));
        entity.setAllPrice(CalculationUtil.add(entity.getConsumablesPrice(), entity.getProcedurePrice(), CommonNumConstants.NUM_TWO));
    }

    @Override
    protected void writePostpose(Bom entity, String userId) {
        super.writePostpose(entity, userId);

        // 在保存子件清单得工序耗材时，已经把（BOM层面的 + BOM子件下的）所有得工序耗材都先删除了，所以下面保存工序耗材列表（BOM层面的）无需在进行删除
        entity.getBomChildList().forEach(bomChild -> bomChild.setBomId(entity.getId()));
        bomChildService.createEntity(entity.getBomChildList(), userId);

        // 保存工序耗材列表（BOM层面的）
        if (CollectionUtil.isNotEmpty(entity.getProcedureConsumablesList())) {
            entity.getProcedureConsumablesList().forEach(consumables -> {
                consumables.setBomId(entity.getId());
                consumables.setBomChildId(null); // BOM层面的耗材，bomChildId为空
            });
            bomProcedureConsumablesService.createEntity(entity.getProcedureConsumablesList(), userId);
        }
    }

    @Override
    public Bom getDataFromDb(String id) {
        Bom bom = super.getDataFromDb(id);
        // 设置子件清单信息
        bom.setBomChildList(bomChildService.queryBomChildByBomId(bom.getId()));
        // 设置工序耗材列表信息（BOM层面的）
        bom.setProcedureConsumablesList(bomProcedureConsumablesService.queryListByBomId(bom.getId()));
        return bom;
    }

    @Override
    protected List<Bom> getDataFromDb(List<String> idList) {
        List<Bom> bomList = super.getDataFromDb(idList);
        List<String> ids = bomList.stream().map(Bom::getId).collect(Collectors.toList());
        // 设置子件清单信息
        Map<String, List<BomChild>> bomChildMap = bomChildService.queryBomChildByBomId(ids);
        // 设置工序耗材列表信息（BOM层面的）
        Map<String, List<BomProcedureConsumables>> consumablesMap = bomProcedureConsumablesService.queryListByBomIds(ids);
        bomList.forEach(bom -> {
            String id = bom.getId();
            bom.setBomChildList(bomChildMap.get(id));
            bom.setProcedureConsumablesList(consumablesMap.get(id));
        });
        return bomList;
    }

    @Override
    public Bom selectById(String id) {
        Bom bom = super.selectById(id);

        // 查询工艺信息
        wayProcedureService.setDataMation(bom, Bom::getWayProcedureId);
        wayProcedureService.setDataMation(bom.getBomChildList(), BomChild::getWayProcedureId);
        bom.getBomChildList().forEach(bomChild -> {
            bomChild.setOpen(true);
        });
        // 设置产品/规格信息
        materialService.setDataMation(bom.getBomChildList(), BomChild::getMaterialId);
        materialNormsService.setDataMation(bom.getBomChildList(), BomChild::getNormsId);
        materialService.setDataMation(bom, Bom::getMaterialId);
        materialNormsService.setDataMation(bom, Bom::getNormsId);
        // 查询工序耗材信息（BOM子件下的）
        bom.getBomChildList().forEach(bomChild -> {
            if (CollectionUtil.isNotEmpty(bomChild.getProcedureConsumablesList())) {
                materialService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
                materialNormsService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
            }
        });
        // 查询工序耗材信息（BOM层面的）
        if (CollectionUtil.isNotEmpty(bom.getProcedureConsumablesList())) {
            materialService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
            materialNormsService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
        }
        return bom;
    }

    @Override
    public List<Bom> selectByIds(String... ids) {
        List<Bom> bomList = super.selectByIds(ids);

        // 查询工艺信息
        wayProcedureService.setDataMation(bomList, Bom::getWayProcedureId);

        // 查询工艺信息
        bomList.forEach(bom -> {
            wayProcedureService.setDataMation(bom.getBomChildList(), BomChild::getWayProcedureId);
            bom.getBomChildList().forEach(bomChild -> {
                bomChild.setOpen(true);
            });
            // 设置产品/规格信息
            materialService.setDataMation(bom.getBomChildList(), BomChild::getMaterialId);
            materialNormsService.setDataMation(bom.getBomChildList(), BomChild::getNormsId);
            // 查询工序耗材信息（BOM子件下的）
            bom.getBomChildList().forEach(bomChild -> {
                if (CollectionUtil.isNotEmpty(bomChild.getProcedureConsumablesList())) {
                    materialService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
                    materialNormsService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
                }
            });
            // 查询工序耗材信息（BOM层面的）
            if (CollectionUtil.isNotEmpty(bom.getProcedureConsumablesList())) {
                materialService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
                materialNormsService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
            }
        });
        materialService.setDataMation(bomList, Bom::getMaterialId);
        materialNormsService.setDataMation(bomList, Bom::getNormsId);

        return bomList;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子件清单以及所有工序耗材
        bomChildService.deleteBomChildByBomId(id);
    }

    /**
     * 根据规格id获取方案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryBomListByNormsId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String normsId = map.get("normsId").toString();
        if (StrUtil.isEmpty(normsId)) {
            return;
        }
        Map<String, List<Bom>> listMap = getBomListByNormsId(normsId);
        List<Bom> bomList = listMap.get(normsId);
        if (CollectionUtil.isEmpty(bomList)) {
            return;
        }
        materialNormsService.setDataMation(bomList, Bom::getNormsId);

        outputObject.setBeans(bomList);
        outputObject.settotal(bomList.size());
    }

    @Override
    public Map<String, List<Bom>> getBomListByNormsId(String... normsId) {
        List<String> normsIdList = Arrays.asList(normsId);
        if (CollectionUtil.isEmpty(normsIdList)) {
            return cn.hutool.core.map.MapUtil.newHashMap();
        }
        QueryWrapper<Bom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.in(MybatisPlusUtil.toColumns(Bom::getNormsId), normsIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Bom::getCreateTime));
        List<Bom> bomList = list(queryWrapper);
        return bomList.stream().collect(Collectors.groupingBy(Bom::getNormsId));
    }

    /**
     * 根据商品信息以及bom方案信息获取商品树---用于生产模块
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String proList = map.get("proList").toString();
        // 处理数据
        List<Map<String, Object>> beans = JSONUtil.toList(proList, null);
        // 设置产品/规格信息
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        // 获取方案下的子件
        List<String> bomIds = beans.stream()
            .filter(bean -> !MapUtil.checkKeyIsNull(bean, "bomId") && StrUtil.isNotEmpty(bean.get("bomId").toString()))
            .map(bean -> bean.get("bomId").toString()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bomIds)) {
            Map<String, Bom> bomMap = selectMapByIds(bomIds);
            List<BomChild> tempList = new ArrayList<>();
            beans.forEach(bean -> {
                String bomId = bean.get("bomId").toString();
                if (StrUtil.isNotEmpty(bomId)) {
                    if (!bomMap.containsKey(bomId)) {
                        return;
                    }
                    tempList.addAll(bomMap.get(bomId).getBomChildList());
                }
            });
            if (CollectionUtil.isNotEmpty(tempList)) {
                beans.addAll(tempList.stream()
                    .map(temp -> BeanUtil.beanToMap(temp)).collect(Collectors.toList()));
            }
        }
        outputObject.setBeans(beans);
    }

}
