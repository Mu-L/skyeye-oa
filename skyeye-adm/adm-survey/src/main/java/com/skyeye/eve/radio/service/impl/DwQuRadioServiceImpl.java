package com.skyeye.eve.radio.service.impl;

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
import com.skyeye.common.util.question.CheckType;
import com.skyeye.eve.radio.dao.DwQuRadioDao;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwQuRadioService;
import com.skyeye.exception.CustomException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwQuRadioServiceImpl
 * @Description: 单选题选项服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "单选题选项", groupName = "单选题选项")
public class DwQuRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwQuRadioDao, DwQuRadio> implements DwQuRadioService {

    @Autowired
    private DwQuRadioService dwQuRadioService;
    @Override
    protected QueryWrapper<DwQuRadio> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuRadio> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuRadio> list, String quId, String userId) {
        List<DwQuRadio> quRadio = new ArrayList<>();
        List<DwQuRadio> editquRadio = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DwQuRadio object = list.get(i);
            DwQuRadio bean = new DwQuRadio();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setIsNote(object.getIsNote());
            bean.setOptionTitle(object.getOptionTitle());
            bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            if (object.getCheckType() != null) {
                if (!ToolUtil.isNumeric(object.getCheckType().toString())) {
                    bean.setCheckType(CheckType.valueOf(object.getCheckType().toString()).getIndex());
                } else {
                    bean.setCheckType(object.getCheckType());
                }
            }
            bean.setIsRequiredFill(object.getIsRequiredFill());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRadio.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquRadio.add(bean);
            }
        }
        if (CollectionUtils.isNotEmpty(quRadio)) {
            dwQuRadioService.createEntity(quRadio, userId);
        }
        if (CollectionUtils.isNotEmpty(editquRadio)) {
            dwQuRadioService.updateEntity(editquRadio, userId);
        }
    }

//    @Override
//    protected void deletePreExecution(DwQuRadio entity) {
//        Integer visibility = entity.getVisibility();
//        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
//            throw new CustomException("该选项已显示，请先隐藏再删除");
//        }
//    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuRadio::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuRadio> selectQuRadio(String copyFromId) {
        QueryWrapper<DwQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), copyFromId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getBelongId), id);
        List<DwQuRadio> list = list(queryWrapper);
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
