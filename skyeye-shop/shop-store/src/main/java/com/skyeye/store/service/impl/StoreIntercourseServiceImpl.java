/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.entity.intercourse.StoreIntercourseQueryDo;
import com.skyeye.store.dao.StoreIntercourseDao;
import com.skyeye.store.service.StoreIntercourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: StoreIntercourseServiceImpl
 * @Description: 门店往来管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/10 21:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class StoreIntercourseServiceImpl implements StoreIntercourseService {

    @Autowired
    private StoreIntercourseDao storeIntercourseDao;

    public enum State {
        WAIT_MEAL_BY_STORE(1, "待套餐购买门店确认"),
        WAIT_KEEPFIT_STORE(2, "待保养门店确认"),
        CONFIRMED(3, "已确认");
        private int state;
        private String name;

        State(int state, String name) {
            this.state = state;
            this.name = name;
        }

        public int getState() {
            return state;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 获取指定日期的支出/收入往来的数据
     *
     * @param day 指定日期
     * @return 指定日期的支出/收入往来的数据
     */
    @Override
    public List<Map<String, Object>> queryStoreIntercourseByDay(String day) {
        List<Map<String, Object>> storeIntercourseList = storeIntercourseDao.queryStoreIntercourseByDay(day);
        return storeIntercourseList;
    }

    /**
     * 新增支出/收入往来的数据
     *
     * @param shopStoreIntercourseMationList
     */
    @Override
    public void insertStoreIntercourse(List<Map<String, Object>> shopStoreIntercourseMationList) {
        shopStoreIntercourseMationList.forEach(bean -> {
            bean.put("id", ToolUtil.getSurFaceId());
        });
        storeIntercourseDao.insertStoreIntercourse(shopStoreIntercourseMationList);
    }

    /**
     * 获取指定门店的支出/收入往来的数据
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryStoreIntercourseList(InputObject inputObject, OutputObject outputObject) {
        StoreIntercourseQueryDo storeIntercourseQuery = inputObject.getParams(StoreIntercourseQueryDo.class);
        Page pages = PageHelper.startPage(storeIntercourseQuery.getPage(), storeIntercourseQuery.getLimit());
        String tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId() : StrUtil.EMPTY;
        storeIntercourseQuery.setTenantId(tenantId);
        List<Map<String, Object>> beans = storeIntercourseDao.queryStoreIntercourseList(storeIntercourseQuery);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 编辑指定门店的支出/收入往来的状态
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editStoreIntercourseState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Integer state = Integer.parseInt(params.get("state").toString());
        Map<String, Object> storeIntercourse = storeIntercourseDao.queryStoreIntercourseById(id);
        // 获取当前的状态
        Integer currentState = Integer.parseInt(storeIntercourse.get("state").toString());
        if (currentState == State.WAIT_MEAL_BY_STORE.getState() && state == State.WAIT_KEEPFIT_STORE.getState()) {
            // 当前状态为【待套餐购买门店确认】，可以修改为【待保养门店确认】
        } else if (currentState == State.WAIT_KEEPFIT_STORE.getState() && state == State.CONFIRMED.getState()) {
            // 当前状态为【待保养门店确认】，可以修改为【已确认】
        } else {
            outputObject.setreturnMessage("Status to change, please refresh the page.");
            return;
        }
        storeIntercourseDao.editStoreIntercourseState(id, state);
    }

    /**
     * 获取已统计信息中，指定日期的支出/收入往来的数据
     *
     * @param day 指定日期
     * @return 指定日期的支出/收入往来的数据
     */
    @Override
    public List<Map<String, Object>> queryStoreIntercourseListByDay(String day) {
        StoreIntercourseQueryDo storeIntercourseQuery = new StoreIntercourseQueryDo();
        storeIntercourseQuery.setDay(day);
        String tenantId = TenantContext.getTenantId();
        storeIntercourseQuery.setTenantId(tenantId);
        List<Map<String, Object>> storeIntercourseList = storeIntercourseDao.queryStoreIntercourseList(storeIntercourseQuery);
        return storeIntercourseList;
    }

}
