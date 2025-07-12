///*******************************************************************************
// * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
// ******************************************************************************/
//
//package com.skyeye.eve.xxljob;
//
//import cn.hutool.json.JSONUtil;
//import com.skyeye.common.util.DateUtil;
//import com.skyeye.eve.servey.dao.DwSurveyDirectoryDao;
//import com.skyeye.eve.service.IQuartzService;
//import com.xxl.job.core.context.XxlJobHelper;
//import com.xxl.job.core.handler.annotation.XxlJob;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @ClassName: EndSurveyMationService
// * @Description: 问卷调查
// * @author: skyeye云系列--卫志强
// * @date: 2022/8/7 16:52
// * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
// * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
// */
//@Component
//public class EndSurveyMationService {
//
//    @Autowired
//    private DwSurveyDirectoryDao dwSurveyDirectoryDao;
//
//    @Autowired
//    private IQuartzService iQuartzService;
//
//    @XxlJob("endSurveyMationService")
//    public void call() {
//        String param = XxlJobHelper.getJobParam();
//        Map<String, String> paramMap = JSONUtil.toBean(param, null);
//        String surveyId = paramMap.get("objectId");
//        // 获取问卷信息
//        Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(surveyId);
//        if ("1".equals(surveyMation.get("surveyState").toString())) {
//            // 执行中可以设置结束时间
//            Map<String, Object> map = new HashMap<>();
//            map.put("id", surveyId);
//            map.put("realEndTime", DateUtil.getTimeAndToString());
//            dwSurveyDirectoryDao.editSurveyStateToEndNumZdById(map);
//        }
//        iQuartzService.stopAndDeleteTaskQuartz(paramMap.get("objectId"));
//    }
//
//}
