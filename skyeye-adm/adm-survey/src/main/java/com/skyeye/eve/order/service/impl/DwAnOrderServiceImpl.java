package com.skyeye.eve.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.multifllblank.entity.DwAnFillblank;
import com.skyeye.eve.order.dao.DwAnOrderDao;
import com.skyeye.eve.order.entity.DwAnOrder;
import com.skyeye.eve.order.service.DwAnOrderService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnOrderServiceImpl
 * @Description: 答卷 评分题接口服务层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 排序题", groupName = "答卷 排序题")
public class DwAnOrderServiceImpl extends SkyeyeBusinessServiceImpl<DwAnOrderDao, DwAnOrder> implements DwAnOrderService {

    @Override
    protected void createPostpose(DwAnOrder entity, String userId) {
        List<DwAnOrder> dFillblankAn = entity.getOrderByAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void createPrepose(DwAnOrder entity) {
        QueryWrapper<DwAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongAnswerId), entity.getBelongAnswerId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongId), entity.getBelongId());
        List<DwAnOrder> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            remove(queryWrapper);
        }
    }

    @Override
    protected void updatePostpose(DwAnOrder entity, String userId) {
        List<DwAnOrder> chenCheckboxAn = entity.getOrderByAn();
        QueryWrapper<DwAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnOrder> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnOrder> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnOrder> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnOrder::getId).collect(Collectors.toSet());
        List<DwAnOrder> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnOrder::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(TodeleteIds)) {
            deleteById(TodeleteIds);
        }
        if (CollectionUtil.isNotEmpty(NoIdChenCheckbox)) {
            super.createEntity(NoIdChenCheckbox, userId);
        }
        if (CollectionUtil.isNotEmpty(YesIdChenCheckbox)) {
            super.updateEntity(YesIdChenCheckbox, userId);
        }
    }

    @Override
    public DwAnOrder selectById(String id) {
        DwAnOrder examAnDfillblank = super.selectById(id);
        String belongAnswerId = examAnDfillblank.getBelongAnswerId();
        String belongId = examAnDfillblank.getBelongId();
        String quId = examAnDfillblank.getQuId();
        QueryWrapper<DwAnOrder> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnOrder::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        examAnDfillblank.setOrderByAn(list(queryWrapper1));
        return examAnDfillblank;
    }

    @Override
    public void queryDwAnOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnOrder> dwAnOrderList = list(queryWrapper);
        outputObject.setBean(dwAnOrderList);
        outputObject.settotal(dwAnOrderList.size());
    }

    @Override
    public List<DwAnOrder> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnOrder> selectAnOrderByQuId(String id) {
        QueryWrapper<DwAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnOrder::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnOrder>> selectByQuIdAndStuId(List<String> orderQuIds, String studentId, String id) {
        if (CollectionUtil.isEmpty(orderQuIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnOrder::getQuId), orderQuIds)
            .eq(MybatisPlusUtil.toColumns(DwAnOrder::getCreateId), studentId)
            .eq(MybatisPlusUtil.toColumns(DwAnOrder::getBelongAnswerId), id);
        Map<String, List<DwAnOrder>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnOrder::getQuId));
        return stringListMap;
    }

}