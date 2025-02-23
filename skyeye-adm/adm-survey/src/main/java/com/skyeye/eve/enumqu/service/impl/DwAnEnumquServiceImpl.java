package com.skyeye.eve.enumqu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.enumqu.dao.DwAnEnumquDao;
import com.skyeye.eve.enumqu.entity.DwAnEnumqu;
import com.skyeye.eve.enumqu.service.DwAnEnumquService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnEnumquServiceImpl
 * @Description: 答卷 枚举题答案服务层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "枚举题答案管理", groupName = "枚举题答案管理")
public class DwAnEnumquServiceImpl extends SkyeyeBusinessServiceImpl<DwAnEnumquDao, DwAnEnumqu> implements DwAnEnumquService {

    @Override
    public void queryDwAnEnumquListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnEnumqu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnEnumqu> dwAnEnumquList = list(queryWrapper);
        outputObject.setBean(dwAnEnumquList);
        outputObject.settotal(dwAnEnumquList.size());
    }

    @Override
    public List<DwAnEnumqu> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnEnumqu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnEnumqu::getBelongId), surveyId);
        return list(queryWrapper);
    }
}
