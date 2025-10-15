/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.WagesConstant;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.wages.WagesStaffWorkTimeMation;
import com.skyeye.eve.field.classenum.WagesTypeEnum;
import com.skyeye.eve.field.dao.FieldStaffLinkDao;
import com.skyeye.eve.field.entity.FieldStaffLink;
import com.skyeye.eve.field.entity.FieldType;
import com.skyeye.eve.field.service.FieldStaffLinkService;
import com.skyeye.eve.field.service.WagesFieldTypeService;
import com.skyeye.eve.model.dao.WagesModelDao;
import com.skyeye.eve.model.dao.WagesModelFieldDao;
import com.skyeye.eve.rest.promote.service.ISysEveUserStaffService;
import com.skyeye.eve.service.IScheduleDayService;
import com.skyeye.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: FieldStaffLinkServiceImpl
 * @Description: 员工与薪资字段关系管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工与薪资字段关系管理", groupName = "员工与薪资字段关系管理")
public class FieldStaffLinkServiceImpl extends SkyeyeBusinessServiceImpl<FieldStaffLinkDao, FieldStaffLink> implements FieldStaffLinkService {

    private static Logger log = LoggerFactory.getLogger(FieldStaffLinkServiceImpl.class);

    @Autowired
    private WagesModelDao wagesModelDao;

    @Autowired
    private WagesModelFieldDao wagesModelFieldDao;

    @Autowired
    private IScheduleDayService iScheduleDayService;

    @Autowired
    private ISysEveUserStaffService iSysEveUserStaffService;

    @Autowired
    private WagesFieldTypeService wagesFieldTypeService;

