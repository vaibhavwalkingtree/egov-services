package org.egov.workflow.domain.service;

import java.util.List;

import org.egov.workflow.web.contract.Designation;
import org.egov.workflow.web.contract.ProcessInstance;
import org.egov.workflow.web.contract.Task;

public interface Workflow {

    ProcessInstance start(String jurisdiction, ProcessInstance processInstance);

    ProcessInstance end(String jurisdiction, ProcessInstance processInstance);

     ProcessInstance getProcess(String jurisdiction, ProcessInstance processInstance);

     List<Task> getTasks(String jurisdiction, ProcessInstance processInstance);

     ProcessInstance update(String jurisdiction, ProcessInstance processInstance);

    Task update(String jurisdiction, Task task);

    List<Task> getHistoryDetail(String tenantId,String workflowId);

    List<Designation> getDesignations(Task t, String departmentName);

    // List<Object> getAssignee(String deptCode, String designationName);

    Object getAssignee(Long locationId, String complaintTypeId, Long assigneeId);
}