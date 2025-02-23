package com.skyeye.eve.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.order.dao.DwAnOrderDao;
import com.skyeye.eve.order.entity.DwAnOrder;
import com.skyeye.eve.order.service.DwAnOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnOrderServiceImpl
 * @Description: 答卷 评分题接口服务层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 评分题", groupName = "答卷 评分题")
public class DwAnOrderServiceImpl extends SkyeyeBusinessServiceImpl<DwAnOrderDao, DwAnOrder> implements DwAnOrderService {

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

}