package com.skyeye.school.lectures.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.lectures.dao.LecturesAttenanceRecoredDao;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredChildService;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@SkyeyeService(name = "质评-听课记录表", groupName = "质评-听课记录表")
public class LecturesAttenanceRecoredServiceImpl extends SkyeyeBusinessServiceImpl<LecturesAttenanceRecoredDao, LecturesAttenanceRecored> implements LecturesAttenanceRecoredService {

    @Autowired
    private LecturesAttenanceRecoredChildService lecturesAttenanceRecoredChildService;

    @Override
    protected void validatorEntity(LecturesAttenanceRecored entity) {
        super.validatorEntity(entity);
        noScoreName(entity);
        validateNumber(entity.getShouldNum(), "应到人数");
        validateNumber(entity.getActualNum(), "实到人数");
        validateNumber(entity.getLateNum(), "迟到人数");
        validateNumber(entity.getLeaveEarlyNum(), "早退人数");
    }

    private void validateNumber(Integer value, String fieldName) {
        if (value == null) {
            throw new CustomException(fieldName + "不能为空");
        }
        if (value < CommonNumConstants.NUM_ZERO) {
            throw new CustomException(fieldName + "必须大于等于0");
        }
    }

    @Override
    protected void updatePostpose(LecturesAttenanceRecored entity, String userId) {
        List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = entity.getLecturesAttenanceRecoredChildList();
        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecoredChildList)) {
            lecturesAttenanceRecoredChildService.updateEntity(lecturesAttenanceRecoredChildList, userId);
        }
    }

    private void noScoreName(LecturesAttenanceRecored entity) {
        String scoreName = entity.getScoreName();
        QueryWrapper<LecturesAttenanceRecored> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecored::getScoreName), scoreName);
        LecturesAttenanceRecored one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one) && !one.getId().equals(entity.getId())) {
            throw new RuntimeException("课程名称已存在");
        }
    }

    @Override
    public LecturesAttenanceRecored queryByAttenanceRecordId(String attenanceRecordId) {
        QueryWrapper<LecturesAttenanceRecored> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, attenanceRecordId);
        return getOne(queryWrapper);
    }

    @Override
    public LecturesAttenanceRecored selectById(String id) {
        List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = lecturesAttenanceRecoredChildService.queryChildByAttenanceRecordId(id);
        LecturesAttenanceRecored lecturesAttenanceRecored = super.selectById(id);
        lecturesAttenanceRecored.setLecturesAttenanceRecoredChildList(lecturesAttenanceRecoredChildList);
        return lecturesAttenanceRecored;
    }

    @Override
    protected void deletePreExecution(String id) {
        lecturesAttenanceRecoredChildService.deleteChildByAttenanceRecordId(id);
    }

}
