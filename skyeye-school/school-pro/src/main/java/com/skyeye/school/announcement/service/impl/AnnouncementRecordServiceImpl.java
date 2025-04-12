package com.skyeye.school.announcement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.school.announcement.dao.AnnouncementRecordDao;
import com.skyeye.school.announcement.entity.Announcement;
import com.skyeye.school.announcement.entity.AnnouncementRecord;
import com.skyeye.school.announcement.service.AnnouncementRecordService;
import com.skyeye.school.announcement.service.AnnouncementService;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName: AnnouncementRecordServiceImpl
 * @Description: 公告收到记录管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "公告收到记录管理", groupName = "公告收到记录管理")
public class AnnouncementRecordServiceImpl extends SkyeyeBusinessServiceImpl<AnnouncementRecordDao,AnnouncementRecord> implements AnnouncementRecordService {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private AnnouncementRecordService announcementRecordService;

    @Autowired
    private ICertificationRest iCertificationRest;

    @Override
    public void queryRecordByAnnouncementId(InputObject inputObject, OutputObject outputObject) {
        String announcementId = inputObject.getParams().get("announcementId").toString();
        List<AnnouncementRecord> announcementRecordList = queryAllData();
        List<String> stuNoList = new ArrayList<>();
        for (AnnouncementRecord announcementRecord : announcementRecordList) {
            if (announcementRecord.getAnnouncementId().equals(announcementId)) {
                stuNoList.add(announcementRecord.getStuNo());
            }
        }
        if(!stuNoList.isEmpty()){
            List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
                    iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
            outputObject.setBeans(userList);
            outputObject.settotal(userList.size());
        }
    }

    @Override
    public void queryUnConfirmRecordByAnnouncementId(InputObject inputObject, OutputObject outputObject) {
        String announcementId = inputObject.getParams().get("announcementId").toString();
        Announcement announcement = announcementService.selectById(announcementId);
        if(ObjectUtil.isNotEmpty(announcement)){
            String subjectClassesId = announcement.getSubjectClassesId();
            if(announcement.getConfirmNum() == CommonNumConstants.NUM_ZERO || announcement.getIsConfirm() == CommonNumConstants.NUM_ZERO){
                List<Map<String, Object>> stuNoList = subjectClassesStuService.queryClassStuIds(subjectClassesId);
                outputObject.setBeans(stuNoList);
                outputObject.settotal(stuNoList.size());
            }else {
                int flag = 0;
                List<String> stuNoList = new ArrayList<>();
                QueryWrapper<AnnouncementRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(AnnouncementRecord::getAnnouncementId), announcementId);
                List<AnnouncementRecord> announcementRecords = announcementRecordService.list(queryWrapper);
                QueryWrapper<SubjectClassesStu> subjectClassesStuQueryWrapper = new QueryWrapper<>();
                subjectClassesStuQueryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subjectClassesId);
                List<SubjectClassesStu> subjectClassesStus = subjectClassesStuService.list(subjectClassesStuQueryWrapper);
                for(SubjectClassesStu subjectClassesStu : subjectClassesStus){
                   for(AnnouncementRecord announcementRecord : announcementRecords){
                       if(announcementRecord.getStuNo().equals(subjectClassesStu.getStuNo())){
                           break;
                       }else {
                           flag += 1;
                       }
                   }
                   if(announcementRecords.size() == flag){
                       stuNoList.add(subjectClassesStu.getStuNo());
                   }
                   flag = 0;
                }
                List<Map<String,Object>> userList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(stuNoList)){
                    userList = ExecuteFeignClient.get(() ->
                        iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
                }
                outputObject.setBeans(userList);
                outputObject.settotal(userList.size());
            }
        }
    }

    @Override
    public  void  validatorEntity(AnnouncementRecord announcementRecord) {
        super.validatorEntity(announcementRecord);
        Announcement announcements = getAnnouncementById(announcementRecord);
        if(ObjectUtils.isEmpty(announcements)){
            throw new CustomException("该公告不存在");
        }
        if(announcements.getIsConfirm() == CommonNumConstants.NUM_ZERO){
            //为0就不需要确认，也就不需要添加到记录表中
            throw new CustomException("该公告不需要确认");
        }
    }
    @Override
    public void createPrepose(AnnouncementRecord announcementRecord) {
        Announcement announcements = getAnnouncementById(announcementRecord);
        announcementRecord.setTitle(announcements.getTitle());
        announcementRecord.setObjectId(announcements.getObjectId());
        announcementRecord.setObjectKey(announcements.getObjectKey());
        announcementRecord.setSubjectClassesId(announcements.getSubjectClassesId());
        announcementRecord.setState(CommonNumConstants.NUM_ONE);
    }

    private Announcement getAnnouncementById(AnnouncementRecord announcementRecord) {
        String announcementId = announcementRecord.getAnnouncementId(); //公告id
        List<Announcement> announcementList = announcementService.queryAllData();
        Announcement announcements = new Announcement();
        for (Announcement announcement : announcementList) {
            if(announcement.getId().equals(announcementId)){
                announcements = announcement;
                break;
            }
        }
        return announcements;
    }

    @Override
    public void deleteById(InputObject inputObject,OutputObject outputObject){
        String announcementId = inputObject.getParams().get("id").toString();
        QueryWrapper<AnnouncementRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.eq(MybatisPlusUtil.toColumns(AnnouncementRecord::getAnnouncementId), announcementId);
        List<AnnouncementRecord> announcementRecordList = list(recordQueryWrapper);
        List<String> ids = new ArrayList<>();
        for (AnnouncementRecord announcementRecord : announcementRecordList) {
            if (announcementRecord.getAnnouncementId().equals(announcementId)) {
               ids.add(announcementRecord.getId());
            }
        }
        announcementRecordService.deleteById(ids);
    }

    @Override
    public void deleteRecordByAnnouncementId(String announcementId){
        QueryWrapper<AnnouncementRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.eq(MybatisPlusUtil.toColumns(AnnouncementRecord::getAnnouncementId), announcementId);
        remove(recordQueryWrapper);
    }

    @Override
    public Map<String, List<AnnouncementRecord>> queryRecordByAnnouncementIdAndStu(List<String> annIds, String stuNo) {
        if (CollectionUtil.isEmpty(annIds)){
            return new HashMap<>();
        }
        QueryWrapper<AnnouncementRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.in(MybatisPlusUtil.toColumns(AnnouncementRecord::getAnnouncementId), annIds);
        recordQueryWrapper.eq(MybatisPlusUtil.toColumns(AnnouncementRecord::getStuNo), stuNo);
        List<AnnouncementRecord> announcementRecordList = list(recordQueryWrapper);
        Map<String, List<AnnouncementRecord>> collect = announcementRecordList.stream().collect(Collectors.groupingBy(AnnouncementRecord::getAnnouncementId));
        return collect;
    }

    @Override
    public void deleteBatchByAnnouncementIds(ArrayList<String> strings) {
        QueryWrapper<AnnouncementRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.in(MybatisPlusUtil.toColumns(AnnouncementRecord::getAnnouncementId), strings);
        remove(recordQueryWrapper);
    }
}
