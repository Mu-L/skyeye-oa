package com.skyeye.school.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.chat.dao.UniqueDao;
import com.skyeye.school.chat.entity.Unique;
import com.skyeye.school.chat.service.UniqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "聊天会话管理", groupName = "聊天会话管理")
public class UniqueServiceImpl extends SkyeyeBusinessServiceImpl<UniqueDao, Unique> implements UniqueService {

    @Autowired
    private IUserService iUserService;

    @Override
    public void queryMyChatMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<Unique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Unique::getSendId), userId)
            .or()
            .eq(MybatisPlusUtil.toColumns(Unique::getReceiveId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Unique::getCreateTime));
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(Unique::getUniqueId));
        queryWrapper.last("LIMIT 50");
        List<Unique> uniqueList = list(queryWrapper);
        if (CollectionUtil.isEmpty(uniqueList)) {
            throw new CustomException("没有聊天信息列表");
        }
        List<String> userIds = uniqueList.stream().map(Unique::getSendId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(userIds)) {
            List<String> receiveIds = uniqueList.stream().map(Unique::getReceiveId).collect(Collectors.toList());
            userIds.addAll(receiveIds);
            userIds = userIds.stream().distinct().collect(Collectors.toList());
        }
        // 教师信息
        Map<String, Map<String, Object>> userMap = iAuthUserService.queryUserNameList(userIds);
        // 学生信息
        String userIdsStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds);
        List<Map<String, Object>> studentList = iUserService.queryEntityMationByIds(userIdsStr);
        Map<String, Map<String, Object>> studentMap = studentList.stream()
            .collect(Collectors.toMap(
                student -> (String) student.get("id"),
                student -> student,
                (existing, replacement) -> existing
            ));
        for (Unique unique : uniqueList) {
            // 获取发送者信息
            Map<String, Object> sendTeacherInfo = userMap.get(unique.getSendId());
            Map<String, Object> sendStudentInfo = studentMap.get(unique.getSendId());

            // 获取接收者信息
            Map<String, Object> receiveTeacherInfo = userMap.get(unique.getReceiveId());
            Map<String, Object> receiveStudentInfo = studentMap.get(unique.getReceiveId());
            unique.setSendTeacher(sendTeacherInfo);
            unique.setSendStudent(sendStudentInfo);
            unique.setReceiveTeacher(receiveTeacherInfo);
            unique.setReceiveStudent(receiveStudentInfo);
        }
        outputObject.setBeans(uniqueList);
        outputObject.settotal(uniqueList.size());
    }

    @Override
    public Unique quesyUniqueIsExist(String uniqueId) {
        QueryWrapper<Unique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Unique::getUniqueId), uniqueId);
        Unique unique = getOne(queryWrapper);
        return unique;
    }

    @Override
    public void deleteMyChatUniqueList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String uniqueId = inputObject.getParams().get("uniqueId").toString();
        QueryWrapper<Unique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Unique::getUniqueId), uniqueId)
            .and(wrapper -> wrapper.eq(MybatisPlusUtil.toColumns(Unique::getSendId), userId)
                .or()
                .eq(MybatisPlusUtil.toColumns(Unique::getReceiveId), userId));
        boolean success = remove(queryWrapper);
        if (success) {
            outputObject.setreturnMessage("会话删除成功");
        } else {
            outputObject.setreturnMessage("会话删除失败");
        }
    }

}