    @Override
    public void queryStaffWagesModelFieldMationListByStaffId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Arrays.asList(staffId));
        if (CollectionUtil.isEmpty(staffMap)) {
            throw new CustomException("员工不存在");
        }
        Map<String, Object> staffMation = staffMap.get(staffId);
        List<String> wagesApplicableObject = Arrays.asList(new String[]{
            staffMation.getOrDefault("companyId", StrUtil.EMPTY).toString(),
            staffMation.getOrDefault("departmentId", StrUtil.EMPTY).toString(),
            staffId}).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        List<Map<String, Object>> modelField = new ArrayList<>();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        // 1.获取薪资模板
        List<Map<String, Object>> model = wagesModelDao.queryWagesModelListByApplicableObjectIds(wagesApplicableObject, tenantId);
        if (CollectionUtil.isNotEmpty(model)) {
            // 2.获取模板参数
            List<String> modelIds = model.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
            modelField = wagesModelFieldDao.queryWagesModelFieldByModelIdsAndStaffId(modelIds, staffId,
                staffMation.getOrDefault("jobScoreId", StrUtil.EMPTY).toString(), tenantId);
        }
        modelField.stream().forEach(bean -> {
            if ("1".equals(bean.get("monthlyClearing").toString())) {
                // 如果是自动清零，则不可进行薪资编辑
                bean.put("disabled", "disabled");
            }
            Integer wagesType = Integer.parseInt(bean.get("wagesType").toString());
            if (WagesTypeEnum.SALARY_INCREASE.getKey().equals(wagesType)) {
                bean.put("wagesTypeStr", WagesTypeEnum.SALARY_INCREASE.getValue());
            } else {
                bean.put("wagesTypeStr", WagesTypeEnum.SALARY_REDUCTION.getValue());
            }
        });
        outputObject.setBeans(modelField);
        outputObject.settotal(modelField.size());
    }

    /**
     * 保存员工薪资设定
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveStaffWagesModelFieldMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String fieldStr = map.get("fieldStr").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> wagesModelFieldMation = JSONUtil.toList(JSONUtil.parseArray(fieldStr), null);
        wagesModelFieldMation.forEach(bean -> {
            bean.put("staffId", staffId);
        });
        // 保存薪资要素字段信息
        skyeyeBaseMapper.saveStaffWagesModelFieldMation(wagesModelFieldMation, tenantId);
        // 保存员工月标准薪资信息以及设定状态
        iSysEveUserStaffService.editSysUserStaffActMoneyById(staffId, map.get("actMoney").toString());
    }

    /**
     * 获取应出勤的班次以及小时
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void setLastMonthBe(InputObject inputObject, OutputObject outputObject) {
        WagesStaffWorkTimeMation wagesStaffWorkTimeMation = inputObject.getParams(WagesStaffWorkTimeMation.class);
        Map<String, Object> staffModelFieldMap = this.setLastMonthBe(wagesStaffWorkTimeMation.getStaffWorkTime(),
            wagesStaffWorkTimeMation.getLastMonthDate());
        outputObject.setBean(staffModelFieldMap);
    }

    /**
     * 设置应出勤的班次以及小时
     *
     * @param staffWorkTime 员工对应的考勤班次
     * @param lastMonthDate 指定年月，格式为yyyy-MM
     * @return 员工拥有的所有薪资要素字段以及对应的值
     */
    @Override
    public Map<String, Object> setLastMonthBe(List<Map<String, Object>> staffWorkTime, String lastMonthDate) {
        List<String> lastMonthDays = DateUtil.getMonthFullDay(Integer.parseInt(lastMonthDate.split("-")[0]), Integer.parseInt(lastMonthDate.split("-")[1]));
        int lastMonthBeNum = 0;
        String lastMonthBeHour = "0";
        for (Map<String, Object> bean : staffWorkTime) {
            List<Map<String, Object>> days = (List<Map<String, Object>>) bean.get("checkWorkTimeWeekList");
            for (String day : lastMonthDays) {
                // 周几
                if (iScheduleDayService.judgeISHoliday(day)) {
                    // 如果是节假日，则不计算
                    continue;
                }
                int weekDay = DateUtil.getWeek(day);
                int weekType = DateUtil.getWeekType(day);
                List<Map<String, Object>> simpleDay = days.stream().filter(item -> Integer.parseInt(item.get("weekNumber").toString()) == weekDay)
                    .collect(Collectors.toList());
                if (simpleDay != null && !simpleDay.isEmpty()) {
                    // 如果今天是需要考勤的日期
                    int dayType = Integer.parseInt(simpleDay.get(0).get("type").toString());
                    if (weekType == 1 && dayType == 2) {
                        // 如果获取到的日期是双周，但考勤班次里面是单周，则不做任何操作
                    } else {
                        // 单周或者每周的当天都上班
                        lastMonthBeNum++;
                        try {
                            String startTime = DateUtil.formatDate(bean.get("startTime").toString());
                            String endTime = DateUtil.formatDate(bean.get("endTime").toString());
                            String time = DateUtil.getDistanceMinuteByHMS(startTime, endTime);
                            lastMonthBeHour = CalculationUtil.add(lastMonthBeHour, time, 2);
                        } catch (Exception e) {
                            log.warn("get differ time failed, startTime is: {}, endTime is: {}", bean.get("startTime").toString(),
                                bean.get("endTime").toString(), e);
                        }
                    }
                }
            }
        }
        Map<String, Object> staffModelFieldMap = new HashMap<>();
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_NUM.getKey(), String.valueOf(lastMonthBeNum));
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey(), CalculationUtil.divide(lastMonthBeHour, "60", 2));
        return staffModelFieldMap;
    }

    @Override
    public void updateStaffFiledKey(String oldKey, String newKey) {
        UpdateWrapper<FieldStaffLink> updateWrapper = new UpdateWrapper<>();
        String keyField = MybatisPlusUtil.toColumns(FieldStaffLink::getFieldTypeKey);
        updateWrapper.eq(keyField, oldKey);
        updateWrapper.set(keyField, newKey);
        update(updateWrapper);
    }

    @Override
    public void deleteStaffFiledKey(String key) {
        QueryWrapper<FieldStaffLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FieldStaffLink::getFieldTypeKey), key);
        remove(queryWrapper);
    }

    @Override
    public void addWagesStaffMationByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getParams().get("staffId").toString();
        // 获取所有薪资要素字段类型(不包含默认字段)
        List<FieldType> fieldTypes = wagesFieldTypeService.queryAllWagesFieldTypeList();
        // 封装成FieldStaffLink对象
        List<FieldStaffLink> fieldStaffLinkList = fieldTypes.stream().map(fieldType -> {
            FieldStaffLink fieldStaffLink = new FieldStaffLink();
            fieldStaffLink.setStaffId(staffId);
            fieldStaffLink.setFieldTypeKey(fieldType.getKey());
            return fieldStaffLink;
        }).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(fieldStaffLinkList)) {
            createEntity(fieldStaffLinkList, StrUtil.EMPTY);
        }
    }

}
