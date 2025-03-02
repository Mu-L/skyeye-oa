/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.classenum.ExamineStateEnum;
import com.skyeye.eve.forum.classenum.NotificationTypeEnum;
import com.skyeye.eve.forum.classenum.ReadEnum;
import com.skyeye.eve.forum.dao.ForumReportDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumNotice;
import com.skyeye.eve.forum.entity.ForumReport;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.forum.service.ForumNoticeService;
import com.skyeye.eve.forum.service.ForumReportService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.ISysDictDataService;
import com.skyeye.exception.CustomException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ForumReportServiceImpl
 * @Description: 论坛内容举报管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛举报管理", groupName = "论坛举报管理")
public class ForumReportServiceImpl extends SkyeyeBusinessServiceImpl<ForumReportDao, ForumReport> implements ForumReportService {

    @Autowired
    private ForumReportDao forumReportDao;

    @Autowired
    private ForumReportService forumReportService;

    @Autowired
    private ForumNoticeService forumNoticeService;

    @Autowired
    private ISysDictDataService iSysDictDataService;

    @Autowired
    private ForumContentService forumContentService;

    @Autowired
    private IAuthUserService iAuthUserService;


    @Override
    public void createPrepose(ForumReport forumReport) {
        iSysDictDataService.setDataMation(forumReport,ForumReport::getReportTypeId);
        String dictName = forumReport.getReportTypeMation().get("dictName").toString();
        if ("其他".equals(dictName) && StrUtil.isEmpty(forumReport.getReportOtherContent())) {
            throw new CustomException("请输入举报内容");
        }
        Map<String, Object> user = InputObject.getLogParamsStatic();
        forumReport.setReportId(user.get("id").toString());
        forumReport.setReportTime(DateUtil.getTimeAndToString());
        forumReport.setExamineState(ExamineStateEnum.NOT_EXAMINE.getKey());
    }

    @Override
    protected void createPostpose(ForumReport entity, String userId) {
        super.createPostpose(entity, userId);
        // 通知举报人
        ForumNotice forumNotice = new ForumNotice();
        forumNotice.setNoticeTitle("举报");
        forumNotice.setNoticeContent("您举报的帖子已提交、等待审核。举报内容为："+entity.getReportDesc());
        forumNotice.setForumId(entity.getForumId());
        forumNotice.setReceiveId(entity.getCreateId());
        forumNotice.setType(NotificationTypeEnum.REPORT.getKey());
        forumNotice.setState(ReadEnum.NO_READ.getKey());
        forumNoticeService.createEntity(forumNotice,null);
        // 通知被举报人
        ForumContent forumContent = forumContentService.selectById(entity.getForumId());
        forumNotice.setNoticeContent("您的帖子被举报、等待审核。举报内容为："+entity.getReportDesc());
        forumNotice.setReceiveId(forumContent.getCreateId());
        forumNoticeService.createEntity(forumNotice,null);
    }

