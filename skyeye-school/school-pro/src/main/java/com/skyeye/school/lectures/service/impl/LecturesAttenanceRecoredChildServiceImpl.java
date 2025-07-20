package com.skyeye.school.lectures.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
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

import java.util.Collections;
import java.util.List;

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
        LecturesAttenanceRecored lecturesAttenanceRecored = lecturesAttenanceRecoredService.getById(attenanceRecordId);
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
    protected void createPrepose(List<LecturesAttenanceRecoredChild> entity) {
        LecturesAttenanceRecoredChild attenanceRecoredChild = entity.stream().findFirst().orElse(new LecturesAttenanceRecoredChild());
        deleteChildByAttenanceRecordId(attenanceRecoredChild.getAttenanceRecordId());
    }


    @Override
    public List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordId(String attenanceRecordId) {
        QueryWrapper<LecturesAttenanceRecoredChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getAttenanceRecordId), attenanceRecordId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getOrder));
        return list(queryWrapper);
    }

    @Override
    public void deleteChildByAttenanceRecordId(String id) {
        if (CollectionUtil.isEmpty(Collections.singleton(id))) {
            return;
        }
        QueryWrapper<LecturesAttenanceRecoredChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getAttenanceRecordId), id);
        remove(queryWrapper);
    }

    @Override
    public void deleteChildByAttenanceRecordIdList(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return;
        }
        QueryWrapper<LecturesAttenanceRecoredChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getAttenanceRecordId), idList);
        remove(queryWrapper);
    }

    @Override
    public List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordIds(List<String> lecturesAttenanceRecoredIds) {
        if (CollectionUtil.isEmpty(lecturesAttenanceRecoredIds)) {
            return null;
        }
        QueryWrapper<LecturesAttenanceRecoredChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LecturesAttenanceRecoredChild::getAttenanceRecordId),lecturesAttenanceRecoredIds);
        return list(queryWrapper);
    }
}