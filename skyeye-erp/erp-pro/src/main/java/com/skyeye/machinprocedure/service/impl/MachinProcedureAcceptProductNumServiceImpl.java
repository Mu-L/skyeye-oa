package com.skyeye.machinprocedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.machinprocedure.dao.MachinProcedureAcceptProductNumDao;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptProductNum;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptProductNumService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "工序验收员工生产数量", groupName = "工序验收", manageShow = false)
public class MachinProcedureAcceptProductNumServiceImpl extends SkyeyeBusinessServiceImpl<MachinProcedureAcceptProductNumDao, MachinProcedureAcceptProductNum> implements MachinProcedureAcceptProductNumService {

    @Override
    public void writeList(String parentId, List<MachinProcedureAcceptProductNum> machinProcedureAcceptProductNumList) {
        // 查询验收单下的员工生产数量信息
        List<MachinProcedureAcceptProductNum> oldList = queryListByParentIdOnly(parentId);
        if (CollectionUtil.isEmpty(oldList)) {
            super.createEntity(machinProcedureAcceptProductNumList, StrUtil.EMPTY);
        }
        // 根据员工id进行分组
        Map<String, MachinProcedureAcceptProductNum> mapProductNum = machinProcedureAcceptProductNumList.stream().collect(Collectors.toMap(MachinProcedureAcceptProductNum::getStaffId, e -> e));
        // 数据库未存在的信息
        List<MachinProcedureAcceptProductNum> insertList = oldList.stream().filter(oldEntity -> !mapProductNum.containsKey(oldEntity.getStaffId())).collect(Collectors.toList());
        // 数据库已存在的信息
        List<MachinProcedureAcceptProductNum> updateList = oldList.stream().filter(oldEntity -> mapProductNum.containsKey(oldEntity.getStaffId())).collect(Collectors.toList());
        // 更新数据库已存在的数据
        for (MachinProcedureAcceptProductNum updateEntity : updateList) {
            updateEntity.setAllNumber(mapProductNum.get(updateEntity.getStaffId()).getAllNumber() + updateEntity.getAllNumber());
            updateEntity.setQualifiedNum(mapProductNum.get(updateEntity.getStaffId()).getQualifiedNum() + updateEntity.getQualifiedNum());
            updateEntity.setReworkNum(mapProductNum.get(updateEntity.getStaffId()).getReworkNum() + updateEntity.getReworkNum());
            updateEntity.setScrapNum(mapProductNum.get(updateEntity.getStaffId()).getScrapNum() + updateEntity.getScrapNum());
        }
        super.updateEntity(updateList, StrUtil.EMPTY);
        super.createEntity(insertList, StrUtil.EMPTY);
    }

    @Override
    public List<MachinProcedureAcceptProductNum> queryListByParentId(String parentId) {
        List<MachinProcedureAcceptProductNum> productNumList = queryListByParentIdOnly(parentId);
        iAuthUserService.setDataMation(productNumList, MachinProcedureAcceptProductNum::getStaffId);
        return productNumList;
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<MachinProcedureAcceptProductNum> queryMachinProcedureAcceptProductNumByStaffId(String staffId) {
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getStaffId), staffId);
        return list(queryWrapper);
    }

    public List<MachinProcedureAcceptProductNum> queryListByParentIdOnly(String parentId) {
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getParentId), parentId);
        return list(queryWrapper);
    }

}
