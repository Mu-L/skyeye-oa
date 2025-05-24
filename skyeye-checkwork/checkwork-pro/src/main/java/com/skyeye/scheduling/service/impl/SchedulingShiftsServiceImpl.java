package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.scheduling.dao.SchedulingShiftsDao;
import com.skyeye.scheduling.entity.SchedulingShifts;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;
import com.skyeye.scheduling.service.SchedulingShiftsService;
import com.skyeye.scheduling.service.SchedulingShiftsTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班班次管理", groupName = "排班班次管理")
public class SchedulingShiftsServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsDao, SchedulingShifts> implements SchedulingShiftsService {

    @Autowired
    private SchedulingShiftsTimeService schedulingShiftsTimeService;

    @Override
    protected void createPrepose(SchedulingShifts entity) {
        Integer minStaff = entity.getMinStaff();
        Integer maxStaff = entity.getMaxStaff();
        if (StrUtil.isNotEmpty(String.valueOf(minStaff)) && StrUtil.isNotEmpty(String.valueOf(maxStaff))) {
            if (minStaff > maxStaff) {
                throw new CustomException("最小人数不能大于最大人数");
            }
        }
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        if (CollectionUtil.isNotEmpty(schedulingShiftsTimeMation)) {
            for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimeMation) {
                String startTime = schedulingShiftsTime.getStartTime();
                String endTime = schedulingShiftsTime.getEndTime();
                if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) {
                    throw new CustomException("班次时间不能为空");
                }
            }
        }
    }

    @Override
    protected void createPostpose(SchedulingShifts entity, String userId) {
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        if (CollectionUtil.isNotEmpty(schedulingShiftsTimeMation)) {
            for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimeMation) {
                schedulingShiftsTime(schedulingShiftsTime);
                schedulingShiftsTime.setShiftId(entity.getId());
            }
            schedulingShiftsTimeService.createEntity(schedulingShiftsTimeMation, userId);
        }
    }

    @Override
    protected void updatePrepose(SchedulingShifts entity) {
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        if (CollectionUtil.isNotEmpty(schedulingShiftsTimeMation)) {
            for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimeMation) {
                String startTime = schedulingShiftsTime.getStartTime();
                String endTime = schedulingShiftsTime.getEndTime();
                if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) {
                    throw new CustomException("班次时间不能为空");
                }
            }
        }
    }

    @Override
    protected void updatePostpose(SchedulingShifts entity, String userId) {
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimeMation) {
            schedulingShiftsTime(schedulingShiftsTime);
        }
        if (CollectionUtil.isNotEmpty(schedulingShiftsTimeMation)) {
            schedulingShiftsTimeService.updateEntity(schedulingShiftsTimeMation, userId);
        }

    }

    private static void schedulingShiftsTime(SchedulingShiftsTime schedulingShiftsTime) {
        String startTime = schedulingShiftsTime.getStartTime();
        String endTime = schedulingShiftsTime.getEndTime();
        boolean compareTime = DateUtil.compareTimeHMS(startTime, endTime);
        if (!compareTime) {
            schedulingShiftsTime.setIsNextDay(CommonNumConstants.NUM_ONE);
        } else {
            schedulingShiftsTime.setIsNextDay(CommonNumConstants.NUM_ZERO);
        }
    }

    @Override
    public void deleteSchedulingShifts(InputObject inputObject, OutputObject outputObject) {
        String ids = inputObject.getParams().get("ids").toString();
        List<String> idList = Arrays.asList(ids);
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, idList);
        boolean remove = remove(queryWrapper);
        if (remove) {
            schedulingShiftsTimeService.deleteSchedulingShiftsTimeByShiftIds(idList);
        }
    }

    @Override
    public void querySchedulingShiftsList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String keyword = commonPageInfo.getKeyword();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(keyword)){
            queryWrapper.like(MybatisPlusUtil.toColumns(SchedulingShifts::getShiftName), keyword);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingShifts::getCreateTime));
        List<SchedulingShifts> schedulingShiftsList = list(queryWrapper);
        Map<String, List<SchedulingShifts>> schedulingShiftsMap = schedulingShiftsList.stream().collect(Collectors.groupingBy(SchedulingShifts::getId));
        List<String> schedulingShiftsIds = schedulingShiftsList.stream().map(SchedulingShifts::getId).collect(Collectors.toList());
        Map<String, List<SchedulingShiftsTime>> timeMapList = schedulingShiftsTimeService.queryTimeByIdList(schedulingShiftsIds);
        schedulingShiftsMap.forEach((k, v) -> {
            v.get(0).setSchedulingShiftsTimeMation(timeMapList.get(k));
        });
        List<SchedulingShifts> allShifts = schedulingShiftsMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
//        List<String> createIds = allShifts.stream().map(SchedulingShifts::getCreateId).collect(Collectors.toList());
//        Map<String, Map<String, Object>> stringMapMap = iAuthUserService.queryUserNameList(createIds);
//        allShifts.forEach(shifts -> {
//            Map<String, Object> stringObjectMap = stringMapMap.get(shifts.getCreateId());
//            if (ObjectUtil.isNotEmpty(stringObjectMap)) {
//                shifts.setCreateMation(stringObjectMap);
//            }
//        });
        iAuthUserService.setName(allShifts, "createId", "createName");
        iAuthUserService.setName(allShifts, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(allShifts);
        outputObject.settotal(page.getTotal());

    }

    @Override
    public SchedulingShifts selectById(String id) {
        SchedulingShifts schedulingShifts = super.selectById(id);
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryTimeByShiftId(id);
        schedulingShifts.setSchedulingShiftsTimeMation(schedulingShiftsTimes);
        return schedulingShifts;
    }

    @Override
    public void querySchedulingShiftsById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SchedulingShifts schedulingShifts = selectById(id);
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryTimeByShiftId(id);
        schedulingShifts.setSchedulingShiftsTimeMation(schedulingShiftsTimes);
        outputObject.setBean(schedulingShifts);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
