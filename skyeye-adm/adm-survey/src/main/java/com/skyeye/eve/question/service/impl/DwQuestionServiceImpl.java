package com.skyeye.eve.question.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;
import com.skyeye.eve.checkbox.entity.DwQuCheckbox;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import com.skyeye.eve.checkbox.service.DwQuCheckboxService;
import com.skyeye.eve.chen.entity.DwAnChenCheckbox;
import com.skyeye.eve.chen.entity.DwAnChenRadio;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwAnChenCheckboxService;
import com.skyeye.eve.chen.service.DwAnChenRadioService;
import com.skyeye.eve.chen.service.DwQuChenColumnService;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.multifllblank.service.DwAnDfillblankService;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import com.skyeye.eve.order.entity.DwAnOrder;
import com.skyeye.eve.order.service.DwAnOrderService;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.orderby.service.DwQuOrderbyService;
import com.skyeye.eve.question.dao.DwQuestionDao;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import com.skyeye.eve.question.service.DwQuestionService;
import com.skyeye.eve.radio.entity.DwAnRadio;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwAnRadioService;
import com.skyeye.eve.radio.service.DwQuRadioService;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwAnScoreService;
import com.skyeye.eve.score.service.DwQuScoreService;
import com.skyeye.exception.CustomException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SkyeyeService(name = "题目", groupName = "题库管理")
public class DwQuestionServiceImpl extends SkyeyeBusinessServiceImpl<DwQuestionDao, DwQuestion> implements DwQuestionService {

    @Autowired
    private DwQuestionLogicService dwQuestionLogicService;
    @Autowired
    private DwQuRadioService dwQuRadioService;
    @Autowired
    private DwQuScoreService dwQuScoreService;
    @Autowired
    private DwQuCheckboxService dwQuCheckboxService;
    @Autowired
    private DwQuMultiFillblankService dwQuMultiFillblankService;
    @Autowired
    private DwQuOrderbyService dwQuOrderbyService;
    @Autowired
    private DwQuChenColumnService dwQuChenColumnService;
    @Autowired
    private DwQuChenRowService dwQuChenRowService;
    @Autowired
    private DwAnRadioService dwAnRadioService;
    @Autowired
    private DwAnCheckboxService dwAnCheckboxService;
    @Autowired
    private DwAnScoreService dwAnScoreService;
    @Autowired
    private DwAnOrderService dwAnOrderService;
    @Autowired
    private DwAnChenRadioService dwAnChenRadioService;
    @Autowired
    private DwAnChenCheckboxService dwAnChenCheckboxService;
    @Autowired
    private DwAnDfillblankService dwAnDfillblankService;

    @Override
    protected void createPrepose(DwQuestion entity) {
        // 设置题目的标签和可见性
        entity.setQuTag(1);
        // 设置文件类型，默认为0
        Integer fileType = entity.getFileType() != null ? entity.getFileType() : 0;
        entity.setFileType(fileType);
        // 设置是否上传，默认为2
        Integer whetherUpload = entity.getWhetherUpload() != null ? entity.getWhetherUpload() : 2;
        entity.setWhetherUpload(whetherUpload);
    }

