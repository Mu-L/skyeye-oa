package com.skyeye.eve.radio.service.impl;

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

import java.util.List;

/**
 * @ClassName: DwAnRadioServiceImpl
 * @Description: 答卷单选题保存服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷单选题保存", groupName = "答卷单选题保存", manageShow = false)
public class DwAnRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwAnRadioDao, DwAnRadio> implements DwAnRadioService {

    @Override
    public void queryDwAnRadioListById(InputObject inputObject, OutputObject outputObject) {
        String examAnRadioId = inputObject.getParams().get("id").toString();
        QueryWrapper<DwAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID,examAnRadioId);
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
}
