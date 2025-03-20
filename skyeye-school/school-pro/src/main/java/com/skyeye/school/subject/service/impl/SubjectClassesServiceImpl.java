/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.QRCodeLinkType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.school.announcement.service.AnnouncementService;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.assignment.service.AssignmentSubService;
import com.skyeye.school.datum.service.DatumService;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.measurement.service.MeasurementService;
import com.skyeye.school.measurement.service.MeasurementSubService;
import com.skyeye.school.score.service.ScoreTypeService;
import com.skyeye.school.semester.service.SemesterService;
import com.skyeye.school.subject.dao.SubjectClassesDao;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectService;
import com.skyeye.school.topic.service.TopicService;
import com.skyeye.school.topiccomment.service.TopicCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SubjectClassesServiceImpl
 * @Description: 科目表与班级表的关系服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/10 14:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "科目表与班级表的关系管理", groupName = "科目管理")
public class SubjectClassesServiceImpl extends SkyeyeBusinessServiceImpl<SubjectClassesDao, SubjectClasses> implements SubjectClassesService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    private static Logger LOGGER = LoggerFactory.getLogger(SubjectClassesServiceImpl.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private ScoreTypeService scoreTypeService;
    
    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private DatumService datumService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicCommentService topicCommentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentSubService assignmentSubService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private MeasurementSubService measurementSubService;

    @Override
    public QueryWrapper<SubjectClasses> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SubjectClasses> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        classesService.setMationForMap(beans, "classesId", "classesMation");
        return beans;
    }

    @Override
    public void queryNoPageSubjectClassesList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), inputObject.getParams().get("objectId").toString());
        List<SubjectClasses> list = list(queryWrapper);
        classesService.setDataMation(list, SubjectClasses::getClassesId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void createPrepose(SubjectClasses entity) {
        Subject subject = subjectService.selectById(entity.getObjectId());
        String imgPath = tPath.replace("images", StrUtil.EMPTY) + subject.getImg();
        // 生成加课码编码
        String code = ToolUtil.getFourWord();
        entity.setSourceCode(code);
        // 生成二维码
        String content = QRCodeLinkType.getJsonStrByType(QRCodeLinkType.SUBJECT_CLASSES.getKey(), code);
        String sourceCodeLogo = QRCodeLogoUtil.encode(content, imgPath, tPath, true, FileConstants.FileUploadPath.SCHOOL_SUBJECT.getType()[0]);
        entity.setSourceCodeLogo(sourceCodeLogo);
        entity.setPeopleNum(CommonNumConstants.NUM_ZERO);
        refreshCache(entity.getId());
    }

    @Override
    public void createPostpose(SubjectClasses entity, String userId) {
        scoreTypeService.createDeFaultInfo(entity, userId);
    }

    @Override
    public void deletePostpose(SubjectClasses entity) {
        FileUtil.deleteFile(tPath.replace("images", StrUtil.EMPTY) + entity.getSourceCode());
    }

    @Override
    public SubjectClasses selectById(String id) {
        SubjectClasses subjectClasses = super.selectById(id);
        classesService.setDataMation(subjectClasses, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClasses, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClasses, SubjectClasses::getSemesterId);
        refreshCache(subjectClasses.getCreateId());//刷新缓存
        return subjectClasses;
    }

    @Override
    public List<SubjectClasses> selectByIds(String... ids) {
        List<SubjectClasses> subjectClassesList = super.selectByIds(ids);
        classesService.setDataMation(subjectClassesList, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClassesList, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClassesList, SubjectClasses::getSemesterId);
        refreshCache(subjectClassesList.get(0).getId());//刷新缓存
        return subjectClassesList;
    }

    @Override
    public void querySubjectClassesBySourceCode(InputObject inputObject, OutputObject outputObject) {
        String sourceCode = inputObject.getParams().get("sourceCode").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getSourceCode), sourceCode);
        SubjectClasses subjectClasses = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(subjectClasses)) {
            throw new CustomException("该加课码对应的课程信息不存在");
        }
        subjectClasses = selectById(subjectClasses.getId());
        outputObject.setBean(subjectClasses);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public List<SubjectClasses> querySubjectClassesByObjectId(String... objectId) {
        List<String> objectIdList = Arrays.asList(objectId);
        if (CollectionUtil.isEmpty(objectIdList)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), objectIdList);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        List<String> ids = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        return selectByIds(ids.toArray(new String[ids.size()]));
    }

    @Override
    public void editSubjectClassesPeopleNum(String id, Boolean isAdd) {
        String lockKey = String.format("editSubjectClassesPeopleNum_%s", id);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                throw new CustomException("增减人员失败，当前并发量较大，请稍后再次尝试.");
            }
            LOGGER.info("get lock success, lockKey is {}.", lockKey);
            SubjectClasses subjectClasses = selectById(id);
            if (isAdd) {
                // 新增
                UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), subjectClasses.getPeopleNum() + CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            } else {
                // 减少
                UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), subjectClasses.getPeopleNum() - CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            }
            refreshCache(id);
            LOGGER.info("editSubjectClassesPeopleNum is success.");
        } catch (Exception ee) {
            LOGGER.warn("editSubjectClassesPeopleNum error, because {}", ee);
            if (ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void updateEnabled(String SubjectClassesId, Integer isEnabled) {
        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, SubjectClassesId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getEnabled), isEnabled);
        update(updateWrapper);
        refreshCache(SubjectClassesId);
    }

    public void updateQuit(String SubjectClassesId, Integer isQuit) {
        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, SubjectClassesId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getQuit), isQuit);
        update(updateWrapper);
        refreshCache(SubjectClassesId);
    }

    public void changeEnabled(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String enabled = map.get("enabled").toString();
        if (enabled.equals(CommonNumConstants.NUM_ONE.toString()) || enabled.equals(CommonNumConstants.NUM_TWO.toString())) {
            String subjectClassesId = map.get("id").toString();
            updateEnabled(subjectClassesId, Integer.parseInt(enabled));
        }
    }

    public void changeQuit(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String quit = map.get("quit").toString();
        if (quit.equals(CommonNumConstants.NUM_ONE.toString()) || quit.equals(CommonNumConstants.NUM_TWO.toString())) {
            String subjectClassesId = map.get("id").toString();
            updateQuit(subjectClassesId, Integer.parseInt(quit));
        }
    }

    @Override
    public void updatePeopleNum(String subClassLinkId, Integer count) {
        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subClassLinkId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), count);
        update(updateWrapper);
    }

    @Override
    public void queryTeacherMessage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, subClassLinkId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        iAuthUserService.setDataMation(subjectClassesList, SubjectClasses::getCreateId);
        outputObject.setBean(subjectClassesList);
        outputObject.settotal(subjectClassesList.size());
    }

    @Override
    public SubjectClasses getSubjectClassesByObjectIdAndClassesId(String objectId, String classesId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classesId);
        return getOne(queryWrapper);}

    @Override
    public void querySubjectClassesInfo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        Map<String, Object> resultMap = new HashMap<>();
        // 获取加课人数
        Long joinNum = subjectClassesStuService.queruClassStuNum(id);
        resultMap.put("joinNum", joinNum);
        // 资料个数
        Long dataNum = datumService.queryClassDataNum(id);
        resultMap.put("dataNum", dataNum);
        // 公告数
        Long noticeNum = announcementService.queryClassNoticeNum(id);
        resultMap.put("noticeNum", noticeNum);
        // 话题发帖数
        Long topicNum = topicService.queryClassTopicNum(id);
        resultMap.put("topicNum", topicNum);
        // 话题参与人数
        Long topicJoinNum = topicCommentService.queryClassTopicJoinNum(id);
        resultMap.put("topicJoinNum", topicJoinNum);
        // 话题参与人次--评论总数
        Long topicJoinPersonNum = topicCommentService.queryClassTopicJoinPersonNum(id);
        resultMap.put("topicJoinPersonNum", topicJoinPersonNum);
        // 作业数
        Long assignmentNum = assignmentService.queryClassAssignmentNum(id);
        resultMap.put("assignmentNum", assignmentNum);
        // 作业参数人数
        Long assignmentJoinNum = assignmentSubService.queryClassAssignmentJoinNum(id);
        resultMap.put("assignmentJoinNum", assignmentJoinNum);
        // 测试数量
        Long testNum = measurementService.queryClassMeasurementNum(id);
        resultMap.put("testNum", testNum);
        // 测试参与人数
        Long testJoinNum = measurementSubService.queryClassMeasurementJoinNum(id);
        resultMap.put("testJoinNum", testJoinNum);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}