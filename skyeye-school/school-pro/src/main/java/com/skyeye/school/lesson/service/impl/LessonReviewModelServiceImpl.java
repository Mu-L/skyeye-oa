package com.skyeye.school.lesson.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        initVersionByFromId(entity);

    }


    private void initVersionByFromId(LessonReviewModel entity) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            // 新增记录，初始化所有版本号
            entity.setVersionNo(String.valueOf(CommonNumConstants.NUM_ONE));
            // 大版本从0开始
            entity.setLargeVersion(CommonNumConstants.NUM_ZERO);
            // 小版本从0开始
            entity.setSmallVersion(CommonNumConstants.NUM_ZERO);
        } else {
            //更新
            try {
                int currentVersion = StrUtil.isEmpty(entity.getVersionNo())
                        ? Integer.parseInt(entity.getVersionNo())
                        : CommonNumConstants.NUM_ZERO;
                entity.setVersionNo(String.valueOf(currentVersion + CommonNumConstants.NUM_ONE));

                // 处理大版本号和小版本号
                if (entity.getStartSmallVersion()) {
                    // 小版本升级
                    entity.setSmallVersion(
                            (Objects.isNull(entity.getSmallVersion()) ? entity.getSmallVersion() : CommonNumConstants.NUM_ZERO) + CommonNumConstants.NUM_ONE
                    );
                } else {
                    // 大版本升级
                    entity.setLargeVersion(
                            (Objects.isNull(entity.getLargeVersion()) ? entity.getLargeVersion() : CommonNumConstants.NUM_ZERO) + CommonNumConstants.NUM_ONE
                    );
                    // 大版本升级时重置小版本
                    entity.setSmallVersion(CommonNumConstants.NUM_ZERO);
                }

            } catch (CustomException e) {
                log.error("版本号格式错误", e);
                entity.setVersionNo(String.valueOf(CommonNumConstants.NUM_ONE));
                entity.setLargeVersion(CommonNumConstants.NUM_ZERO);
                entity.setSmallVersion(CommonNumConstants.NUM_ZERO);
            }
        }
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
            for (LessonReviewType reviewType:reviewTypeList){
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
        // 2. 编辑LecturesAttenanceRecored
        List<LecturesAttenanceRecored> lecturesAttenanceRecoredList = entity.getLecturesAttenanceRecoredList();
        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecoredList)) {
            lecturesAttenanceRecoredService.updateEntity(lecturesAttenanceRecoredList, userId);
        }
//        // 3. 编辑LecturesAttenanceRecoredChild
//        List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = entity.getLecturesAttenanceRecoredChildList();
//        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecoredChildList)) {
//            lecturesAttenanceRecoredChildService.updateEntity(lecturesAttenanceRecoredChildList, userId);
//        }
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
        //查听课记录
        List<LecturesAttenanceRecored> lecturesAttenanceRecoredList = Collections.singletonList(lecturesAttenanceRecoredService.queryByAttenanceRecordId(id));
        lessonReviewModel.setLecturesAttenanceRecoredList(lecturesAttenanceRecoredList);
        //查授课成绩表
//        List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = lecturesAttenanceRecoredChildService.queryChildByAttenanceRecordId(id);
//        lessonReviewModel.setLecturesAttenanceRecoredChildList(lecturesAttenanceRecoredChildList);
        return lessonReviewModel;
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
