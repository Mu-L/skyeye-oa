/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.WagesConstant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.ApplicableObjectsType;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.field.entity.FieldType;
import com.skyeye.eve.field.service.WagesFieldTypeService;
import com.skyeye.eve.model.classenum.WagesModelFieldType;
import com.skyeye.eve.model.dao.WagesModelDao;
import com.skyeye.eve.model.entity.ModelApplicableObjects;
import com.skyeye.eve.model.entity.WagesModel;
import com.skyeye.eve.model.entity.WagesModelField;
import com.skyeye.eve.model.service.ModelApplicableObjectsService;
import com.skyeye.eve.model.service.WagesModelFieldService;
import com.skyeye.eve.model.service.WagesModelService;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WagesModelServiceImpl
 * @Description: 薪资模板服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 11:19
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "薪资模板", groupName = "薪资模板")
public class WagesModelServiceImpl extends SkyeyeBusinessServiceImpl<WagesModelDao, WagesModel> implements WagesModelService {

    @Autowired
    private ModelApplicableObjectsService modelApplicableObjectsService;

    @Autowired
    private WagesModelFieldService wagesModelFieldService;

    @Autowired
    private WagesFieldTypeService wagesFieldTypeService;

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryWagesModelList(pageInfo);
        return beans;
    }

    @Override
    public void writePostpose(WagesModel entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存适用对象
        modelApplicableObjectsService.saveApplicableObjects(entity.getId(), entity.getApplicableObjectsList());
        // 保存关联的字段
        wagesModelFieldService.saveModelField(entity.getId(), entity.getWagesModelFieldList());
    }

    @Override
    public void deletePostpose(String id) {
        // 删除适用对象
        modelApplicableObjectsService.deleteApplicableObjectsByPId(id);
        // 删除关联的字段
        wagesModelFieldService.deleteModelFieldByPId(id);
    }

    @Override
    public WagesModel getDataFromDb(String id) {
        WagesModel wagesModel = super.getDataFromDb(id);
        // 适用对象信息
        List<ModelApplicableObjects> applicableObjectsList = modelApplicableObjectsService.queryApplicableObjectsByPId(id);
        wagesModel.setApplicableObjectsList(applicableObjectsList);
        // 关联的字段
        List<WagesModelField> wagesModelFields = wagesModelFieldService.queryModelFieldByPId(id);
        wagesModel.setWagesModelFieldList(wagesModelFields);
        return wagesModel;
    }

    @Override
    protected List<WagesModel> getDataFromDb(List<String> idList) {
        List<WagesModel> wagesModels = super.getDataFromDb(idList);
        // 适用对象信息
        Map<String, List<ModelApplicableObjects>> applicableObjectsMap = modelApplicableObjectsService.queryApplicableObjectsByPId(idList);
        wagesModels.forEach(wagesModel -> {
            wagesModel.setApplicableObjectsList(applicableObjectsMap.get(wagesModel.getId()));
        });
        // 关联的字段
        Map<String, List<WagesModelField>> modelFieldMap = wagesModelFieldService.queryModelFieldByPId(idList);
        wagesModels.forEach(wagesModel -> {
            wagesModel.setWagesModelFieldList(modelFieldMap.get(wagesModel.getId()));
        });
        return wagesModels;
    }

    @Override
    public WagesModel selectById(String id) {
        WagesModel wagesModel = super.selectById(id);
        // 设置适用对象信息
        List<ModelApplicableObjects> applicableObjectsList = wagesModel.getApplicableObjectsList();
        setApplicable(applicableObjectsList);
        // 设置关联的字段信息
        Map<String, FieldType> fieldTypeMap = wagesFieldTypeService.queryAllFieldTypeMap();
        setFieldType(wagesModel, fieldTypeMap);
        return wagesModel;
    }

    @Override
    public List<WagesModel> selectByIds(String... ids) {
        List<WagesModel> wagesModels = super.selectByIds(ids);
        // 设置适用对象信息
        wagesModels.forEach(wagesModel -> {
            List<ModelApplicableObjects> applicableObjectsList = wagesModel.getApplicableObjectsList();
            setApplicable(applicableObjectsList);
        });

        // 设置关联的字段信息
        Map<String, FieldType> fieldTypeMap = wagesFieldTypeService.queryAllFieldTypeMap();
        wagesModels.forEach(wagesModel -> {
            setFieldType(wagesModel, fieldTypeMap);
        });
        return wagesModels;
    }

    private void setApplicable(List<ModelApplicableObjects> applicableObjectsList) {
        if (CollectionUtil.isNotEmpty(applicableObjectsList)) {
            Map<Integer, List<ModelApplicableObjects>> listMap = applicableObjectsList.stream()
                .collect(Collectors.groupingBy(ModelApplicableObjects::getObjectType));
            listMap.forEach((key, value) -> {
                List<String> ids = value.stream().map(ModelApplicableObjects::getObjectId).collect(Collectors.toList());
                if (ApplicableObjectsType.STAFF.getKey().equals(key)) {
                    // 员工
                    Map<String, Map<String, Object>> staffMaps = iAuthUserService.queryUserMationListByStaffIds(ids);
                    setObjectMation(applicableObjectsList, staffMaps, key);
                } else if (ApplicableObjectsType.DEPARTMENT.getKey().equals(key)) {
                    // 部门
                    String departmentIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
                    Map<String, Map<String, Object>> departMent = iDepmentService.queryDataMationForMapByIds(departmentIdStr);
                    setObjectMation(applicableObjectsList, departMent, key);
                } else if (ApplicableObjectsType.COMPANY.getKey().equals(key)) {
                    // 企业
                    String companyIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
                    Map<String, Map<String, Object>> company = iCompanyService.queryDataMationForMapByIds(companyIdStr);
                    setObjectMation(applicableObjectsList, company, key);
                }
            });
        }
    }

    private static void setFieldType(WagesModel wagesModel, Map<String, FieldType> fieldTypeMap) {
        if (CollectionUtil.isNotEmpty(wagesModel.getWagesModelFieldList())) {
            wagesModel.getWagesModelFieldList().forEach(wagesModelField -> {
                FieldType fieldType = fieldTypeMap.get(wagesModelField.getFieldKey());
                if (ObjectUtil.isEmpty(fieldType)) {
                    fieldType = new FieldType();
                    String name = WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getNameByKey(wagesModelField.getFieldKey());
                    fieldType.setName(name);
                }
                wagesModelField.setFieldKeyMation(fieldType);
                wagesModelField.setFieldTypeMation(WagesModelFieldType.getMation(wagesModelField.getFieldType()));
            });
        }
    }

    private static void setObjectMation(List<ModelApplicableObjects> applicableObjectsList, Map<String, Map<String, Object>> temMap, Integer key) {
        if (CollectionUtil.isEmpty(temMap)) {
            return;
        }
        applicableObjectsList.forEach(applicableObjects -> {
            if (key.equals(applicableObjects.getObjectType())) {
                applicableObjects.setObjectMation(temMap.get(applicableObjects.getObjectId()));
            }
        });
    }

    @Override
    public List<WagesModel> queryWagesModelByDate(String date) {
        if (StrUtil.isEmpty(date)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<WagesModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(WagesModel::getStartTime) + ", '%Y-%m') <= date_format({0}, '%Y-%m')", date)
            .apply("date_format(" + MybatisPlusUtil.toColumns(WagesModel::getEndTime) + ", '%Y-%m') >= date_format({0}, '%Y-%m')", date);
        queryWrapper.eq(MybatisPlusUtil.toColumns(WagesModel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<WagesModel> wagesModelList = list(queryWrapper);
        List<String> ids = wagesModelList.stream().map(WagesModel::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        List<WagesModel> wagesModels = selectByIds(ids.toArray(new String[]{}));
        return wagesModels;
    }
}
