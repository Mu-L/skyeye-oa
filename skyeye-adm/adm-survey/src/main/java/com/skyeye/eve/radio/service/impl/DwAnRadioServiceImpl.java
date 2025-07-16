package com.skyeye.eve.radio.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.radio.dao.DwAnRadioDao;
import com.skyeye.eve.radio.entity.DwAnRadio;
import com.skyeye.eve.radio.service.DwAnRadioService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnRadioServiceImpl
 * @Description: 答卷单选题保存服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷单选题保存", groupName = "答卷单选题保存")
public class DwAnRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwAnRadioDao, DwAnRadio> implements DwAnRadioService {

    @Override
    public void queryDwAnRadioListById(InputObject inputObject, OutputObject outputObject) {
        String examAnRadioId = inputObject.getParams().get("id").toString();
        QueryWrapper<DwAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examAnRadioId);
        List<DwAnRadio> dwAnRadioList = list(queryWrapper);
        outputObject.setBean(dwAnRadioList);
        outputObject.settotal(dwAnRadioList.size());
    }

    @Override
    public List<DwAnRadio> selectRadioBySurveyId(String surveyId) {
        QueryWrapper<DwAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnRadio> selectRadioByQuId(String id) {
        QueryWrapper<DwAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnRadio::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnRadio>> selectByQuIdAndStuId(List<String> radioIds, String studentId) {
        if (CollectionUtil.isEmpty(radioIds)){
            return new HashMap<>();
        }
        QueryWrapper<DwAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnRadio::getQuId), radioIds)
            .eq(MybatisPlusUtil.toColumns(DwAnRadio::getCreateId), studentId);
        Map<String, List<DwAnRadio>> radioMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnRadio::getQuId));
        return radioMap;
    }
}
