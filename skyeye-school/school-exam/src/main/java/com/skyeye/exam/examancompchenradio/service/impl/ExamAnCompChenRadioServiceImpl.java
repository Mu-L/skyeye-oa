package com.skyeye.exam.examancompchenradio.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examancompchenradio.dao.ExamAnCompChenRadioDao;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamAnCompChenRadioServiceImpl
 * @Description: 答卷 复合矩阵单选题服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 复合矩阵单选题", groupName = "答卷 复合矩阵单选题")
public class ExamAnCompChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnCompChenRadioDao, ExamAnCompChenRadio> implements ExamAnCompChenRadioService {

    @Autowired
    private ExamAnCompChenRadioService examAnCompChenRadioService;
}
