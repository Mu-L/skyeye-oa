package com.skyeye.exam.examanchencheckbox.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examanchencheckbox.dao.ExamAnChenCheckboxDao;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamAnChenCheckboxServiceImpl
 * @Description: 答卷 矩阵多选题管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 矩阵多选题", groupName = "答卷 矩阵多选题")
public class ExamAnChenCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnChenCheckboxDao, ExamAnChenCheckbox> implements ExamAnChenCheckboxService {



}
