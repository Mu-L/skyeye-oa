/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personrequire.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.personrequire.classenum.PersonRequireStateEnum;
import com.skyeye.personrequire.dao.PersonRequireDao;
import com.skyeye.personrequire.entity.PersonRequire;
import com.skyeye.personrequire.service.PersonRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PersonRequireServiceImpl
 * @Description: 人员需求服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/8 16:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "人员需求申请", groupName = "人员需求申请", flowable = true)
public class PersonRequireServiceImpl extends SkyeyeBusinessServiceImpl<PersonRequireDao, PersonRequire> implements PersonRequireService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    @Override
    protected QueryWrapper<PersonRequire> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PersonRequire> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(PersonRequire::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "recruitDepartmentId", "recruitDepartmentMation");
        iCompanyJobService.setMationForMap(beans, "recruitJobId", "recruitJobMation");
        return beans;
    }

    private List<Map<String, Object>> queryBySQL(CommonPageInfo pageInfo) {
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryPersonRequireList(pageInfo);
        iDepmentService.setMationForMap(beans, "recruitDepartmentId", "recruitDepartmentMation");
        iCompanyJobService.setMationForMap(beans, "recruitJobId", "recruitJobMation");
        return beans;
    }

    @Override
    public PersonRequire selectById(String id) {
        PersonRequire personRequire = super.selectById(id);
        // 获取部门信息
        personRequire.setRecruitDepartmentMation(iDepmentService.queryDataMationById(personRequire.getRecruitDepartmentId()));
        // 获取招聘岗位
        personRequire.setRecruitJobMation(iCompanyJobService.queryDataMationById(personRequire.getRecruitJobId()));
        // 获取负责人信息
        if (CollectionUtil.isNotEmpty(personRequire.getPersonLiable())) {
            personRequire.setPersonLiableMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(personRequire.getPersonLiable())));
        }
        return personRequire;
    }

    @Override
    public List<PersonRequire> selectByIds(String... ids) {
        List<PersonRequire> personRequires = super.selectByIds(ids);
        // 获取部门信息
        iDepmentService.setDataMation(personRequires, PersonRequire::getRecruitDepartmentId);
        // 获取招聘岗位
        iCompanyJobService.setDataMation(personRequires, PersonRequire::getRecruitJobId);
        // 获取负责人信息
        List<String> personLiableIds = personRequires.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getPersonLiable()))
            .flatMap(norms -> norms.getPersonLiable().stream()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(personLiableIds)) {
            Map<String, Map<String, Object>> userMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(personLiableIds));
            personRequires.forEach(personRequire -> {
                if (CollectionUtil.isEmpty(personRequire.getPersonLiable())) {
                    return;
                }
                List<Map<String, Object>> userMation = new ArrayList<>();
                personRequire.getPersonLiable().forEach(personLiableId -> {
                    if (!userMap.containsKey(personLiableId)) {
                        return;
                    }
                    userMation.add(userMap.get(personLiableId));
                });
                personRequire.setPersonLiableMation(userMation);
            });
        }
        return personRequires;
    }

    /**
     * 人员需求申请责任人设置
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setPersonLiable(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        PersonRequire personRequire = selectById(id);
        if (StrUtil.equals(FlowableStateEnum.PASS.getKey(), personRequire.getState())
            || StrUtil.equals(PersonRequireStateEnum.IN_RECRUITMENT.getKey(), personRequire.getState())) {
            // 审核通过状态/招聘中可以设置责任人
            List<String> personLiable = JSONUtil.toList(map.get("personLiable").toString(), null);
            UpdateWrapper<PersonRequire> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            personRequire.setPersonLiable(personLiable);
            update(personRequire, updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("只有审核通过状态下/招聘中可以设置责任人");
        }
    }

    /**
     * 获取我负责的人员需求申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyChargePersonRequireList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        // 我负责的
        pageInfo.setChargePersonId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = queryBySQL(pageInfo);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取所有审批通过状态之后的人员需求申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllPersonRequireList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        // 审核通过，招聘中，招聘结束
        pageInfo.setStateList(Arrays.asList(FlowableStateEnum.PASS.getKey(), PersonRequireStateEnum.IN_RECRUITMENT.getKey(),
            PersonRequireStateEnum.END_RECRUITMENT.getKey()));
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = queryBySQL(pageInfo);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void updatePersonRequireNum(String id, Integer num) {
        PersonRequire personRequire = selectById(id);
        if (StrUtil.equals(FlowableStateEnum.PASS.getKey(), personRequire.getState())
            || StrUtil.equals(PersonRequireStateEnum.IN_RECRUITMENT.getKey(), personRequire.getState())) {
            // 审核通过，招聘中的可以修改已招聘人数
            Integer currentNum = personRequire.getRecruitedNum() + num;
            if (currentNum > personRequire.getRecruitNum()) {
                throw new CustomException("超出该人员需求申请单的招聘人数。");
            }
            UpdateWrapper<PersonRequire> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(PersonRequire::getRecruitedNum), currentNum);
            if (currentNum == personRequire.getRecruitNum()) {
                updateWrapper.set(MybatisPlusUtil.toColumns(PersonRequire::getState), PersonRequireStateEnum.END_RECRUITMENT.getKey());
            }
            update(updateWrapper);
            refreshCache(id);
        } else {
            throw new CustomException("人员需求申请单状态错误。");
        }
    }
}
