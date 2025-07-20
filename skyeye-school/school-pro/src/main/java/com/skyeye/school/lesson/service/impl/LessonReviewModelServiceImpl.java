package com.skyeye.school.lesson.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredChildService;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredService;
import com.skyeye.school.lesson.dao.LessonReviewModelDao;
import com.skyeye.school.lesson.entity.LessonReviewModel;
import com.skyeye.school.lesson.entity.LessonReviewType;
import com.skyeye.school.lesson.service.LessonReviewModelService;
import com.skyeye.school.lesson.service.LessonReviewTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@SkyeyeService(name = "听评课模型管理", groupName = "听评课模型管理")
public class LessonReviewModelServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewModelDao, LessonReviewModel> implements LessonReviewModelService {

    @Autowired
    private LessonReviewTypeService lessonReviewTypeService;

    @Autowired
    private LecturesAttenanceRecoredService lecturesAttenanceRecoredService;

    @Autowired
    private LecturesAttenanceRecoredChildService lecturesAttenanceRecoredChildService;


    @Override
    protected void validatorEntity(LessonReviewModel entity) {
        super.validatorEntity(entity);
    }


    @Override
    public String createEntity(LessonReviewModel entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(LessonReviewModel entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    public QueryWrapper<LessonReviewModel> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LessonReviewModel> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LessonReviewModel::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        return queryWrapper;
    }

    @Override
    protected void createPostpose(LessonReviewModel entity, String userId) {
        // 1. 新增LessonReviewType
        List<LessonReviewType> reviewTypeList = entity.getLessonReviewTypeList();
        if (ObjectUtil.isNotEmpty(reviewTypeList)) {
            for (LessonReviewType reviewType : reviewTypeList) {
                reviewType.setModelId(entity.getId());
            }
            lessonReviewTypeService.createEntity(reviewTypeList, userId);
        }

        // 2. 新增LecturesAttenanceRecored
        List<LecturesAttenanceRecored> attenanceRecoredList = entity.getLecturesAttenanceRecoredList();
        if (ObjectUtil.isNotEmpty(attenanceRecoredList)) {
            for (LecturesAttenanceRecored recored : attenanceRecoredList) {
                recored.setReviewModelId(entity.getId());
            }
            lecturesAttenanceRecoredService.createEntity(attenanceRecoredList, userId);
        }
    }


    @Override
    protected void updatePostpose(LessonReviewModel entity, String userId) {
        //1.编辑LessonReviewType
        List<LessonReviewType> lessonReviewTypeList = entity.getLessonReviewTypeList();
        if (ObjectUtil.isNotEmpty(lessonReviewTypeList)) {
            lessonReviewTypeService.updateEntity(lessonReviewTypeList, userId);
        }
        // 2. 编辑LecturesAttenanceRecored及其子表
        List<LecturesAttenanceRecored> lecturesAttenanceRecoredList = entity.getLecturesAttenanceRecoredList();
        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecoredList)) {
            lecturesAttenanceRecoredService.updateEntity(lecturesAttenanceRecoredList, userId);
            for (LecturesAttenanceRecored parent : lecturesAttenanceRecoredList) {
                List<LecturesAttenanceRecoredChild> children = parent.getLecturesAttenanceRecoredChildList();
                if (ObjectUtil.isNotEmpty(children)) {
                    lecturesAttenanceRecoredChildService.updateEntity(children, userId);
                }
            }
        }
    }

    @Override
    public LessonReviewModel queryByModelId(String modelId) {
        QueryWrapper<LessonReviewModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, modelId);
        return getOne(queryWrapper);
    }

    @Override
    public LessonReviewModel selectById(String id) {
        LessonReviewModel lessonReviewModel = super.selectById(id);
        //查类型
        List<LessonReviewType> lessonReviewTypeList = lessonReviewTypeService.queryTypeByModelId(id);
        lessonReviewModel.setLessonReviewTypeList(lessonReviewTypeList);
        //查听课记录及其子表
        List<LecturesAttenanceRecored> lecturesAttenanceRecoredList = lecturesAttenanceRecoredService.getByReviewModelId(id);
        if (CollectionUtil.isNotEmpty(lecturesAttenanceRecoredList)) {
            List<String> LecturesAttenanceRecoredIds = lecturesAttenanceRecoredList.stream().map(LecturesAttenanceRecored::getId).collect(Collectors.toList());
            List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = lecturesAttenanceRecoredChildService.queryChildByAttenanceRecordIds(LecturesAttenanceRecoredIds);
            Map<String, List<LecturesAttenanceRecoredChild>> map = lecturesAttenanceRecoredChildList.stream().collect(Collectors.groupingBy(LecturesAttenanceRecoredChild::getAttenanceRecordId));
            lecturesAttenanceRecoredList.forEach(
                    lecturesAttenanceRecored -> lecturesAttenanceRecored
                            .setLecturesAttenanceRecoredChildList(map.get(lecturesAttenanceRecored.getId())));
        }
        lessonReviewModel.setLecturesAttenanceRecoredList(lecturesAttenanceRecoredList);
        return lessonReviewModel;
    }

    @Override
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {

        super.queryPageList(inputObject, outputObject);

    }

    @Override
    protected void deletePreExecution(String id) {
        // 1.删除LessonReviewType
        lessonReviewTypeService.deleteTypeByModelId(id);
        //查询与该模型关联的所有听课记录
        List<LecturesAttenanceRecored> recoreds = lecturesAttenanceRecoredService.getByReviewModelId(id);
        if (CollectionUtil.isNotEmpty(recoreds)) {
            List<String> recoredIds = recoreds.stream().map(LecturesAttenanceRecored::getId).collect(Collectors.toList());
            //2.删除LecturesAttenanceRecoredChild
            lecturesAttenanceRecoredChildService.deleteChildByAttenanceRecordIdList(recoredIds);
            // 3.删除LecturesAttenanceRecored
            lecturesAttenanceRecoredService.deleteById(recoredIds);
        }
    }
}
