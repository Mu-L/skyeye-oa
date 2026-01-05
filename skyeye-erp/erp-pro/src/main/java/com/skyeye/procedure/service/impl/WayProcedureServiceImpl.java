/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.procedure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.procedure.dao.WayProcedureDao;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.entity.WayProcedureChild;
import com.skyeye.procedure.entity.WorkProcedure;
import com.skyeye.procedure.service.WayProcedureChildService;
import com.skyeye.procedure.service.WayProcedureService;
import com.skyeye.procedure.service.WorkProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ErpWayProcedureServiceImpl
 * @Description: 工艺路线管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/5 21:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工艺路线管理", groupName = "工艺路线管理", allowDynamicAttrKey = false)
public class WayProcedureServiceImpl extends SkyeyeBusinessServiceImpl<WayProcedureDao, WayProcedure> implements WayProcedureService {

    @Autowired
    private WayProcedureChildService wayProcedureChildService;

    @Autowired
    private WorkProcedureService workProcedureService;

    @Override
    public QueryWrapper<WayProcedure> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<WayProcedure> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(WayProcedure::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        return beans;
    }

    @Override
    public String createEntity(WayProcedure entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(WayProcedure entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    public void validatorEntity(WayProcedure entity) {
        entity.setAllPrice(wayProcedureChildService.calcOrderAllTotalPrice(entity.getWorkProcedureList()));
    }

    @Override
    public void writePostpose(WayProcedure entity, String userId) {
        super.writePostpose(entity, userId);
        wayProcedureChildService.saveWayProcedure(entity.getId(), entity.getWorkProcedureList(), userId);
    }

    @Override
    public WayProcedure getDataFromDb(String id) {
        WayProcedure wayProcedure = super.getDataFromDb(id);
        // 查询工序信息
        wayProcedure.setWorkProcedureList(wayProcedureChildService.queryWayProcedureByWayId(wayProcedure.getId()));
        return wayProcedure;
    }

    @Override
    public List<WayProcedure> getDataFromDb(List<String> idList) {
        List<WayProcedure> wayProcedureList = super.getDataFromDb(idList);
        // 查询工序信息
        List<String> ids = wayProcedureList.stream().map(WayProcedure::getId).collect(Collectors.toList());
        Map<String, List<WayProcedureChild>> wayProcedureMap = wayProcedureChildService.queryWayProcedureByWayId(ids);
        wayProcedureList.forEach(bom -> {
            String id = bom.getId();
            bom.setWorkProcedureList(wayProcedureMap.get(id));
        });
        return wayProcedureList;
    }

    @Override
    public WayProcedure selectById(String id) {
        WayProcedure wayProcedure = super.selectById(id);
        // 设置工序信息
        workProcedureService.setDataMation(wayProcedure.getWorkProcedureList(), WayProcedureChild::getProcedureId);
        return wayProcedure;
    }

    @Override
    public List<WayProcedure> selectByIds(String... ids) {
        List<WayProcedure> wayProcedureList = super.selectByIds(ids);
        // 设置工序信息
        wayProcedureList.forEach(wayProcedure -> {
            workProcedureService.setDataMation(wayProcedure.getWorkProcedureList(), WayProcedureChild::getProcedureId);
        });
        return wayProcedureList;
    }

    @Override
    public void deletePostpose(String id) {
        wayProcedureChildService.deleteWayProcedureByWayId(id);
    }

    /**
     * 获取工艺下的工序列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryProcedureListByWayId(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        WayProcedure wayProcedure = selectById(id);
        List<WorkProcedure> workProcedureList = wayProcedure.getWorkProcedureList().stream()
            .map(wayProcedureChild -> wayProcedureChild.getProcedureMation())
            .collect(Collectors.toList());
        outputObject.setBeans(workProcedureList);
        outputObject.settotal(workProcedureList.size());
    }

    @Override
    public void queryAllPublishProcedureList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<WayProcedure> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WayProcedure::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(WayProcedure::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(WayProcedure::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(WayProcedure::getCreateTime));
        List<WayProcedure> wayProcedureList = list(queryWrapper);
        outputObject.setBeans(wayProcedureList);
        outputObject.settotal(wayProcedureList.size());
    }
}
