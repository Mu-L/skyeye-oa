/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.articles.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.StringUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.articles.dao.ArticlesDao;
import com.skyeye.eve.articles.entity.Articles;
import com.skyeye.eve.articles.service.ArticlesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArticlesServiceImpl
 * @Description: 用品管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "用品管理", groupName = "用品模块")
public class ArticlesServiceImpl extends SkyeyeBusinessServiceImpl<ArticlesDao, Articles> implements ArticlesService {

    @Override
    public void createPrepose(Articles entity) {
        setArticlesNum(entity);
        entity.setResidualNum(entity.getInitialNum());
    }

    private void setArticlesNum(Articles articles) {
        String prefix = StringUtil.chineseToFirstLetter(articles.getName());
        Map<String, Object> bussness = MapUtil.newHashMap();
        bussness.put("prefix", prefix);
        String codeNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), bussness);
        articles.setArticlesNum(codeNumber);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        return beans;
    }

    @Override
    public Articles selectById(String id) {
        Articles articles = super.selectById(id);
        iSysDictDataService.setName(articles, "typeId", "typeName");
        articles.setAssetAdminMation(iAuthUserService.queryDataMationById(articles.getAssetAdmin()));
        return articles;
    }

    @Override
    public List<Articles> selectByIds(String... ids) {
        List<Articles> articles = super.selectByIds(ids);
        iSysDictDataService.setName(articles, "typeId", "typeName");
        // 设置管理人信息
        iAuthUserService.setDataMation(articles, Articles::getAssetAdmin);
        return articles;
    }

    /**
     * 根据用品类别获取用品列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllArticlesList(InputObject inputObject, OutputObject outputObject) {
        List<Articles> articlesList = list();
        outputObject.setBeans(articlesList);
        outputObject.settotal(articlesList.size());
    }

    /**
     * 根据id修改剩余库存信息
     *
     * @param id
     * @param residualNum
     */
    @Override
    public void editResidualNum(String id, Integer residualNum) {
        UpdateWrapper<Articles> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Articles::getResidualNum), residualNum);
        update(updateWrapper);
        refreshCache(id);
    }

}
