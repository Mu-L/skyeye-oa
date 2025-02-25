package com.skyeye.eve.myquestionbank.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.myquestionbank.dao.MatrixMultipleChoiceQuestionDao;
import com.skyeye.eve.myquestionbank.entity.MatrixMultipleChoiceQuestion;
import com.skyeye.eve.myquestionbank.service.MatrixMultipleChoiceQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "矩阵多选题管理", groupName = "矩阵多选题管理")
public class MatrixMultipleChoiceQuestionServiceImpl extends SkyeyeBusinessServiceImpl<MatrixMultipleChoiceQuestionDao, MatrixMultipleChoiceQuestion> implements MatrixMultipleChoiceQuestionService {

    @Autowired
    private MatrixMultipleChoiceQuestionDao matrixMultipleChoiceQuestionDao;

    /**
     * 编辑题目信息
     * map-> belongId:试卷id
     * quTitle:问题标题
     * orderById:序号
     * tag:表示题目是试卷题还是题库中题
     * answerInputWidth:填空的input宽度
     * answerInputRow:填空的input行
     * contactsAttr:1关联到联系人属性  0不关联到联系人属性
     * contactsField:关联的联系人字段
     * checkType:说明的验证方式
     * hv:1水平显示 2垂直显示
     * randOrder:选项随机排列  1随机排列 0不随机排列
     * cellCount:按列显示时，列数
     * fraction:分数
     * quId:问题id------非必填
     *
     * @return
     */
    private String compileQuestion(Map<String, Object> question, String userId) {
        String quId = "";
        //判断题目id是否为空，为空则新增，不为空则修改
        if (ToolUtil.isBlank(question.get("quId").toString())) {
            quId = ToolUtil.getSurFaceId();
            question.put("id", quId);
            question.put("quTag", 1);
            question.put("visibility", 1);
            question.put("createId", userId);
            question.put("createTime", DateUtil.getTimeAndToString());
            matrixMultipleChoiceQuestionDao.addQuestionMation(question);
        } else {
            quId = question.get("quId").toString();
            matrixMultipleChoiceQuestionDao.editQuestionMationById(question);
        }
        return quId;
    }

    /**
     * 操作问题和知识点的绑定信息
     *
     * @param schoolKnowledgeMationList
     * @param questionId
     */
    private void operatorQuestionAndKnowledge(String schoolKnowledgeMationList, String questionId) {
        List<Map<String, Object>> beans = JSONUtil.toList(schoolKnowledgeMationList, null);
        List<Map<String, Object>> items = new ArrayList<>();
        beans.stream().forEach(bean -> {
            if (bean.containsKey("id") && !ToolUtil.isBlank(bean.get("id").toString())) {
                bean.put("questionId", questionId);
                items.add(bean);
            }
        });
        //删除之前的绑定关系
        matrixMultipleChoiceQuestionDao.deleteOldBindingByQuId(questionId);
        if (!items.isEmpty()) {
            matrixMultipleChoiceQuestionDao.insertNewBinding(items);
        }
    }

    /**
     * 根据题目id获取知识点
     *
     * @param quId
     * @return
     */
    private List<Map<String, Object>> getKnowledgeListBuQuId(String quId) {
        List<Map<String, Object>> knowledgeList = matrixMultipleChoiceQuestionDao.queryQuestionKnowledgeByQuestionId(quId);
        return knowledgeList;
    }


