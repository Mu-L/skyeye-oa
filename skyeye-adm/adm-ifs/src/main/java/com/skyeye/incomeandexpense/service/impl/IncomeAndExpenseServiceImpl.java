/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.incomeandexpense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.incomeandexpense.dao.IncomeAndExpenseDao;
import com.skyeye.incomeandexpense.entity.IncomeAndExpense;
import com.skyeye.incomeandexpense.service.IncomeAndExpenseService;
import com.skyeye.subject.service.IfsAccountSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IncomeAndExpenseServiceImpl
 * @Description: 收支项目信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "收支项目管理", groupName = "收支项目管理")
public class IncomeAndExpenseServiceImpl extends SkyeyeBusinessServiceImpl<IncomeAndExpenseDao, IncomeAndExpense> implements IncomeAndExpenseService {

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        ifsAccountSubjectService.setMationForMap(beans, "subjectId", "subjectMation");
        return beans;
    }

    /**
     * 根据条件查询收支项目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryInoutitemListByType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String type = params.get("type").toString();

        QueryWrapper<IncomeAndExpense> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(IncomeAndExpense::getType), type);
        queryWrapper.eq(MybatisPlusUtil.toColumns(IncomeAndExpense::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());

        List<IncomeAndExpense> beans = list(queryWrapper);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
