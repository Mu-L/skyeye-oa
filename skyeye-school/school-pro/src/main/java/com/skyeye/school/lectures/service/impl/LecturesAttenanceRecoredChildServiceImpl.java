package com.skyeye.school.lectures.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.lectures.dao.LecturesAttenanceRecoredChildDao;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredChildService;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "授课成绩表", groupName = "授课成绩表")
public class LecturesAttenanceRecoredChildServiceImpl extends SkyeyeBusinessServiceImpl<LecturesAttenanceRecoredChildDao, LecturesAttenanceRecoredChild> implements LecturesAttenanceRecoredChildService {

    @Autowired
    private LecturesAttenanceRecoredService lecturesAttenanceRecoredService;

    @Override
    protected void validatorEntity(LecturesAttenanceRecoredChild entity) {
        super.validatorEntity(entity);
        noAttenanceRecordId(entity);
    }

    private void noAttenanceRecordId(LecturesAttenanceRecoredChild entity) {
        String attenanceRecordId = entity.getAttenanceRecordId();
        LecturesAttenanceRecored lecturesAttenanceRecored = lecturesAttenanceRecoredService.queryByAttenanceRecordId(attenanceRecordId);
        if (ObjectUtil.isEmpty(lecturesAttenanceRecored)) {
            throw new CustomException("该听课记录表不存在");
        }
    }

    @Override
    protected void validatorEntity(List<LecturesAttenanceRecoredChild> entity) {
        super.validatorEntity(entity);
        entity.forEach(
                lecturesAttenanceRecoredService -> noAttenanceRecordId(lecturesAttenanceRecoredService)
        );
    }

    @Override
    public List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordId(String id) {
        QueryWrapper<LecturesAttenanceRecoredChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getAttenanceRecordId), id);
        List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = list(queryWrapper);
        List<LecturesAttenanceRecoredChild> NoRecordChildNodes = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecoredChildList)) {
            NoRecordChildNodes = lecturesAttenanceRecoredChildList.stream().filter(
                    lecturesAttenanceRecoredChild -> StrUtil.isEmpty(lecturesAttenanceRecoredChild.getAttenanceRecordId())
            ).collect(Collectors.toList());
            List<LecturesAttenanceRecoredChild> ChildRecordChildNodes = lecturesAttenanceRecoredChildList.stream().filter(
                    lessonReviewType -> StrUtil.isNotEmpty(lessonReviewType.getAttenanceRecordId())
            ).collect(Collectors.toList());
            Map<String, List<LecturesAttenanceRecoredChild>> collected =
                    ChildRecordChildNodes.stream().collect(Collectors.groupingBy(LecturesAttenanceRecoredChild::getAttenanceRecordId));
            NoRecordChildNodes.forEach(
                    lecturesAttenanceRecoredChild -> lecturesAttenanceRecoredChild.setChildren(collected.get(lecturesAttenanceRecoredChild.getId()))
            );
        }
        return NoRecordChildNodes;
    }

    @Override
    public void deleteChildByAttenanceRecordId(String id) {
        QueryWrapper<LecturesAttenanceRecoredChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getAttenanceRecordId), id);
        remove(queryWrapper);
    }
}