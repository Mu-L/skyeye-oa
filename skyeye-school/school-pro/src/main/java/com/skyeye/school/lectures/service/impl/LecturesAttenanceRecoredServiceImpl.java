package com.skyeye.school.lectures.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
        if (Objects.isNull(value)) {
            throw new CustomException(fieldName + "不能为空");
        }
        if (value < CommonNumConstants.NUM_ZERO) {
            throw new CustomException(fieldName + "必须大于等于0");
        }
    }

    @Override
    public void createPostpose(List<LecturesAttenanceRecored> entity, String userId) {
        List<LecturesAttenanceRecoredChild> insertList = new ArrayList<>();
        for (LecturesAttenanceRecored recored : entity) {
            for (LecturesAttenanceRecoredChild recoredChild : recored.getLecturesAttenanceRecoredChildList()) {
                recoredChild.setAttenanceRecordId(recored.getId());
            }
            insertList.addAll(recored.getLecturesAttenanceRecoredChildList());
        }
        lecturesAttenanceRecoredChildService.createEntity(insertList, userId);
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
    public List<LecturesAttenanceRecored> queryByAttenanceRecordId(String attenanceRecordId) {
        QueryWrapper<LecturesAttenanceRecored> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, attenanceRecordId);
        return list(queryWrapper);
    }

    @Override
    public void deleteAttenanceRecoredByReviewModelId(String id) {
        QueryWrapper<LecturesAttenanceRecored> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecored::getReviewModelId), id);
        List<LecturesAttenanceRecored> list = list(queryWrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            List<String> attenceRecoredIdList = list.stream().map(LecturesAttenanceRecored::getId).collect(Collectors.toList());
            lecturesAttenanceRecoredChildService.deleteChildByAttenanceRecordIdList(attenceRecoredIdList);
            remove(queryWrapper);
        }
    }

    @Override
    protected void deletePreExecution(String id) {
        lecturesAttenanceRecoredChildService.deleteChildByAttenanceRecordId(id);
    }

    @Override
    public LecturesAttenanceRecored selectById(String id) {
        LecturesAttenanceRecored lecturesAttenanceRecored = super.selectById(id);
        if (ObjectUtil.isEmpty(lecturesAttenanceRecored)) {
            throw new CustomException("未找到该ID的听课记录");
        }
        // 查询质评-听课记录表
        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecored.getLecturesAttenanceRecoredChildList())) {
            lecturesAttenanceRecoredChildService.setDataMation(lecturesAttenanceRecored.getLecturesAttenanceRecoredChildList(), LecturesAttenanceRecoredChild::getAttenanceRecordId);

            lecturesAttenanceRecored.getLecturesAttenanceRecoredChildList().forEach(lecturesAttenanceRecoredChild -> {
                lecturesAttenanceRecoredChild.setOpen(true);
            });
        }
        lecturesAttenanceRecoredChildService.setDataMation(lecturesAttenanceRecored, LecturesAttenanceRecored::getAttendLectureTeacherId);
        return lecturesAttenanceRecored;
    }

    /**
     * 根据听评表模型ID查询所有关联的听课记录
     *
     * @param reviewModelId
     * @return
     */
    @Override
    public List<LecturesAttenanceRecored> getByReviewModelId(String reviewModelId) {
        if (StrUtil.isEmpty(reviewModelId)) {
            return Collections.emptyList();
        }
        QueryWrapper<LecturesAttenanceRecored> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecored::getReviewModelId), reviewModelId);
        return list(queryWrapper);
    }


}
