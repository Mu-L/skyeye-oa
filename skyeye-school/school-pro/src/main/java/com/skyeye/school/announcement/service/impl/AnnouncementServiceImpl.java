package com.skyeye.school.announcement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.announcement.dao.AnnouncementDao;
import com.skyeye.school.announcement.entity.Announcement;
import com.skyeye.school.announcement.entity.AnnouncementRecord;
import com.skyeye.school.announcement.service.AnnouncementRecordService;
import com.skyeye.school.announcement.service.AnnouncementService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @ClassName: AnnouncementServiceImpl
 * @Description: 公告管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "公告管理", groupName = "公告管理")
public class AnnouncementServiceImpl extends SkyeyeBusinessServiceImpl<AnnouncementDao, Announcement> implements AnnouncementService {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRecordService announcementRecordService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private IUserService iUserService;

    @Override
    public void confirmAnnouncement(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("announcementId").toString();
        String stuNo = inputObject.getParams().get("stuNo").toString();
        UpdateWrapper<Announcement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        Announcement announcement = announcementService.getOne(updateWrapper);
        if(ObjectUtil.isEmpty(announcement)){
            throw new CustomException("该公告不存在");
        }
        int total = announcement.getConfirmNum() + announcement.getUnConfirmNum();
        if(announcement.getIsConfirm()==CommonNumConstants.NUM_ZERO){
            throw new CustomException("该公告不需要确认");
        }
        if(announcement.getConfirmNum() == total){
            throw new CustomException("该班级里所有人都已确认");
        } else {
            int flag = 0;
            List<AnnouncementRecord> announcementRecord = announcementRecordService.queryAllData();
            List<AnnouncementRecord> bean = new ArrayList<>();
            for(AnnouncementRecord record : announcementRecord){
                if (record.getAnnouncementId().equals(id)&&record.getStuNo().equals(stuNo)) {
                    bean.add(record);
                    flag = 1;
                    break;
                }
            }
            if(flag == 1){
                outputObject.setBeans(bean);
                outputObject.settotal(bean.size());
            }else {
                updateWrapper.set(MybatisPlusUtil.toColumns(Announcement::getConfirmNum), announcement.getConfirmNum()+1);
                updateWrapper.set(MybatisPlusUtil.toColumns(Announcement::getUnConfirmNum),announcement.getUnConfirmNum()-1);
                update(updateWrapper);
                announcementRecordService.createEntity(inputObject,outputObject);
                announcementService.refreshCache(id);
            }
        }
    }

    @Override
    public void queryAnnouncementListBySubjectClassesId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectClassesId = map.get("subjectClassesId").toString();
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Announcement::getSubjectClassesId), subjectClassesId)
                .orderByDesc(MybatisPlusUtil.toColumns(Announcement::getCreateTime));
        List<Announcement> announcementList = list(queryWrapper);
        if (CollectionUtil.isEmpty(announcementList)) {
            iUserService.setDataMation(announcementList, Announcement::getCreateId);
        } else {
            iAuthUserService.setDataMation(announcementList, Announcement::getCreateId);
        }
        for (Announcement announcement : announcementList) {
            setCheckIsConfirm(announcement);
        }
        outputObject.setBeans(announcementList);
        outputObject.settotal(announcementList.size());
    }

    @Override
    public void validatorEntity(Announcement announcement){
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Announcement::getTitle),announcement.getTitle());
        List<Announcement> announcementList = list(queryWrapper);
        if(CollectionUtil.isNotEmpty(announcementList)&&!announcementList.get(0).getTitle().equals(announcement.getTitle())){
            throw new CustomException("标题重复");
        }
        subjectClassesService.refreshCache(announcement.getSubjectClassesId());
        SubjectClasses subjectClasses = subjectClassesService.selectById(announcement.getSubjectClassesId());
        if (StrUtil.isEmpty(subjectClasses.getId())) {
            throw new CustomException("科目班级不存在");
        }
    }

    @Override
    public Announcement selectById(String id){
        Announcement announcement = super.selectById(id);
        setCheckIsConfirm(announcement);
        iAuthUserService.setDataMation(announcement,Announcement::getCreateId);
        return announcement;
    }

    private void setCheckIsConfirm(Announcement announcement){
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<AnnouncementRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AnnouncementRecord::getAnnouncementId),announcement.getId())
                .eq(MybatisPlusUtil.toColumns(AnnouncementRecord::getCreateId),userId);
        AnnouncementRecord one = announcementRecordService.getOne(queryWrapper);
        announcement.setCheckConfirm(ObjectUtil.isNotEmpty(one));
    }

    @Override
    public void createPrepose(Announcement announcement){
        SubjectClasses subjectClasses = subjectClassesService.selectById(announcement.getSubjectClassesId());
        announcement.setConfirmNum(CommonNumConstants.NUM_ZERO);
        announcement.setUnConfirmNum(subjectClasses.getPeopleNum());
        announcement.setObjectId(subjectClasses.getObjectId());
        announcement.setObjectKey(subjectClasses.getObjectKey());
    }

    @Override
    public void updatePrepose(Announcement announcement){
        String id = announcement.getId();
        SubjectClasses subjectClasses = subjectClassesService.selectById(announcement.getSubjectClassesId());
        announcement.setConfirmNum(CommonNumConstants.NUM_ZERO);
        announcement.setUnConfirmNum(subjectClasses.getPeopleNum());
        announcement.setObjectId(subjectClasses.getObjectId());
        announcement.setObjectKey(subjectClasses.getObjectKey());
        // 删除记录表先前announcedId的记录
        announcementRecordService.deleteRecordByAnnouncementId(id);
    }

    @Override
    public void deleteById(InputObject inputObject,OutputObject outputObject){
        String announcementId = inputObject.getParams().get("id").toString();
        List<Announcement> announcementList = announcementService.queryAllData();
        Announcement announcements = new Announcement();
        for(Announcement announcement : announcementList){
            if(announcementId.equals(announcement.getId())){
                announcements = announcement;
                break;
            }
        }
        if(ObjectUtil.isEmpty(announcements)){
            throw new CustomException("该公告不存在");
        }
        // 该公告不存在或者确认人数为0就直接删除公告表，不必理会记录表
        if(announcements.getConfirmNum() == CommonNumConstants.NUM_ZERO){
            super.deleteById(inputObject,outputObject);
        }else {
            announcementRecordService.deleteById(inputObject,outputObject);
            super.deleteById(inputObject,outputObject);
        }
    }
}
