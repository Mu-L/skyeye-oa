package com.skyeye.school.groups.service.impl;

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
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.school.groups.dao.GroupsDao;
import com.skyeye.school.groups.entity.Groups;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.groups.service.GroupsService;
import com.skyeye.school.groups.service.GroupsStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "学生分组管理", groupName = "分组管理")
public class GroupServiceImpl extends SkyeyeBusinessServiceImpl<GroupsDao, Groups> implements GroupsService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public QueryWrapper<Groups> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Groups> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 学生分组信息下的所有分组
        queryWrapper.eq(MybatisPlusUtil.toColumns(Groups::getGroupsInformationId), commonPageInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Groups::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);

        return beans;
    }

    @Override
    public void insertList(GroupsInformation groupsInformation) {
        Integer status = groupsInformation.getStatus();
        List<Groups> groupsList = new ArrayList<>();
        if (status.equals(CommonNumConstants.NUM_ZERO)) {
            Integer groNumber = groupsInformation.getGroNumber();
            getGroupList(groupsInformation, groNumber, groupsList);
            super.createEntity(groupsList, groupsInformation.getCreateId());
            List<String> groupsIds = groupsList.stream().map(Groups::getId).collect(Collectors.toList());
            refreshCache(groupsIds);
        }
        if (status.equals(CommonNumConstants.NUM_ONE)) {
            Integer groupsNumber = groupsInformation.getGroupsNumber();
            getGroupList(groupsInformation, groupsNumber, groupsList);
            super.createEntity(groupsList, groupsInformation.getCreateId());
            List<String> groupsIds = groupsList.stream().map(Groups::getId).collect(Collectors.toList());
            refreshCache(groupsIds);
        }
    }

    private void getGroupList(GroupsInformation groupsInformation, Integer groupsNumber, List<Groups> groupsList) {
        for (int i = 1; i <= groupsNumber; i++) {
            Groups entity = getGroups(groupsInformation, i);
            entity.setGroupsInformationId(groupsInformation.getId());
            groupsList.add(entity);
        }
    }

    private Groups getGroups(GroupsInformation groupsInformation, int i) {
        Groups entity = new Groups();
        entity.setGroupName("第" + i + "组");
        entity.setGroupsInformationId(groupsInformation.getId());
        String imgPath = tPath.replace("images", StrUtil.EMPTY + entity.getGroupBarcode());
        //生成分组编码
        String code = ToolUtil.getFourWord();
        entity.setGroupBarcode(code);
        //生成分组二维码
        String content = QRCodeLinkType.getJsonStrByType(QRCodeLinkType.STUDENT_CHECKWORK.getKey(), code);
        String sourCodeLogo = QRCodeLogoUtil.encode(content, imgPath, tPath, true, FileConstants.FileUploadPath.SCHOOL_SUBJECT.getType()[0]);
        entity.setGrCodeUrl(sourCodeLogo);
        return entity;
    }

    @Autowired
    private GroupsService groupsService;
    @Autowired
    private GroupsStudentService groupsStudentService;
    @Override
    public void deleteGroups(String groupsInformationId) {
        QueryWrapper<Groups> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Groups::getGroupsInformationId), groupsInformationId);
        List<Groups> groupsList = list(queryWrapper);
        List<String> groupsIds = groupsList.stream().map(Groups::getId).collect(Collectors.toList());
        groupsStudentService.deleteByGroupsIds(groupsIds);
        remove(queryWrapper);
    }

    @Override
    public void deleteGroupsById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<Groups> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        remove(updateWrapper);
    }

    @Override
    public void changeState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Integer state = (Integer) map.get("state");
        UpdateWrapper<Groups> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Groups::getState), state);
        update(updateWrapper);
    }

}