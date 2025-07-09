package com.skyeye.school.lectures.service.impl;


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
import com.skyeye.school.lectures.dao.LecturesAttenanceRecoredDao;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredChildService;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        initVersionByFromId(entity);
    }

    private void initVersionByFromId(LecturesAttenanceRecored entity) {
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
                            (ObjectUtil.isEmpty(entity.getSmallVersion()) ? entity.getSmallVersion() : CommonNumConstants.NUM_ZERO) + CommonNumConstants.NUM_ONE
                    );
                } else {
                    // 大版本升级
                    entity.setLargeVersion(
                            (ObjectUtil.isEmpty(entity.getLargeVersion()) ? entity.getLargeVersion() : CommonNumConstants.NUM_ZERO) + CommonNumConstants.NUM_ONE
                    );
                    // 大版本升级时重置小版本
                    entity.setSmallVersion(CommonNumConstants.NUM_ZERO);
                }

            } catch (CustomException e) {
                log.error("版本号格式错误", e);
                entity.setVersionNo("1");
                entity.setLargeVersion(0);
                entity.setSmallVersion(0);
            }
        }
    }

    @Override
    public QueryWrapper<LecturesAttenanceRecored> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LecturesAttenanceRecored> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesAttenanceRecored::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        return queryWrapper;
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
    protected void writePostpose(LecturesAttenanceRecored entity, String userId) {
        super.writePostpose(entity, userId);
        entity.getLecturesAttenanceRecoredChildList().forEach(attenanceRecoredChild -> attenanceRecoredChild.setAttenanceRecordId(entity.getId()));
        lecturesAttenanceRecoredChildService.createEntity(entity.getLecturesAttenanceRecoredChildList(), userId);
    }

    @Override
    protected void updatePostpose(LecturesAttenanceRecored entity, String userId) {
        List<LecturesAttenanceRecoredChild> lecturesAttenanceRecoredChildList = entity.getLecturesAttenanceRecoredChildList();
        if (ObjectUtil.isNotEmpty(lecturesAttenanceRecoredChildList)) {
            lecturesAttenanceRecoredChildService.updateEntity(lecturesAttenanceRecoredChildList, userId);
        }
    }

    @Override
    public String createEntity(LecturesAttenanceRecored entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(LecturesAttenanceRecored entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
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
        if (lecturesAttenanceRecored.getLecturesAttenanceRecoredChildList() != null) {
            lecturesAttenanceRecoredChildService.setDataMation(lecturesAttenanceRecored.getLecturesAttenanceRecoredChildList(), LecturesAttenanceRecoredChild::getAttenanceRecordId);

            lecturesAttenanceRecored.getLecturesAttenanceRecoredChildList().forEach(lecturesAttenanceRecoredChild -> {
                lecturesAttenanceRecoredChild.setOpen(true);
            });
        }
        lecturesAttenanceRecoredChildService.setDataMation(lecturesAttenanceRecored, LecturesAttenanceRecored::getAttendLectureTeacherId);
        return lecturesAttenanceRecored;
    }


}
