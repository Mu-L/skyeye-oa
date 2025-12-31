/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.voucher.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.voucher.entity.Voucher;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsVoucherDao
 * @Description: 凭证信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/3 18:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsVoucherDao extends SkyeyeBaseMapper<Voucher> {

    List<Map<String, Object>> queryIfsVoucherList(CommonPageInfo commonPageInfo);

}
