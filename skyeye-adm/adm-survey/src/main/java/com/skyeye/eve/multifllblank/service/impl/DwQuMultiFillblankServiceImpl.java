package com.skyeye.eve.multifllblank.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.multifllblank.dao.DwQuMultiFillblankDao;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "多行填空题管理", groupName = "多行填空题管理")
public class DwQuMultiFillblankServiceImpl extends SkyeyeBusinessServiceImpl<DwQuMultiFillblankDao, DwQuMultiFillblank> implements DwQuMultiFillblankService {

    @Override
    protected QueryWrapper<DwQuMultiFillblank> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuMultiFillblank> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuMultiFillblank> list, String quId, String userId) {
        List<DwQuMultiFillblank> quMultiFillblank = new ArrayList<>();
        List<DwQuMultiFillblank> editquMultiFillblank = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DwQuMultiFillblank object = list.get(i);
            DwQuMultiFillblank bean = new DwQuMultiFillblank();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quMultiFillblank.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquMultiFillblank.add(bean);
            }
        }
        if (!quMultiFillblank.isEmpty()) {
            createEntity(quMultiFillblank, userId);
        }
        if (!editquMultiFillblank.isEmpty()) {
            updateEntity(editquMultiFillblank, userId);
        }
    }

    @Override
    protected void deletePreExecution(DwQuMultiFillblank entity) {
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId),quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuMultiFillblank> selectQuMultiFillblank(String copyFromId) {
        QueryWrapper<DwQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId),copyFromId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getBelongId),id);
        List<DwQuMultiFillblank> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item->{
            String quId = item.getQuId();
            if(result.containsKey(quId)){
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            }else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId,tmp);
            }
        });
        return result;
    }

}
