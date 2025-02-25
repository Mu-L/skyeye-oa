/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;
import com.skyeye.eve.checkbox.entity.DwQuCheckbox;
import com.skyeye.eve.chen.entity.DwAnChenCheckbox;
import com.skyeye.eve.chen.entity.DwAnChenRadio;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.order.entity.DwAnOrder;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.radio.entity.DwAnRadio;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.score.entity.DwQuScore;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DwQuestion
 * @Description:问题表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "dw_question")
@ApiModel(value = "问题表实体类")
public class DwQuestion extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("answer_input_row")
    @ApiModelProperty(value = "填空的input行")
    private Integer answerInputRow;

    @TableField("answer_input_width")
    @ApiModelProperty(value = "填空的input宽度")
    private Integer answerInputWidth;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷或题库")
    private String belongId;

    @TableField("cell_count")
    @ApiModelProperty(value = "按列显示时，列数", required = "required")
    private Integer cellCount;

    @TableField("check_type")
    @ApiModelProperty(value = "说明的验证方式")
    private Integer checkType;

    @TableField("contacts_attr")
    @ApiModelProperty(value = "1关联到联系人属性  0不关联到联系人属性")
    private Integer contactsAttr;

    @TableField("contacts_field")
    @ApiModelProperty(value = "关联的联系人字段")
    private String contactsField;

    @TableField("copy_from_id")
    @ApiModelProperty(value = "如果是复制的题，则有复制于那一题")
    private String copyFromId;

    @TableField("hv")
    @ApiModelProperty(value = "1水平显示 2垂直显示", required = "required")
    private Integer hv;

    @TableField("is_required")
    @ApiModelProperty(value = "是否必答 0非必答 1必答", required = "required")
    private Integer isRequired;

    @TableField("keywords")
    @ApiModelProperty(value = "关键字")
    private String keywords;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID")
    private Integer orderById;

    @TableField("param_int01")
    @ApiModelProperty(value = "枚举题 枚举项数目 ,评分题起始分值")
    private Integer paramInt01;

    @TableField("param_int02")
    @ApiModelProperty(value = "评分题，最大分值")
    private Integer paramInt02;

    @TableField("parent_qu_id")
    @ApiModelProperty(value = "所属大题  只有小题才有此属性 即quTag=3的题")
    private String parentQuId;

    @TableField("qu_name")
    @ApiModelProperty(value = "题目名称")
    private String quName;

    @TableField("qu_note")
    @ApiModelProperty(value = "题目说明")
    private String quNote;

    @TableField("qu_tag")
    @ApiModelProperty(value = "是否是大小题    1默认题  2大题  3大题下面的小题", required = "required")
    private Integer quTag;

    @TableField("qu_title")
    @ApiModelProperty(value = "题干")
    private String quTitle;

    @TableField("qu_type")
    @ApiModelProperty(value = "题目类型", required = "required")
    private Integer quType;

    @TableField("rand_order")
    @ApiModelProperty(value = "选项随机排列  1随机排列 0不随机排列", required = "required")
    private Integer randOrder;

    @TableField("tag")
    @ApiModelProperty(value = "标记  1题库中的题   2问卷中的题")
    private Integer tag;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示 0不显示   1显示", required = "required")
    private Integer visibility;

    @TableField("yesno_option")
    @ApiModelProperty(value = "是非题的选项 ")
    private Integer yesno_option;

    @TableField("is_public")
    @ApiModelProperty(value = "是否公开  0公开  1私有", defaultValue = "0")
    private Integer isPublic;

    @TableField("is_delete")
    @ApiModelProperty(value = "0表示问题已经删除，1.表示未删除，默认为1")
    private Integer isDelete;

    @TableField("file_type")
    @ApiModelProperty(value = "试题类型，0.默认没有，1.视频，2.音频，3.图片", required = "required")
    private Integer fileType;

    @TableField("whether_upload")
    @ApiModelProperty(value = "是否允许拍照/上传图片选中，1.是，2.否", required = "required")
    private Integer whetherUpload;

    @TableField("fraction")
    @ApiModelProperty(value = "每道题的分数，不能小于1")
    private Integer fraction;

    @TableField(exist = false)
    @ApiModelProperty(value = "单选题选项信息", required = "json")
    private List<DwQuRadio> radioTd;

    @TableField(exist = false)
    @Property(value = "单选题答案信息")
    private DwAnRadio radioAn;

    @TableField(exist = false)
    @ApiModelProperty(value = "评分题选项信息", required = "json")
    private List<DwQuScore> scoreTd;

    @TableField(exist = false)
    @Property(value = "评分题答案信息")
    private List<DwAnScore> scoreAn;

    @TableField(exist = false)
    @ApiModelProperty(value = "多选题选项信息", required = "json")
    private List<DwQuCheckbox> checkboxTd;

    @TableField(exist = false)
    @Property(value = "多选题答案信息")
    private List<DwAnCheckbox> checkboxAn;

    @TableField(exist = false)
    @ApiModelProperty(value = "矩阵题-列选项信息", required = "json")
    private List<DwQuChenColumn> columnTd;

    @TableField(exist = false)
    @Property(value = "矩阵题-列选项信息答案")
    private DwAnChenRadio chenAn;

    @TableField(exist = false)
    @ApiModelProperty(value = "矩阵题-行选项信息", required = "json")
    private List<DwQuChenRow> rowTd;

    @TableField(exist = false)
    @Property(value = "矩阵题-行选项信息答案")
    private List<DwAnChenCheckbox> chenRowAn;

    @TableField(exist = false)
    @ApiModelProperty(value = "多行填空题选项信息", required = "json")
    private List<DwQuMultiFillblank> multifillblankTd;

    @TableField(exist = false)
    @ApiModelProperty(value = "排序题选项信息", required = "json")
    private List<DwQuOrderby> orderbyTd;

    @TableField(exist = false)
    @Property(value = "排序题答案信息")
    private List<DwAnOrder> orderbyAn;

    @TableField(exist = false)
    @ApiModelProperty(value = "问题逻辑设置信息", required = "json")
    private List<DwQuestionLogic> questionLogic;
}