/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;
import com.skyeye.eve.chen.dao.DwAnChenCheckboxDao;
import com.skyeye.eve.chen.entity.DwAnChenCheckbox;
import com.skyeye.eve.chen.service.DwAnChenCheckboxService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnCheckboxServiceImpl
 * @Description: 答卷矩阵多选题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷矩阵多选题", groupName = "答卷矩阵多选题", manageShow = false)
public class DwAnChenCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<DwAnChenCheckboxDao, DwAnChenCheckbox> implements DwAnChenCheckboxService {

    @Override
    protected void createPostpose(DwAnChenCheckbox entity, String userId) {
        List<DwAnChenCheckbox> dFillblankAn = entity.getDwChenCheckboxAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void createPrepose(DwAnChenCheckbox entity) {
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongAnswerId), entity.getBelongAnswerId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongId), entity.getBelongId());
        List<DwAnChenCheckbox> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            remove(queryWrapper);
        }
    }

    @Override
    protected void updatePostpose(DwAnChenCheckbox entity, String userId) {
        List<DwAnChenCheckbox> chenCheckboxAn = entity.getDwChenCheckboxAn();
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnChenCheckbox> dwAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnChenCheckbox> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnChenCheckbox> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnChenCheckbox::getId).collect(Collectors.toSet());
        List<DwAnChenCheckbox> result = dwAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnChenCheckbox::getId).collect(Collectors.toList());
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
    public List<DwAnChenCheckbox> selectAnChenCheckboxByQuId(String id) {
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnChenCheckbox> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongId), surveyId);
        return list(queryWrapper);
    }


    @Override
    public DwAnChenCheckbox selectById(String id) {
        DwAnChenCheckbox dwAnChenCheckbox = super.selectById(id);
        String belongAnswerId = dwAnChenCheckbox.getBelongAnswerId();
        String belongId = dwAnChenCheckbox.getBelongId();
        String quId = dwAnChenCheckbox.getQuId();
        QueryWrapper<DwAnChenCheckbox> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        dwAnChenCheckbox.setDwChenCheckboxAn(list(queryWrapper1));
        return dwAnChenCheckbox;
    }

    @Override
    public void queryDwAnChenCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnChenCheckbox> dwAnChenCheckboxList = list(queryWrapper);
        outputObject.setBean(dwAnChenCheckboxList);
        outputObject.settotal(dwAnChenCheckboxList.size());
    }

    @Override
    public List<DwAnChenCheckbox> selectByQuId(String id) {
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnChenCheckbox>> selectByQuIdAndStuId(List<String> chenIds, String studentId, String id) {
        if (CollectionUtil.isEmpty(chenIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getQuId), chenIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getCreateId), studentId)
            .eq(MybatisPlusUtil.toColumns(DwAnChenCheckbox::getBelongAnswerId), id);
        Map<String, List<DwAnChenCheckbox>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnChenCheckbox::getQuId));
        return stringListMap;
    }
}
