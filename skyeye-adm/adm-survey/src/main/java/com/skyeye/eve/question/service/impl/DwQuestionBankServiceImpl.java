package com.skyeye.eve.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.question.dao.DwQuestionBankDao;
import com.skyeye.eve.question.entity.DwQuestionBank;
import com.skyeye.eve.question.service.DwQuestionBankService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "题库管理", groupName = "题库管理")
public class DwQuestionBankServiceImpl extends SkyeyeBusinessServiceImpl<DwQuestionBankDao, DwQuestionBank> implements DwQuestionBankService {

    @Override
    protected void createPrepose(DwQuestionBank entity) {
        Integer bankState = entity.getBankState();
        if (bankState != null) {
            if (!bankState.equals(CommonNumConstants.NUM_ZERO) &&
                    !bankState.equals(CommonNumConstants.NUM_ONE)) {
                throw new CustomException("题库状态不正确");
            }
            Integer bankTag = entity.getBankTag();
            if (bankTag == null) {
                throw new CustomException("题库标签不能为空");
            } else if (!bankTag.equals(CommonNumConstants.NUM_ZERO) &&
                    !bankTag.equals(CommonNumConstants.NUM_ONE) &&
                    !bankTag.equals(CommonNumConstants.NUM_TWO)) {
                throw new CustomException("题库标签不正确");
            }
        }
    }

    @Override
    public void queryDwQuestionBankList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwQuestionBank> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestionBank::getCreateTime));
        List<DwQuestionBank> dwQuestionBankList = list(queryWrapper);
        iAuthUserService.setName(dwQuestionBankList, "createId", "createName");
        iAuthUserService.setName(dwQuestionBankList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(dwQuestionBankList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryMyDwQuestionBankList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwQuestionBank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestionBank::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestionBank::getCreateTime));
        List<DwQuestionBank> dwQuestionBankList = list(queryWrapper);
        iAuthUserService.setName(dwQuestionBankList, "createId", "createName");
        iAuthUserService.setName(dwQuestionBankList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(dwQuestionBankList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void setUpDwQuestionBank(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuestionBank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuestionBank::getBankState), CommonNumConstants.NUM_ONE);
        update(updateWrapper);
    }

}

