package com.skyeye.eve.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwAnCompChenRadioDao;
import com.skyeye.eve.chen.entity.DwAnCompChenRadio;
import com.skyeye.eve.chen.service.DwAnCompChenRadioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnCompChenRadioServiceImpl
 * @Description: 答卷复合矩阵单选题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷复合矩阵单选题", groupName = "答卷复合矩阵单选题")
public class DwAnCompChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwAnCompChenRadioDao, DwAnCompChenRadio> implements DwAnCompChenRadioService {
    @Override
    public void queryDwAnCompChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnCompChenRadio> dwAnCompChenRadioList = list(queryWrapper);
        outputObject.setBean(dwAnCompChenRadioList);
        outputObject.settotal(dwAnCompChenRadioList.size());
    }

    @Override
    public List<DwAnCompChenRadio> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnCompChenRadio> selectByQuId(String id) {
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getQuId), id);
        return list(queryWrapper);
    }

}
