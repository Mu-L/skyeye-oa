package com.skyeye.school.datum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chapter.entity.Chapter;
import com.skyeye.school.datum.entity.Datum;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DatumService
 * @Description: 资料管理服务层接口
 * @author: 卢雨佳
 * @date: 2024/7/14 11:08
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DatumService extends SkyeyeBusinessService<Datum> {
    void queryDatumListBySubjectId(InputObject inputObject, OutputObject outputObject);

    Map<String, Double> queryDatumByChapterId(Long classNum,String ...ids);

    Long queryClassDataNum(String id, String stuId, String chapterId);
}
