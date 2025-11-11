/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.conference.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.conference.classenum.ConferenceState;
import com.skyeye.eve.conference.dao.ConferenceRoomDao;
import com.skyeye.eve.conference.entity.ConferenceRoom;
import com.skyeye.eve.conference.service.ConferenceRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConferenceRoomServiceImpl
 * @Description: 会议室管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:08
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "会议室管理", groupName = "会议室模块")
public class ConferenceRoomServiceImpl extends SkyeyeBusinessServiceImpl<ConferenceRoomDao, ConferenceRoom> implements ConferenceRoomService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "roomAdmin", "roomAdminMation");
        return beans;
    }

    @Override
    public void createPrepose(ConferenceRoom entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        entity.setRoomNum(oddNumber);
        entity.setState(ConferenceState.NORMAL.getKey());
    }

    @Override
    public ConferenceRoom selectById(String id) {
        ConferenceRoom conferenceRoom = super.selectById(id);
        conferenceRoom.setRoomAdminMation(iAuthUserService.queryDataMationById(conferenceRoom.getRoomAdmin()));
        return conferenceRoom;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void normalConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ConferenceRoom conferenceRoom = selectById(id);
        if (conferenceRoom.getState().equals(ConferenceState.REPAIR.getKey()) || conferenceRoom.getState().equals(ConferenceState.SCRAP.getKey())) {
            // 维修或者报废可以恢复正常
            UpdateWrapper<ConferenceRoom> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ConferenceRoom::getState), ConferenceState.NORMAL.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void repairConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ConferenceRoom conferenceRoom = selectById(id);
        if (conferenceRoom.getState().equals(ConferenceState.NORMAL.getKey()) || conferenceRoom.getState().equals(ConferenceState.SCRAP.getKey())) {
            // 正常或者报废可以维修
            UpdateWrapper<ConferenceRoom> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ConferenceRoom::getState), ConferenceState.REPAIR.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void scrapConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ConferenceRoom conferenceRoom = selectById(id);
        if (conferenceRoom.getState().equals(ConferenceState.NORMAL.getKey()) || conferenceRoom.getState().equals(ConferenceState.REPAIR.getKey())) {
            // 正常或者维修可以报废
            UpdateWrapper<ConferenceRoom> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ConferenceRoom::getState), ConferenceState.SCRAP.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    @Override
    public void queryAllConferenceRoomList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ConferenceRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ConferenceRoom::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ConferenceRoom::getState), ConferenceState.NORMAL.getKey());
        List<ConferenceRoom> conferenceRoomList = list(queryWrapper);
        outputObject.setBeans(conferenceRoomList);
        outputObject.settotal(conferenceRoomList.size());
    }

}
