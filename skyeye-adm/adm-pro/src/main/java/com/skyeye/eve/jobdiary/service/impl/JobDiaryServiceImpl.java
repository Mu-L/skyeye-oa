/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.jobdiary.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.jobdiary.classenum.JobDiaryState;
import com.skyeye.eve.jobdiary.classenum.JobDiaryType;
import com.skyeye.eve.jobdiary.classenum.ReadState;
import com.skyeye.eve.jobdiary.dao.JobDiaryDao;
import com.skyeye.eve.jobdiary.entity.JobDiary;
import com.skyeye.eve.jobdiary.service.JobDiaryReceivedService;
import com.skyeye.eve.jobdiary.service.JobDiaryService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: JobDiaryServiceImpl
 * @Description: 工作日志管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工作日志", groupName = "工作日志")
public class JobDiaryServiceImpl extends SkyeyeBusinessServiceImpl<JobDiaryDao, JobDiary> implements JobDiaryService {

    @Autowired
    private JobDiaryReceivedService jobDiaryReceivedService;

    @Override
    @IgnoreTenant
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        commonPageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        commonPageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMysendJobDiaryList(commonPageInfo);
        beans.forEach(bean -> {
            bean.put("stateName", JobDiaryState.getName(Integer.parseInt(bean.get("state").toString())));
            bean.put("typeName", JobDiaryType.getName(Integer.parseInt(bean.get("type").toString())));
        });
        return beans;
    }

    @Override
    public void validatorEntity(JobDiary entity) {
        entity.setName(JobDiaryType.getTitle(entity.getType()));
        entity.setState(JobDiaryState.NORMAL.getKey());
        entity.setCreateId(InputObject.getLogParamsStatic().get("id").toString());
        super.validatorEntity(entity);
    }

    @Override
    public void writePostpose(JobDiary entity, String userId) {
        super.writePostpose(entity, userId);
        jobDiaryReceivedService.save(entity.getId(), entity.getReceivedId());
    }

    @Override
    public void deletePostpose(String id) {
        jobDiaryReceivedService.deleteByDiaryId(id);
    }

    @Override
    public JobDiary getDataFromDb(String id) {
        JobDiary jobDiary = super.getDataFromDb(id);
        jobDiary.setReceivedId(jobDiaryReceivedService.queryByDiaryId(id));
        return jobDiary;
    }

    @Override
    public JobDiary selectById(String id) {
        JobDiary jobDiary = super.selectById(id);
        jobDiary.setReceivedMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(jobDiary.getReceivedId())));
        return jobDiary;
    }

    @Override
    @IgnoreTenant
    public void queryMyReceivedJobDiaryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        commonPageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        // 删除标记为正常
        commonPageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        // 只查询正常状态的日志
        commonPageInfo.setState(String.valueOf(JobDiaryState.NORMAL.getKey()));
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMyReceivedJobDiaryList(commonPageInfo);
        String serviceClassName = getServiceClassName();
        beans.forEach(bean -> {
            bean.put("stateName", ReadState.getName(Integer.parseInt(bean.get("state").toString())));
            bean.put("typeName", JobDiaryType.getName(Integer.parseInt(bean.get("type").toString())));
            bean.put("serviceClassName", serviceClassName);
        });
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void revokeJobDiaryById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        checkTime(id);
        UpdateWrapper<JobDiary> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(JobDiary::getState), JobDiaryState.REVOKE.getKey());
        update(updateWrapper);
        refreshCache(id);
    }

    private void checkTime(String id) {
        JobDiary jobDiary = selectById(id);
        // 计算当前时间和创建时间的时间差，返回分钟
        long twoHour = DateUtil.getDistanceMinute(DateUtil.getTimeAndToString(), jobDiary.getCreateTime());
        if (twoHour > 120) {
            throw new CustomException("已超出可操作时间，撤销失败！");
        }
    }

}
