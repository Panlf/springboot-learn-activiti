package com.plf.learn.activiti.bean;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Panlf
 * @date 2020/3/17
 */
@Data
public class Holiday  implements Serializable {
    private Integer id;
    /**
     * 申请人姓名
     */
    private String holidayName;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date beginDate;
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    /**
     * 请假天数
     */
    private Float holidayNum;
    /**
     * 事由
     */
    private String reason;
    /**
     * 请假类型
     */
    private String type;

    /**
     * 上级领导
     */
    private String leader;

    /**
     * 请假流程
     *  0 还未审核
     *  1 审核通过
     *  2 审核未通过
     */
    private Integer holidayStatus=0;

    /**
     * 备注信息
     */
    private String remark;

}
