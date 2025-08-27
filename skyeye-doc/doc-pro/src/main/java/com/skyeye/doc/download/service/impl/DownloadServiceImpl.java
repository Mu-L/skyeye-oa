/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.download.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.doc.code.service.CodeSourceService;
import com.skyeye.doc.download.dao.DownloadDao;
import com.skyeye.doc.download.entity.Download;
import com.skyeye.doc.download.service.DownloadService;
import com.skyeye.doc.member.service.DocMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @ClassName: DownloadServiceImpl
 * @Description: 下载历史记录业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/27 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "下载历史", groupName = "下载历史", tenant = TenantEnum.PLATE)
public class DownloadServiceImpl extends SkyeyeBusinessServiceImpl<DownloadDao, Download> implements DownloadService {

    @Autowired
    private DocMemberService docMemberService;

    @Autowired
    private CodeSourceService codeSourceService;

    @Autowired
    private Executor docMemberDownloadLogExecutor;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        codeSourceService.setMationForMap(beans, "codeSourceId", "codeSourceMation");
        docMemberService.setMationForMap(beans, "memberId", "memberMation");
        return beans;
    }

    @Override
    public void createDownloadLog(String userId, String codeSourceId) {
        HttpServletRequest request = PutObject.getRequest();
        docMemberDownloadLogExecutor.execute(() -> {
            TenantContext.setTenantId(TenantEnum.PLATE.getKey());
            Download download = new Download();
            download.setMemberId(userId);
            download.setCodeSourceId(codeSourceId);
            // 设置登录信息
            String clientIp = ToolUtil.getIpByRequest(request);
            download.setIp(clientIp);
            // 获取IP对应的城市信息
            String address = IPSeeker.getCountry(clientIp);
            String city = IPSeeker.getCurCityByCountry(address);
            download.setCity(city);
            createEntity(download, userId);
        });
    }
}
