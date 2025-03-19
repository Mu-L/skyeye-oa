package com.skyeye.eve.orderby.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.orderby.dao.DwQuOrderbyDao;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.orderby.service.DwQuOrderbyService;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "排序题行选项管理", groupName = "排序题行选项管理")
public class DwQuOrderbyServiceImpl extends SkyeyeBusinessServiceImpl<DwQuOrderbyDao, DwQuOrderby> implements DwQuOrderbyService {

    @Override
    protected QueryWrapper<DwQuOrderby> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuOrderby> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuOrderby> orderby, String quId, String userId) {
        List<DwQuOrderby> quOrderBy = new ArrayList<>();
        List<DwQuOrderby> editquOrderBy = new ArrayList<>();
        for (int i = 0; i < orderby.size(); i++) {
            DwQuOrderby object = orderby.get(i);
            DwQuOrderby bean = new DwQuOrderby();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quOrderBy.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquOrderBy.add(bean);
            }
        }
        if (!quOrderBy.isEmpty()) {
            createEntity(quOrderBy, userId);
        }
        if (!editquOrderBy.isEmpty()) {
            updateEntity(editquOrderBy, userId);
        }
        quOrderBy.addAll(editquOrderBy);
    }

    @Override
    protected void deletePreExecution(DwQuOrderby entity) {
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuOrderby::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuOrderby> selectQuOrderby(String copyFromId) {
        QueryWrapper<DwQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuOrderby::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getBelongId), id);
        List<DwQuOrderby> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item -> {
            String quId = item.getQuId();
            if (result.containsKey(quId)) {
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            } else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId, tmp);
            }
        });
        return result;
    }
}
