/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.model.dao.WagesModelFieldDao;
import com.skyeye.eve.model.entity.WagesModelField;
import com.skyeye.eve.model.service.WagesModelFieldService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WagesModelFieldServiceImpl
 * @Description: 薪资模板关联的字段服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 14:03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "薪资模板关联的字段管理", groupName = "薪资模板关联的字段管理", manageShow = false)
public class WagesModelFieldServiceImpl extends SkyeyeBusinessServiceImpl<WagesModelFieldDao, WagesModelField> implements WagesModelFieldService {

    @Override
    public void deleteModelFieldByPId(String modelId) {
        QueryWrapper<WagesModelField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WagesModelField::getModelId), modelId);
        remove(queryWrapper);
    }

    @Override
    public void saveModelField(String modelId, List<WagesModelField> wagesModelFields) {
        deleteModelFieldByPId(modelId);
        if (CollectionUtil.isNotEmpty(wagesModelFields)) {
            for (WagesModelField modelField : wagesModelFields) {
                modelField.setModelId(modelId);
            }
            createEntity(wagesModelFields, StrUtil.EMPTY);
        }
    }

    @Override
    public List<WagesModelField> queryModelFieldByPId(String modelId) {
        QueryWrapper<WagesModelField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WagesModelField::getModelId), modelId);
        List<WagesModelField> wagesModelFields = list(queryWrapper);
        return wagesModelFields;
    }

    @Override
    public Map<String, List<WagesModelField>> queryModelFieldByPId(List<String> modelId) {
        QueryWrapper<WagesModelField> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(WagesModelField::getModelId), modelId);
        List<WagesModelField> wagesModelFields = list(queryWrapper);
        Map<String, List<WagesModelField>> listMap = wagesModelFields.stream()
            .collect(Collectors.groupingBy(WagesModelField::getModelId));
        return listMap;
    }
}