    /**
     * 新增矩阵单选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addMatrixMultiplechoiceQuestionMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> column = JSONUtil.toList(map.get("column").toString(), null);
        List<Map<String, Object>> row = JSONUtil.toList(map.get("row").toString(), null);
        if (column.size() > 0 && row.size() > 0) {
            int quType = QuType.getIndex(map.get("quType").toString());
            if (-1 == quType) {
                outputObject.setreturnMessage("参数值错误！");
                return;
            } else {
                map.put("quType", quType);
            }
            Map<String, Object> user = inputObject.getLogParams();
            // 添加问题并返回问题id
            //---------改为了protected
            String quId = compileQuestion(map, user.get("id").toString());
            // 知识点关联
            operatorQuestionAndKnowledge(map.get("schoolKnowledgeMationList").toString(), quId);

            List<Map<String, Object>> quColumn = new ArrayList<>();
            List<Map<String, Object>> editquColumn = new ArrayList<>();
            Map<String, Object> bean;
            for (int i = 0; i < column.size(); i++) {
                Map<String, Object> object = column.get(i);
                bean = new HashMap<>();
                bean.put("orderById", object.get("key"));
                bean.put("optionName", object.get("optionValue"));
                if (ToolUtil.isBlank(object.get("optionId").toString())) {
                    bean.put("quId", quId);
                    bean.put("visibility", 1);
                    bean.put("id", ToolUtil.getSurFaceId());
                    bean.put("createId", user.get("id"));
                    bean.put("createTime", DateUtil.getTimeAndToString());
                    quColumn.add(bean);
                } else {
                    bean.put("id", object.get("optionId"));
                    editquColumn.add(bean);
                }
            }
            // 删除要删除的问题项
            List<String> deleteColumnList = JSONUtil.toList(map.get("deleteColumnList").toString(), null);
            if (!deleteColumnList.isEmpty()) {
                matrixMultipleChoiceQuestionDao.deleteQuestionColumnOptionMationList(deleteColumnList);
            }
            if (!quColumn.isEmpty()) {
                matrixMultipleChoiceQuestionDao.addQuestionColumnMationList(quColumn);
            }
            if (!editquColumn.isEmpty()) {
                matrixMultipleChoiceQuestionDao.editQuestionColumnMationList(editquColumn);
            }

            List<Map<String, Object>> quRow = new ArrayList<>();
            List<Map<String, Object>> editquRow = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                Map<String, Object> object = row.get(i);
                bean = new HashMap<>();
                bean.put("orderById", object.get("key"));
                bean.put("optionName", object.get("optionValue"));
                if (ToolUtil.isBlank(object.get("optionId").toString())) {
                    bean.put("quId", quId);
                    bean.put("visibility", 1);
                    bean.put("id", ToolUtil.getSurFaceId());
                    bean.put("createId", user.get("id"));
                    bean.put("createTime", DateUtil.getTimeAndToString());
                    quRow.add(bean);
                } else {
                    bean.put("id", object.get("optionId"));
                    editquRow.add(bean);
                }
            }
            // 删除要删除的问题项
            List<String> deleteRowList = JSONUtil.toList(map.get("deleteRowList").toString(), null);
            if (!deleteRowList.isEmpty()) {
                matrixMultipleChoiceQuestionDao.deleteQuestionRowOptionMationList(deleteRowList);
            }
            if (!quRow.isEmpty()) {
                matrixMultipleChoiceQuestionDao.addQuestionRowMationList(quRow);
            }
            if (!editquRow.isEmpty()) {
                matrixMultipleChoiceQuestionDao.editQuestionRowMationList(editquRow);
            }
        } else {
            outputObject.setreturnMessage("选项不能为空");
        }
    }

    /**
     * 编辑矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMatrixMultiplechoiceQuestionMationToEditById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String quId = map.get("id").toString();
        // 获取题目信息
        Map<String, Object> question = matrixMultipleChoiceQuestionDao.queryQuestionMationById(quId);
        if (question != null && !question.isEmpty()) {
            String quType = QuType.getActionName(Integer.parseInt(question.get("quType").toString()));
            // 获取选项
            List<Map<String, Object>> questionChenRow = matrixMultipleChoiceQuestionDao.queryQuestionChenRowListByQuestionId(question);// 获取行选项
            List<Map<String, Object>> questionChenColumn = matrixMultipleChoiceQuestionDao.queryQuestionChenColumnListByQuestionId(question);// 获取列选项
            for (Map<String, Object> bean : questionChenRow) {
                for (Map<String, Object> item : questionChenColumn) {
                    item.put("rowId", bean.get("id"));
                }
                bean.put("questionChenColumn", questionChenColumn);
            }
            question.put("questionChenRow", questionChenRow);
            question.put("questionChenColumn", questionChenColumn);
            if (quType.equals(QuType.COMPCHENRADIO.getActionName())) {// 如果是复合矩阵单选题， 则还有题选项
                List<Map<String, Object>> questionChenOption = matrixMultipleChoiceQuestionDao.queryQuestionChenOptionListByQuestionId(question);// 获取选项
                question.put("questionChenOption", questionChenOption);
            }
            // 获取知识点
            question.put("knowledgeList", getKnowledgeListBuQuId(quId));
            question.put("quTypeName", quType.toUpperCase());
            outputObject.setBean(question);
        } else {
            outputObject.setreturnMessage("该题目信息不存在。");
        }
    }
}