    /**
     * 创建题目后的后置处理
     *
     * @param entity 题目实体对象
     * @param userId 创建题目的用户ID
     */
    @Override
    public void createPostpose(DwQuestion entity, String userId) {
        // 获取题目ID
        String quId = entity.getId();
        Integer tag = entity.getTag();
        if (tag == null) {
            throw new CustomException("请设置题目标记");
        } else if (tag.equals(CommonNumConstants.NUM_ONE)) {
        } else if (tag.equals(CommonNumConstants.NUM_TWO)) {
            List<DwQuestionLogic> questionLogic = entity.getQuestionLogic();
            if (CollectionUtils.isNotEmpty(questionLogic)) {
                dwQuestionLogicService.setLogics(quId, questionLogic, userId);
            }
        } else {
            throw new CustomException("题目标记值不正确");
        }
        // 根据不同的题目类型，保存对应的题目数据
        // 处理单选题
        List<DwQuRadio> radioTd = entity.getRadioTd();
        if (CollectionUtils.isNotEmpty(radioTd)) {
            dwQuRadioService.saveList(radioTd, quId, userId);
        }
        // 处理评分题
        List<DwQuScore> ScoreTd = entity.getScoreTd();
        if (CollectionUtils.isNotEmpty(ScoreTd)) {
            dwQuScoreService.saveList(ScoreTd, quId, userId);
        }
        // 处理多选题
        List<DwQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (CollectionUtils.isNotEmpty(checkboxTd)) {
            dwQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 处理多空填空题
        List<DwQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (CollectionUtils.isNotEmpty(multiFillblankTd)) {
            dwQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 处理排序题
        List<DwQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (CollectionUtils.isNotEmpty(orderbyTd)) {
            dwQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        // 处理矩阵题
        List<DwQuChenColumn> columnTd = entity.getColumnTd();
        List<DwQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            dwQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    /**
     * 更新题目前的前置处理
     *
     * @param entity 题目实体对象
     */
    @Override
    public void updatePrepose(DwQuestion entity) {
        // 获取当前登录用户ID
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 更新不同题目类型的数据
        // 更新单选题
        List<DwQuRadio> radioTd = entity.getRadioTd();
        String quId = entity.getId();
        if (CollectionUtils.isNotEmpty(radioTd)) {
            dwQuRadioService.saveList(radioTd, quId, userId);
        }
        // 更新得分题
        List<DwQuScore> scoreTd = entity.getScoreTd();
        if (CollectionUtils.isNotEmpty(scoreTd)) {
            dwQuScoreService.saveList(scoreTd, quId, userId);
        }
        // 更新多选题
        List<DwQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (CollectionUtils.isNotEmpty(checkboxTd)) {
            dwQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 更新多行填空题
        List<DwQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (CollectionUtils.isNotEmpty(multiFillblankTd)) {
            dwQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 更新排序题
        List<DwQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (CollectionUtils.isNotEmpty(orderbyTd)) {
            dwQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        // 更新矩阵题
        List<DwQuChenColumn> columnTd = entity.getColumnTd();
        List<DwQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            dwQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    @Override
    public void queryDwQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestion::getIsDelete), CommonNumConstants.NUM_ONE);
        getIAuthUser(outputObject, page, queryWrapper);
    }

    @Override
    public DwQuestion selectById(String id) {
        DwQuestion question = super.selectById(id);
        List<DwQuRadio> dwQuRadioList = dwQuRadioService.selectQuRadio(id);
        question.setRadioTd(dwQuRadioList);
        List<DwQuScore> dwQuScoreList = dwQuScoreService.selectQuScore(id);
        question.setScoreTd(dwQuScoreList);
        List<DwQuCheckbox> dwQuCheckboxList = dwQuCheckboxService.selectQuChenbox(id);
        question.setCheckboxTd(dwQuCheckboxList);
        List<DwQuMultiFillblank> dwQuMultiFillblankList = dwQuMultiFillblankService.selectQuMultiFillblank(id);
        question.setMultifillblankTd(dwQuMultiFillblankList);
        List<DwQuOrderby> dwQuOrderbyList = dwQuOrderbyService.selectQuOrderby(id);
        question.setOrderbyTd(dwQuOrderbyList);
        List<DwQuChenColumn> dwQuChenColumnList = dwQuChenColumnService.selectQuChenColumn(id);
        question.setColumnTd(dwQuChenColumnList);
        List<DwQuChenRow> dwQuChenRowList = dwQuChenRowService.selectQuChenRow(id);
        question.setRowTd(dwQuChenRowList);
        return question;
    }

    @Override
    public List<DwQuestion> selectByIds(String... ids) {
        List<DwQuestion> questionList = new ArrayList<>();
        for (String id : ids) {
            DwQuestion question = super.selectById(id);
            questionList.add(question);
        }
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        for (DwQuestion question : questionList) {
            // 1 单选题
            if (question.getQuType() == QuType.RADIO.getIndex()) {
                List<DwQuRadio> radioList = dwQuRadioService.selectQuRadio(question.getId());
                DwAnRadio dwAnRadio = dwAnRadioService.selectById(question.getId());
                question.setRadioTd(radioList);
                question.setRadioAn(dwAnRadio);
                continue;
            }
            // 2 多选题
            if (question.getQuType() == QuType.CHECKBOX.getIndex()) {
                List<DwQuCheckbox> dwQuCheckboxeList = dwQuCheckboxService.selectQuChenbox(question.getId());
                List<DwAnCheckbox> dwAnCheckboxes = dwAnCheckboxService.selectAnCheckBoxByQuId(question.getId());
                question.setCheckboxTd(dwQuCheckboxeList);
                question.setCheckboxAn(dwAnCheckboxes);
                continue;
            }
            // 8 评分题
            if (question.getQuType() == QuType.SCORE.getIndex()) {
                List<DwQuScore> scoreList = dwQuScoreService.selectQuScore(question.getId());
                List<DwAnScore> dwAnScoreList = dwAnScoreService.selectAnScoreByQuId(question.getId());
                question.setScoreTd(scoreList);
                question.setScoreAn(dwAnScoreList);
                continue;
            }
            // 9 排序题
            if (question.getQuType() == QuType.ORDERQU.getIndex()) {
                List<DwQuOrderby> orderbyList = dwQuOrderbyService.selectQuOrderby(question.getId());
                List<DwAnOrder> dwAnOrderbyList = dwAnOrderService.selectAnOrderByQuId(question.getId());
                question.setOrderbyTd(orderbyList);
                question.setOrderbyAn(dwAnOrderbyList);
                continue;
            }
            // 4 多行填空题
            if (question.getQuType() == QuType.MULTIFILLBLANK.getIndex()) {
                List<DwQuMultiFillblank> dwQuMultiFillblanks = dwQuMultiFillblankService.selectQuMultiFillblank(question.getId());
                List<DwAnDfillblank> dwAnDfillblanks = dwAnDfillblankService.selectAnDfillblankQuId(question.getId());
                question.setMultifillblankTd(dwQuMultiFillblanks);
                question.setDfillblankAn(dwAnDfillblanks);
                continue;
            }
            // 11 矩阵单选题CHENRADIO 12 矩阵填空题CHENFBK 13 矩阵多选题CHENCHECKBOX 18 矩阵评分题CHENSCORE
            if (question.getQuType() == QuType.CHENRADIO.getIndex() ||
                    question.getQuType() == QuType.CHENFBK.getIndex() ||
                    question.getQuType() == QuType.CHENCHECKBOX.getIndex() ||
                    question.getQuType() == QuType.CHENSCORE.getIndex()) {
                List<DwQuChenColumn> dwQuChenColumnList = dwQuChenColumnService.selectQuChenColumn(question.getId());
                List<DwQuChenRow> dwQuChenRowList = dwQuChenRowService.selectQuChenRow(question.getId());
                DwAnChenRadio dwAnChenRadio = dwAnChenRadioService.selectById(question.getId());
                List<DwAnChenCheckbox> dwAnCheckboxList = dwAnChenCheckboxService.selectAnChenCheckboxByQuId(question.getId());
                question.setColumnTd(dwQuChenColumnList);
                question.setChenAn(dwAnChenRadio);
                question.setRowTd(dwQuChenRowList);
                question.setChenRowAn(dwAnCheckboxList);
            }
        }
        return questionList;
    }

    /**
     * 根据归属ID查询题目列表
     *
     * @param belongId 归属ID
     * @return 题目列表
     */
    @Override
    public List<DwQuestion> QueryQuestionByBelongId(String belongId) {
        QueryWrapper<DwQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestion::getBelongId), belongId);
        return list(queryWrapper);
    }

    /**
     * 删除题目前的执行操作
     *
     * @param entity 题目实体对象
     */
    @Override
    public void deletePreExecution(DwQuestion entity) {
        // 获取题目ID和类型
        String quId = entity.getId();
        Integer quType = entity.getQuType();
        // 根据题目类型删除对应的题目数据
        if (quType.equals(QuType.RADIO.getIndex())) {
            dwQuRadioService.removeByQuId(quId);
        } else if (quType.equals(QuType.MULTIFILLBLANK.getIndex())) {
            dwQuMultiFillblankService.removeByQuId(quId);
        } else if (quType.equals(QuType.CHECKBOX.getIndex())) {
            dwQuCheckboxService.removeByQuId(quId);
        } else if (quType.equals(QuType.SCORE.getIndex())) {
            dwQuScoreService.removeByQuId(quId);
        } else if (quType.equals(QuType.ORDERQU.getIndex())) {
            dwQuOrderbyService.removeByQuId(quId);
        } else if (quType.equals(QuType.CHENRADIO.getIndex()) ||
                quType.equals(QuType.CHENFBK.getIndex()) ||
                quType.equals(QuType.CHENCHECKBOX.getIndex()) ||
                quType.equals(QuType.COMPCHENRADIO.getIndex()) ||
                quType.equals(QuType.CHENSCORE.getIndex())
        ) {
            dwQuChenColumnService.removeByQuId(quId);
        }
    }

    /**
     * 复制题目信息的方法
     *
     * @param question 题目实体对象，包含要复制的题目信息以及复制后题目的ID
     */
    @Override
    public void copyQuestionListMation(DwQuestion question) {
        // 根据题目类型进行不同的复制操作
        String quType = QuType.getActionName(Integer.parseInt(question.getQuType().toString()));
        // 复制单选题或复合单选题
        if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
            List<DwQuRadio> dwQuRadioList = dwQuRadioService.selectQuRadio(question.getCopyFromId());
            if (CollectionUtils.isEmpty(dwQuRadioList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (DwQuRadio dwQuRadio : dwQuRadioList) {
                dwQuRadio.setId(ToolUtil.getSurFaceId()); // 设置新的唯一ID
                dwQuRadio.setCreateTime(DateUtil.getTimeAndToString()); // 设置创建时间
                dwQuRadio.setQuId(question.getId()); // 设置所属题目ID
                dwQuRadioService.createEntity(dwQuRadio, StrUtil.EMPTY);
            }
        }
        // 复制多选题或复合多选题
        else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
            List<DwQuCheckbox> dwQuCheckboxList = dwQuCheckboxService.selectQuChenbox(question.getCopyFromId());
            if (CollectionUtils.isEmpty(dwQuCheckboxList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (DwQuCheckbox dwQuCheckbox : dwQuCheckboxList) {
                dwQuCheckbox.setId(ToolUtil.getSurFaceId());
                dwQuCheckbox.setCreateTime(DateUtil.getTimeAndToString());
                dwQuCheckbox.setQuId(question.getId());
                dwQuCheckboxService.createEntity(dwQuCheckbox, StrUtil.EMPTY);
            }
        }
        // 复制多空填空题
        else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
            List<DwQuMultiFillblank> multiFillblanksList = dwQuMultiFillblankService.selectQuMultiFillblank(question.getCopyFromId());
            if (CollectionUtils.isEmpty(multiFillblanksList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (DwQuMultiFillblank dwQuMultiFillblank : multiFillblanksList) {
                dwQuMultiFillblank.setId(ToolUtil.getSurFaceId());
                dwQuMultiFillblank.setCreateTime(DateUtil.getTimeAndToString());
                dwQuMultiFillblank.setQuId(question.getId());
                dwQuMultiFillblankService.createEntity(dwQuMultiFillblank, StrUtil.EMPTY);
            }
        }
        // 复制陈列题相关数据
        else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) ||
                quType.equals(QuType.CHENSCORE.getActionName()) || quType.equals(QuType.CHENFBK.getActionName()) ||
                quType.equals(QuType.COMPCHENRADIO.getActionName())) {
            List<DwQuChenRow> dwQuChenRowList = dwQuChenRowService.selectQuChenRow(question.getCopyFromId());
            List<DwQuChenColumn> dwQuChenColumnList = dwQuChenColumnService.selectQuChenColumn(question.getCopyFromId());
            if (CollectionUtils.isEmpty(dwQuChenRowList) || CollectionUtils.isEmpty(dwQuChenColumnList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (DwQuChenRow dwQuChenRow : dwQuChenRowList) {
                dwQuChenRow.setId(ToolUtil.getSurFaceId());
                dwQuChenRow.setCreateTime(DateUtil.getTimeAndToString());
                dwQuChenRow.setQuId(question.getId());
                dwQuChenRowService.createEntity(dwQuChenRow, StrUtil.EMPTY);
            }
            for (DwQuChenColumn dwQuChenColumn : dwQuChenColumnList) {
                dwQuChenColumn.setId(ToolUtil.getSurFaceId());
                dwQuChenColumn.setCreateTime(DateUtil.getTimeAndToString());
                dwQuChenColumn.setQuId(question.getId());
                dwQuChenColumnService.createEntity(dwQuChenColumn, StrUtil.EMPTY);
            }
        }
        // 复制得分题
        else if (quType.equals(QuType.SCORE.getActionName())) {
            List<DwQuScore> dwQuScoreList = dwQuScoreService.selectQuScore(question.getCopyFromId());
            for (DwQuScore dwQuScore : dwQuScoreList) {
                dwQuScore.setId(ToolUtil.getSurFaceId());
                dwQuScore.setCreateTime(DateUtil.getTimeAndToString());
                dwQuScore.setQuId(question.getId());
                dwQuScoreService.createEntity(dwQuScore, StrUtil.EMPTY);
            }
        }
        // 复制排序题
        else if (quType.equals(QuType.ORDERQU.getActionName())) {
            List<DwQuOrderby> dwQuOrderbyList = dwQuOrderbyService.selectQuOrderby(question.getCopyFromId());
            for (DwQuOrderby dwQuOrderby : dwQuOrderbyList) {
                dwQuOrderby.setId(ToolUtil.getSurFaceId());
                dwQuOrderby.setCreateTime(DateUtil.getTimeAndToString());
                dwQuOrderby.setQuId(question.getId());
                dwQuOrderbyService.createEntity(dwQuOrderby, StrUtil.EMPTY);
            }
        }
    }

    @Override
    public void queryMyDwQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestion::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        getIAuthUser(outputObject, page, queryWrapper);
    }

    private void getIAuthUser(OutputObject outputObject, Page page, QueryWrapper<DwQuestion> queryWrapper) {
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestion::getCreateTime));
        List<DwQuestion> questionList = list(queryWrapper);
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(questionList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryPageDwQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = null;
        setCommonPageInfoOtherInfo(commonPageInfo);
        if (commonPageInfo.getIsPaging() == null || commonPageInfo.getIsPaging()) {
            pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        }
        QueryWrapper<DwQuestion> queryWrapper = getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestion::getCreateTime));// 按创建时间降序
        List<DwQuestion> questionList = list(queryWrapper);
        outputObject.setBeans(questionList);
        outputObject.settotal(pages.getTotal());
    }
}
