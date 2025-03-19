package com.skyeye.school.announcement.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.announcement.entity.Announcement;
import com.skyeye.school.announcement.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AnnouncementController
 * @Description: 公告管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "公告管理", tags = "公告管理", modelName = "公告管理")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 添加或修改公告
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeAnnouncement" , value = "新增/编辑公告信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Announcement.class)
    @RequestMapping("/post/AnnouncementController/writeAnnouncement")
    public void writeAnnouncement(InputObject inputObject, OutputObject outputObject) {
        announcementService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询公告信息
      *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAnnouncementById", value = "根据id查询公告信息信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AnnouncementController/queryAnnouncementById")
    public void queryAnnouncementById(InputObject inputObject, OutputObject outputObject) {
        announcementService.selectById(inputObject, outputObject);
    }

    /**
     * 根据id删除公告信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteAnnouncementById", value = "根据ID删除公告信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AnnouncementController/deleteAnnouncementById")
    public void deleteAnnouncementById(InputObject inputObject, OutputObject outputObject) {
        announcementService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据subjectClassesId查询公告
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id="queryAnnouncementListBySubjectClassesId",value = "根据subjectClassesId查询公告",method = "POST",allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectClassesId", name = "subjectClassesId", value = "科目班级id",required = "require")})
    @RequestMapping("/post/AnnouncementController/queryAnnouncementListBySubjectClassesId")
    public void queryAnnouncementListBySubjectClassesId(InputObject inputObject, OutputObject outputObject){
        announcementService.queryAnnouncementListBySubjectClassesId(inputObject, outputObject);  //查询所有的数据
    }

    /**
     * 确认收收公告
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id="confirmAnnouncement",value = "确认收收公告",method = "POST",allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "announcementId", name = "announcementId", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "stuNo", name = "stuNo", value = "学生学号", required = "required"),
    })
    @RequestMapping("/post/AnnouncementController/confirmAnnouncement")
    public void confirmAnnouncement(InputObject inputObject, OutputObject outputObject){
        announcementService.confirmAnnouncement(inputObject, outputObject);
    }
}
