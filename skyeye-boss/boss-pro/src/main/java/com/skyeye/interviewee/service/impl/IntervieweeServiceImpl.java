/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.base.handler.enclosure.EnclosureDetailsHandler;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.interviewee.classenum.IntervieweeStatusEnum;
import com.skyeye.interviewee.dao.IntervieweeDao;
import com.skyeye.interviewee.entity.Interviewee;
import com.skyeye.interviewee.service.IntervieweeFromService;
import com.skyeye.interviewee.service.IntervieweeService;
import com.skyeye.organization.service.IDepmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: IntervieweeServiceImpl
 * @Description: 面试者管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/27 13:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "面试者管理", groupName = "面试者管理")
public class IntervieweeServiceImpl extends SkyeyeBusinessServiceImpl<IntervieweeDao, Interviewee> implements IntervieweeService {

    @Autowired
    private IntervieweeFromService intervieweeFromService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private EnclosureDetailsHandler enclosureDetailsHandler;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer type = Integer.parseInt(pageInfo.getType());
        String userId = inputObject.getLogParams().get("id").toString();
        if (type == 1) {
            // 我录入的
            pageInfo.setCreateId(userId);
        } else if (type == 2) {
            // 我负责的
            pageInfo.setChargePersonId(userId);
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryBossIntervieweeList(pageInfo);
        iAuthUserService.setMationForMap(beans, "chargePersonId", "chargePersonMation");
        // 获取面试者来源信息
        intervieweeFromService.setMationForMap(beans, "fromId", "fromMation");
        // 附件
        beans.forEach(bean -> {
            Interviewee interviewee = BeanUtil.toBean(bean, Interviewee.class);
            enclosureDetailsHandler.executorHandler(interviewee);
            bean.put("enclosureResume", interviewee.getEnclosureResume());
        });
        return beans;
    }

    @Override
    public void createPrepose(Interviewee entity) {
        entity.setState(IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getKey());
    }

    @Override
    public void validatorEntity(Interviewee entity) {
        // 判断工作年限是否为负数
        if (Double.parseDouble(entity.getWorkYears()) < 0) {
            throw new CustomException("工作年限不能为负数!");
        }
        // 根据姓名、手机号查询面试者信息
        QueryWrapper<Interviewee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Interviewee::getName), entity.getName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Interviewee::getPhone), entity.getPhone());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Interviewee::getLastJoinTime));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Interviewee interviewee = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(interviewee)) {
            // 获取相同姓名+手机号最近的一条面试者数据
            List<Integer> needCheckStates =
                Arrays.asList(IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getKey(),
                    IntervieweeStatusEnum.INTERVIEW_STATUS.getKey(),
                    IntervieweeStatusEnum.INTERVIEW_FAILED_STATUS.getKey());
            if (needCheckStates.contains(interviewee.getState())) {
                throw new CustomException("同一个姓名、手机号的面试者已存在, 请重新确认面试者信息!");
            } else if (IntervieweeStatusEnum.INTERVIEW_PASS_STATUS.getKey().equals(interviewee.getState())) {
                Date lastJoinTimeDate = DateUtil.getPointTime(interviewee.getLastJoinTime(), DateUtil.YYYY_MM_DD);
                // 比较当前时间与最后入职的日期相差几个月
                long differMonth = cn.hutool.core.date.DateUtil.betweenMonth(lastJoinTimeDate, new Date(), true);
                if (differMonth < 6) {
                    throw new CustomException("该面试者通过面试未没有超过半年，则不允许录入");
                }
            } else if (IntervieweeStatusEnum.REJECTED_STATUS.getKey().equals(interviewee.getState())) {
                Date refuseTimeDate = DateUtil.getPointTime(interviewee.getRefuseTime(), DateUtil.YYYY_MM_DD);
                // 比较当前时间与最后入职的日期相差几个月
                long differMonth = cn.hutool.core.date.DateUtil.betweenMonth(refuseTimeDate, new Date(), true);
                if (differMonth < 6) {
                    throw new CustomException("该面试者不通过面试未没有超过半年，则不允许录入");
                }
            }
        }
    }

    @Override
    public Interviewee selectById(String id) {
        Interviewee interviewee = super.selectById(id);
        // 设置面试者来源信息
        interviewee.setFromMation(intervieweeFromService.selectById(interviewee.getFromId()));
        // 设置负责人
        interviewee.setChargePersonMation(iAuthUserService.queryDataMationById(interviewee.getChargePersonId()));
        // 设置入职的部门
        iDepmentService.setDataMation(interviewee, Interviewee::getLastJoinDepartmentId);
        return interviewee;
    }

    @Override
    public List<Interviewee> selectByIds(String... ids) {
        List<Interviewee> intervieweeList = super.selectByIds(ids);
        // 获取面试者来源信息
        intervieweeFromService.setDataMation(intervieweeList, Interviewee::getFromId);
        // 设置负责人信息
        iAuthUserService.setDataMation(intervieweeList, Interviewee::getChargePersonId);
        // 设置入职的部门
        iDepmentService.setDataMation(intervieweeList, Interviewee::getLastJoinDepartmentId);
        return intervieweeList;
    }

    @Override
    protected void deletePreExecution(Interviewee entity) {
        if (!IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getKey().equals(entity.getState())) {
            throw new CustomException("删除失败, 只有待面试状态的数据可删除!");
        }
    }

    @Override
    public void editStateById(String id, Integer state) {
        UpdateWrapper<Interviewee> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Interviewee::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }
}
