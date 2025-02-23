/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwAnChenRadioDao;
import com.skyeye.eve.chen.entity.DwAnChenRadio;
import com.skyeye.eve.chen.service.DwAnChenRadioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnChenRadioServiceImpl
 * @Description: 答卷矩阵单选题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷矩阵单选题", groupName = "答卷矩阵单选题")
public class DwAnChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwAnChenRadioDao, DwAnChenRadio> implements DwAnChenRadioService {

    @Override
    public void queryDwAnChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnChenRadio> dwAnChenRadioList = list(queryWrapper);
        outputObject.setBean(dwAnChenRadioList);
        outputObject.settotal(dwAnChenRadioList.size());
    }

    @Override
    public List<DwAnChenRadio> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

}