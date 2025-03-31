package com.skyeye.school.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.chat.dao.UniqueDao;
import com.skyeye.school.chat.entity.Unique;
import com.skyeye.school.chat.service.ChatHistoryService;
import com.skyeye.school.chat.service.UniqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "聊天会话管理", groupName = "聊天会话管理")
public class UniqueServiceImpl extends SkyeyeBusinessServiceImpl<UniqueDao, Unique> implements UniqueService {

    @Autowired
    private ChatHistoryService chatHistoryService;

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
