package com.skyeye.depot.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOutPutRecord;
import com.skyeye.entity.ErpOrderItem;

import java.util.List;

/**
 * @ClassName: DepotOutPutRecordService
 * @Description: 仓库出入库记录管接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/14 9:20
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DepotOutPutRecordService extends SkyeyeBusinessService<DepotOutPutRecord> {
    List<DepotOutPutRecord> selectByNormCodes(List<String> codeList);

    List<DepotOutPutRecord> queryRecordListByHolderIdAndMN(String holderId, List<String> materialIdList, List<String> normsIdList);

    void writeOutPutRecord(Object o,Integer fromTypeId);

    void queryOutPutRecordDetailList(InputObject inputObject, OutputObject outputObject);

    void queryHolderOutPutNormsList(InputObject inputObject, OutputObject outputObject);

    void checkOutPutRecord(List<ErpOrderItem> erpOrderItemList,String holderId);
}