    @Override
    public QueryWrapper<ForumReport> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ForumReport> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ForumReport::getExamineState), commonPageInfo.getState());
        }
        return queryWrapper;
    }

    /**
     * 获取论坛举报列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
//    @Override
//    public void queryReportNoCheckList(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
//        List<Map<String, Object>> beans = forumReportDao.queryReportNoCheckList(map);
//        iSysDictDataService.setNameForMap(beans, "reportTypeId", "reportType");
//        outputObject.setBeans(beans);
//        outputObject.settotal(pages.getTotal());
//    }
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void checkForumReport(InputObject inputObject, OutputObject outputObject) {
        // 校验数据
        Map<String, Object> map = inputObject.getParams();
        if (StrUtil.isEmpty(map.get("state").toString())
            && ExamineStateEnum.EXAMINE_NO_PASS.getKey().toString().equals(map.get("state").toString())) {
            throw new CustomException("审核不通过需要填写未通过的原因");
        }

        ForumReport forumReport = forumReportService.selectById(map.get("id").toString());
        if (ExamineStateEnum.NOT_EXAMINE.getKey().equals(forumReport.getExamineState())) {
            Map<String, Object> currentUser = inputObject.getLogParams();
            forumReport.setExamineId(currentUser.get("id").toString());
            forumReport.setExamineTime(DateUtil.getTimeAndToString());
            // 更新状态
            forumReportService.updateById(forumReport);
            if (ExamineStateEnum.EXAMINE_PASS.getKey().equals(forumReport.getExamineState())) {// 审核通过
                // 通知发帖人
                ForumNotice forumNotice = new ForumNotice();
                forumNotice.setNoticeContent(forumReport.getExamineState().toString());
                forumNotice.setNoticeTitle("违规");
                forumNotice.setReceiveId(forumReport.getCreateId());
                forumNotice.setType(NotificationTypeEnum.REPLY.getKey());
                forumNotice.setState(ReadEnum.NO_READ.getKey());
                forumNoticeService.createEntity(forumNotice, null);
                // 通知举报人
                forumNotice.setReceiveId(forumReport.getReportId());
                forumNotice.setNoticeTitle("违规");
                forumNoticeService.createEntity(forumNotice, null);
            } else if (ExamineStateEnum.EXAMINE_NO_PASS.getKey().equals(forumReport.getExamineState())) {// 审核不通过
                // 审核不通过，通知举报人
                ForumNotice forumNotice = new ForumNotice();
                forumNotice.setNoticeContent(forumReport.getExamineState().toString());
                forumNotice.setNoticeTitle("举报");
                forumNotice.setForumId(forumReport.getForumId());
                forumNotice.setReceiveId(forumReport.getReportId());
                forumNotice.setType(NotificationTypeEnum.REPLY.getKey());
                forumNotice.setState(ReadEnum.NO_READ.getKey());
                forumNoticeService.createEntity(forumNotice, null);
            }
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 举报信息审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void editReportCheckMationById(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Map<String, Object> beans = forumReportDao.queryForumReportStateById(map);
//        if ("1".equals(beans.get("examineState").toString())) {//未审核的状态可以审核
//            Map<String, Object> user = inputObject.getLogParams();
//            map.put("examineId", user.get("id"));
//            map.put("examineTime", DateUtil.getTimeAndToString());
//            int edit = forumReportDao.editReportCheckMationById(map);
//            if (edit == 1) {
//                if (map.get("examineState").toString().equals("2")) {
//                    // 审核通过，通知举报人和发帖人
//                    Map<String, Object> m = forumReportDao.queryForumReportMationById(map);
//                    Map<String, Object> bean = new HashMap<>();
//                    bean.put("id", ToolUtil.getSurFaceId());
//                    bean.put("noticeContent", map.get("examineState"));
//                    bean.put("noticeTitle", "违规");
//                    bean.put("forumId", m.get("forumId"));
//                    bean.put("receiveId", m.get("createId"));
//                    bean.put("type", 1);
//                    bean.put("state", 1);
//                    bean.put("createTime", DateUtil.getTimeAndToString());
//                    // 通知发帖人
//                    forumReportDao.insertForumNoticeMation(bean);
//                    bean.put("id", ToolUtil.getSurFaceId());
//                    bean.put("receiveId", m.get("reportId"));
//                    bean.put("noticeTitle", "举报");
//                    // 通知举报人
//                    forumReportDao.insertForumNoticeMation(bean);
//                } else if (map.get("examineState").toString().equals("3")) {
//                    // 审核不通过，通知举报人
//                    Map<String, Object> m = forumReportDao.queryForumReportMationById(map);
//                    Map<String, Object> bean = new HashMap<>();
//                    bean.put("id", ToolUtil.getSurFaceId());
//                    bean.put("noticeContent", map.get("examineState"));
//                    bean.put("noticeTitle", "举报");
//                    bean.put("forumId", m.get("forumId"));
//                    bean.put("receiveId", m.get("reportId"));
//                    bean.put("type", 1);
//                    bean.put("state", 1);
//                    bean.put("createTime", DateUtil.getTimeAndToString());
//                    forumReportDao.insertForumNoticeMation(bean);
//                }
//            }
//        } else {
//            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
//        }
//    }

    /**
     * 获取论坛举报已审核列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
//    @Override
//    public void queryReportCheckedList(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
//        List<Map<String, Object>> beans = forumReportDao.queryReportCheckedList(map);
//        iSysDictDataService.setNameForMap(beans, "reportTypeId", "reportType");
//        outputObject.setBeans(beans);
//        outputObject.settotal(pages.getTotal());
//    }

    /**
     * 举报详情
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumReportMationToDetails(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = forumReportDao.queryForumReportMationToDetails(map);
        iSysDictDataService.setNameForMap(bean, "reportTypeId", "reportType");
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    @Override
    public ForumReport selectById(String id) {
        ForumReport forumReport = super.selectById(id);
        forumContentService.setDataMation(forumReport, ForumReport::getForumId);
        iAuthUserService.setDataMation(forumReport, ForumReport::getReportId);
        iAuthUserService.setDataMation(forumReport, ForumReport::getExamineId);
        return forumReport;
    }

}
