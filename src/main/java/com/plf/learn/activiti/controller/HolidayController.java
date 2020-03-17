package com.plf.learn.activiti.controller;

import com.plf.learn.activiti.bean.Holiday;
import com.plf.learn.activiti.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 请假流程Controller
 * @author Panlf
 * @date 2020/3/17
 */
@RestController
@Slf4j
@RequestMapping("holiday")
public class HolidayController {

    /**
     * 特别注意
     *      taskId是根据不同的办理人不同的id
     *      processInstanceId 流程实例Id 同一个流程中是不会变的
     *
     * */

    /**
     * 流程图key 每一个流程有对应的一个key这个是某一个流程内固定的写在bpmn内的
     *
     * 单独一个控制器处理请假流程
     */
    private static final String PROCESS_DEFINITION_KEY = "holiday";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 开启一个请假流程
     * @param holiday
     * @return
     */
    @GetMapping("start")
    public String startHolidayProcess(Holiday holiday){
        log.info("{} starts a holiday process instance.",holiday.getHolidayName());

        HashMap<String, Object> variables=new HashMap<>(1);
        variables.put("holiday", holiday);

        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey(PROCESS_DEFINITION_KEY,variables);

        /**
         * 流程实例ID
         * 流程定义ID
          */
        log.info("process instance id:{},process definition id:{}",instance.getId(),instance.getProcessDefinitionId());

        return "success";
    }


    /**
     * 指定某人的办理的流程信息
     *  例如张三申请请假，即可获取请假流程的信息
     * @return
     */
    @GetMapping("get")
    public List<Holiday> queryPresentHoliday(String assignee){
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(assignee).list();
        List<Holiday> result = new ArrayList<>();
        if(taskList != null){
            taskList.forEach(t->{
                //任务ID
                log.info("task id : {}",t.getId());
                //任务名称
                log.info("task name : {}",t.getName());
                //任务的创建时间
                log.info("task createtime : {}",t.getCreateTime());
                //任务的办理人
                log.info("task assignee : {}",t.getAssignee());
                //流程实例ID
                log.info("task process instance id : {}",t.getProcessInstanceId());
                //执行对象ID
                log.info("task execution id : {}",t.getExecutionId());
                //流程定义ID
                log.info("task process definition id : {}",t.getProcessDefinitionId());
                Object holidayInfo = FastJsonUtils.convertJsonToObject(taskService.getVariables(t.getId()).get("holiday").toString(),Holiday.class);
                log.info("holiday info:{}",holidayInfo);
                result.add((Holiday) holidayInfo);
            });
        }

        return result;
    }

    /**
     * 提交自己的流程
     * @param taskId
     * @return
     */
    @GetMapping("/complete")
    public String completeTask(String taskId){
        taskService.complete(taskId);
        return "success";
    }

    /**
     * 审核流程
     * @param taskId
     * @param status
     * @param remark
     * @return
     */
    @GetMapping("/audit")
    public String completeTask(String taskId,Integer status,String remark){
        Holiday holiday = new Holiday();
        holiday.setHolidayStatus(status);
        HashMap<String, Object> variables=new HashMap<>(1);
        variables.put("holiday", holiday);
        taskService.complete(taskId,variables);
        return "success";
    }


    @GetMapping("history")
    public String queryHistory(String processInstanceId){
        List<HistoricTaskInstance> list=historyService
                /**
                 *  创建历史活动实例查询
                 */
                .createHistoricTaskInstanceQuery()
                /**
                 *  执行流程实例id
                 */
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .asc()
                .list();

        list.forEach(l->{
            log.info("====================");
            log.info("iD:{}",l.getId());
            //流程实例ID
            log.info("process instance id:{}",l.getProcessInstanceId());
            log.info("name:{}",l.getName());
            //办理人
            log.info("assignee:{}",l.getAssignee());
            //开始时间
            log.info("startTime:{}",l.getStartTime());
            //结束时间
            log.info("endTime:{}",l.getEndTime());
            log.info("====================");
        });

        return "success";
    }


    @GetMapping("assignee")
    public String queryHistoryWithAssignee(String name) {
        List<HistoricTaskInstance> list = historyService
                /**
                 * 创建历史任务实例查询
                 */
                .createHistoricTaskInstanceQuery()
                /**
                 *  指定办理人
                 */
                .taskAssignee(name)
                /**
                 * 查询已经完成的任务
                 */
                .finished()
                .list();
        list.forEach(l->{
            log.info("====================");
            log.info("task id:{}",l.getId());
            //流程实例ID
            log.info("process instance id:{}",l.getProcessInstanceId());
            log.info("name:{}",l.getName());
            //办理人
            log.info("assignee:{}",l.getAssignee());
            //开始时间
            log.info("startTime:{}",l.getStartTime());
            //结束时间
            log.info("endTime:{}",l.getEndTime());
            log.info("====================");
        });

        return "success";
    }
}
