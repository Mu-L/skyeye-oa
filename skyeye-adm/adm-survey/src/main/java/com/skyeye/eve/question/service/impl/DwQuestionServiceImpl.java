package com.skyeye.eve.question.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.checkbox.entity.DwQuCheckbox;
import com.skyeye.eve.checkbox.service.DwQuCheckboxService;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwQuChenColumnService;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.orderby.service.DwQuOrderbyService;
import com.skyeye.eve.question.dao.DwQuestionDao;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import com.skyeye.eve.question.service.DwQuestionService;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwQuRadioService;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwQuScoreService;
import com.skyeye.exception.CustomException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public void createPrepose(List<DwQuestion> entity) {
        for (DwQuestion question : entity) {
            // 设置题目的标签和可见性
            question.setQuTag(1);
            // 设置文件类型，默认为0
            Integer fileType = question.getFileType() != null ? question.getFileType() : 0;
            question.setFileType(fileType);
            // 设置是否上传，默认为2
            Integer whetherUpload = question.getWhetherUpload() != null ? question.getWhetherUpload() : 2;
            question.setWhetherUpload(whetherUpload);
        }
    }

    @Override
    public void createPrepose(DwQuestion question) {
        // 设置题目的标签和可见性
        question.setQuTag(1);
        // 设置文件类型，默认为0
        Integer fileType = question.getFileType() != null ? question.getFileType() : 0;
        question.setFileType(fileType);
        // 设置是否上传，默认为2
        Integer whetherUpload = question.getWhetherUpload() != null ? question.getWhetherUpload() : 2;
        question.setWhetherUpload(whetherUpload);

    }

    @Override
    public void createPostpose(List<DwQuestion> dwQuestionList, String userId) {
        for (DwQuestion entity : dwQuestionList) {
            Integer tag = entity.getTag();
            if (tag == null) {
                throw new CustomException("请设置题目标记");
            } else if (!tag.equals(CommonNumConstants.NUM_ONE) && !tag.equals(CommonNumConstants.NUM_TWO)) {
                throw new CustomException("题目标记值不正确");
            }
        }
        dwQuestionLogicService.createLogics(dwQuestionList, userId);
        dwQuRadioService.createRadios(dwQuestionList, userId);
        dwQuScoreService.createScores(dwQuestionList, userId);
        dwQuCheckboxService.createCheckboxs(dwQuestionList, userId);
        dwQuMultiFillblankService.createMultiFillblanks(dwQuestionList, userId);
        dwQuOrderbyService.createOrderbys(dwQuestionList, userId);
        dwQuChenColumnService.createChenColumns(dwQuestionList, userId);
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
        } else {
            if (tag.equals(CommonNumConstants.NUM_ONE)) {
            } else if (tag.equals(CommonNumConstants.NUM_TWO)) {
                List<DwQuestionLogic> questionLogic = entity.getQuestionLogic();
                if (CollectionUtils.isEmpty(questionLogic)) {
                } else {
                    dwQuestionLogicService.setLogics(quId, questionLogic, userId);
                }
            } else {
                throw new CustomException("题目标记值不正确");
            }
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
        List<DwQuOrderby> orderByTd = entity.getOrderByTd();
        if (CollectionUtils.isNotEmpty(orderByTd)) {
            dwQuOrderbyService.saveList(orderByTd, quId, userId);
        }
        // 处理矩阵题
        List<DwQuChenColumn> columnTd = entity.getColumnTd();
        List<DwQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            dwQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
        List<DwQuestionLogic> questionLogic = entity.getQuestionLogic();
        if (CollectionUtils.isNotEmpty(questionLogic)) {
            dwQuestionLogicService.createEntity(questionLogic, userId);
        }
    }

    @Override
    protected void updatePostpose(List<DwQuestion> dwQuestionList, String userId) {
        deleteNoBelongDwQuestions(dwQuestionList);
        // id 为空的 DwQuestionLogic 列表
        List<DwQuestionLogic> idEmptyList = dwQuestionList.stream()
            .flatMap(q -> q.getQuestionLogic().stream())     // 扁平化所有 DwQuestionLogic
            .filter(l -> l.getId() == null || l.getId().isEmpty())
            .collect(Collectors.toList());

      // id 不为空的 DwQuestionLogic 列表
        List<DwQuestionLogic> idNotEmptyList = dwQuestionList.stream()
            .flatMap(q -> q.getQuestionLogic().stream())
            .filter(l -> l.getId() != null && !l.getId().isEmpty())
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(idEmptyList)) {
            dwQuestionLogicService.createEntity(idEmptyList, userId);
        }
        if (CollectionUtil.isNotEmpty(idNotEmptyList)) {
            dwQuestionLogicService.updateEntity(idNotEmptyList, userId);
        }
        // 过滤出 id 为空的数据
        List<List<DwQuestionLogic>> emptyIdList = dwQuestionList.stream()
            .map(DwQuestion::getQuestionLogic)
            .map(logics -> logics.stream()
                .filter(logic -> logic.getId() == null || logic.getId().isEmpty())
                .collect(Collectors.toList()))
            .collect(Collectors.toList());
        // 批量更新各题型数据
        dwQuRadioService.updateRadios(dwQuestionList, userId);
        dwQuScoreService.updateScores(dwQuestionList, userId);
        dwQuCheckboxService.updateCheckboxs(dwQuestionList, userId);
        dwQuMultiFillblankService.updateMultiFillblanks(dwQuestionList, userId);
        dwQuOrderbyService.updateOrderbys(dwQuestionList, userId);
        dwQuChenColumnService.updateChenColumn(dwQuestionList, userId);
    }

    private void deleteNoBelongDwQuestions(List<DwQuestion> dwQuestions) {
        List<String> dwQuestionIds = dwQuestions.stream()
            .filter(q -> StrUtil.isEmpty(q.getBelongId()))
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(dwQuestionIds)) {
            dwQuRadioService.removeByQuIds(dwQuestionIds);
            dwQuScoreService.removeByQuIds(dwQuestionIds);
            dwQuCheckboxService.removeByQuIds(dwQuestionIds);
            dwQuMultiFillblankService.removeByQuIds(dwQuestionIds);
            dwQuOrderbyService.removeByQuIds(dwQuestionIds);
            dwQuChenColumnService.removeByQuIds(dwQuestionIds);
        }
    }

    /**
     * 更新题目前的后置处理
     *
     * @param entity 题目实体对象
     */
    @Override
    public void updatePostpose(DwQuestion entity, String userId) {
        String entityId = entity.getId();
        // 更新单选题
        String belongId = entity.getBelongId();
        if (StrUtil.isEmpty(belongId)) {
            dwQuRadioService.removeByQuId(entityId);
            dwQuScoreService.removeByquId(entityId);
            dwQuCheckboxService.removeByQuId(entityId);
            dwQuMultiFillblankService.removeByQuId(entityId);
            dwQuOrderbyService.removeByQuId(entityId);
            dwQuChenColumnService.removeByQuId(entityId);
        }
        List<DwQuestionLogic> questionLogic = entity.getQuestionLogic();
        // 过滤 id 为空的
        List<DwQuestionLogic> idIsEmpty = questionLogic.stream()
            .filter(logic -> logic.getId() == null || logic.getId().trim().isEmpty())
            .collect(Collectors.toList());

        // 过滤 id 不为空的
        List<DwQuestionLogic> idIsNotEmpty = questionLogic.stream()
            .filter(logic -> logic.getId() != null && !logic.getId().trim().isEmpty())
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idIsEmpty)) {
            dwQuestionLogicService.createEntity(idIsEmpty, userId);
        }
        if (CollectionUtils.isNotEmpty(idIsNotEmpty)) {
            dwQuestionLogicService.updateEntity(idIsNotEmpty, userId);
        }
        List<DwQuRadio> radioTd = entity.getRadioTd();
        String quId = entity.getId();
        if (CollectionUtils.isNotEmpty(radioTd)) {
            List<String> collect = radioTd.stream().map(DwQuRadio::getOptionId).collect(Collectors.toList());
            List<DwQuRadio> examQuRadioList = dwQuRadioService.selectQuRadio(entityId);
            List<String> collect1 = examQuRadioList.stream().map(DwQuRadio::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(
                optionId -> !collect.contains(optionId)
            ).collect(Collectors.toList());
            dwQuRadioService.deleteById(collect2);
            dwQuRadioService.saveList(radioTd, quId, userId);
        }
        // 更新得分题
        List<DwQuScore> scoreTd = entity.getScoreTd();
        if (CollectionUtils.isNotEmpty(scoreTd)) {
            List<String> collect = scoreTd.stream().map(DwQuScore::getOptionId).collect(Collectors.toList());
            List<String> collect1 = dwQuScoreService.selectQuScore(entityId).stream().map(DwQuScore::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            dwQuScoreService.deleteById(collect2);
            dwQuScoreService.saveList(scoreTd, quId, userId);
        }
        // 更新多选题
        List<DwQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (CollectionUtils.isNotEmpty(checkboxTd)) {
            List<String> collect = checkboxTd.stream().map(DwQuCheckbox::getOptionId).collect(Collectors.toList());
            List<String> collect1 = dwQuCheckboxService.selectQuChenbox(entityId).stream().map(DwQuCheckbox::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            dwQuCheckboxService.deleteById(collect2);
            dwQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 更新多空填空题
        List<DwQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (CollectionUtils.isNotEmpty(multiFillblankTd)) {
            List<String> collect = multiFillblankTd.stream().map(DwQuMultiFillblank::getOptionId).collect(Collectors.toList());
            List<String> collect1 = dwQuMultiFillblankService.selectQuMultiFillblank(entityId).stream().map(DwQuMultiFillblank::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            dwQuMultiFillblankService.deleteById(collect2);
            dwQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 更新排序题
        List<DwQuOrderby> orderByTd = entity.getOrderByTd();
        if (CollectionUtils.isNotEmpty(orderByTd)) {
            List<String> collect = orderByTd.stream().map(DwQuOrderby::getOptionId).collect(Collectors.toList());
            List<String> collect1 = dwQuOrderbyService.selectQuOrderby(entityId).stream().map(DwQuOrderby::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            dwQuOrderbyService.deleteById(collect2);
            dwQuOrderbyService.saveList(orderByTd, quId, userId);
        }
        // 更新陈列题
        List<DwQuChenColumn> columnTd = entity.getColumnTd();
        List<DwQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            List<String> collect = columnTd.stream().map(DwQuChenColumn::getOptionId).collect(Collectors.toList());
            List<String> collect1 = dwQuChenColumnService.selectQuChenColumn(entityId).stream().map(DwQuChenColumn::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            dwQuChenColumnService.deleteById(collect2);
            List<String> collect3 = rowTd.stream().map(DwQuChenRow::getOptionId).collect(Collectors.toList());
            List<String> collect4 = dwQuChenRowService.selectQuChenRow(entityId).stream().map(DwQuChenRow::getId).collect(Collectors.toList());
            List<String> collect5 = collect4.stream().filter(optionId -> !collect3.contains(optionId)).collect(Collectors.toList());
            dwQuChenRowService.deleteById(collect5);
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
        DwQuestionLogic dwQuestionLogics = dwQuestionLogicService.selectLogicByIdByQuId(id);
        if (ObjectUtil.isNotEmpty(dwQuestionLogics)) {
            question.setQuestionOneLogic(dwQuestionLogics);
        }
        List<DwQuRadio> dwQuRadioList = dwQuRadioService.selectQuRadio(id);
        question.setRadioTd(dwQuRadioList);
        List<DwQuScore> dwQuScoreList = dwQuScoreService.selectQuScore(id);
        question.setScoreTd(dwQuScoreList);
        List<DwQuCheckbox> dwQuCheckboxList = dwQuCheckboxService.selectQuChenbox(id);
        question.setCheckboxTd(dwQuCheckboxList);
        List<DwQuMultiFillblank> dwQuMultiFillblankList = dwQuMultiFillblankService.selectQuMultiFillblank(id);
        question.setMultifillblankTd(dwQuMultiFillblankList);
        List<DwQuOrderby> dwQuOrderbyList = dwQuOrderbyService.selectQuOrderby(id);
        question.setOrderByTd(dwQuOrderbyList);
        List<DwQuChenColumn> dwQuChenColumnList = dwQuChenColumnService.selectQuChenColumn(id);
        question.setColumnTd(dwQuChenColumnList);
        List<DwQuChenRow> dwQuChenRowList = dwQuChenRowService.selectQuChenRow(id);
        question.setRowTd(dwQuChenRowList);
        return question;
    }

    @Override
    public List<DwQuestion> selectByIds(String... ids) {
        List<DwQuestion> questionList = super.selectByIds(ids);
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        getQuestionOption(questionList);
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
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuestion::getCreateTime));
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuestion::getOrderById));
        List<DwQuestion> dwQuestionList = list(queryWrapper);
        getQuestionOption(dwQuestionList);
        return dwQuestionList;
    }

    private void getQuestionOption(List<DwQuestion> questionList) {
        List<DwQuestion> radioList = questionList.stream().filter(question -> question.getQuType().equals(QuType.RADIO.getIndex()))
            .collect(Collectors.toList());
        List<String> radioIds = radioList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuRadio>> radioMapList = dwQuRadioService.selectByBelongId(radioIds);

        List<DwQuestion> cheankboxList = questionList.stream().filter(question -> question.getQuType().equals(QuType.CHECKBOX.getIndex())).collect(Collectors.toList());
        List<String> cheankboxIds = cheankboxList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuCheckbox>> chaeckBoxMapList = dwQuCheckboxService.selectByBelongId(cheankboxIds);

        List<DwQuestion> scoreList = questionList.stream().filter(question -> question.getQuType().equals(QuType.SCORE.getIndex())).collect(Collectors.toList());
        List<String> scoreIds = scoreList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuScore>> scoreMapList = dwQuScoreService.selectByBelongId(scoreIds);

        List<DwQuestion> orderQuList = questionList.stream().filter(question -> question.getQuType().equals(QuType.ORDERQU.getIndex())).collect(Collectors.toList());
        List<String> orderQuIds = orderQuList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuOrderby>> orderQuMapList = dwQuOrderbyService.selectByBelongId(orderQuIds);

        List<DwQuestion> multifillblankList = questionList.stream().filter(question -> question.getQuType().equals(QuType.MULTIFILLBLANK.getIndex())).collect(Collectors.toList());
        List<String> multifillblankIds = multifillblankList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuMultiFillblank>> multifillblankMapList = dwQuMultiFillblankService.selectByBelongId(multifillblankIds);

        List<DwQuestion> chenList = questionList.stream().filter(question ->
            question.getQuType().equals(QuType.CHENRADIO.getIndex()) ||
                question.getQuType().equals(QuType.CHENFBK.getIndex()) ||
                question.getQuType().equals(QuType.CHENCHECKBOX.getIndex()) ||
                question.getQuType().equals(QuType.CHENSCORE.getIndex())
        ).collect(Collectors.toList());
        List<String> chenIds = chenList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuChenColumn>> chenColMapList = dwQuChenColumnService.selectByBelongId(chenIds);
        Map<String, List<DwQuChenRow>> chenRowMapList = dwQuChenRowService.selectByBelongId(chenIds);
        questionList.forEach(question -> {
            String qid = question.getId();
            int quType = question.getQuType();

            switch (quType) {
                case 1: // 单选题
                    question.setRadioTd(radioMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 2: // 多选题
                    question.setCheckboxTd(chaeckBoxMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 8: // 评分题
                    question.setScoreTd(scoreMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 9: // 排序题
                    question.setOrderByTd(orderQuMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 3:
                case 4: // 多行填空题
                    question.setMultifillblankTd(multifillblankMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 11:
                case 12:
                case 13:
                case 18:
                    question.setColumnTd(chenColMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setRowTd(chenRowMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                default:
            }
        });
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
