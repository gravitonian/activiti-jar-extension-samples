/**
 * Copyright (C) 2016 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK Samples project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.activiti.extension.bean;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 * @author martin.bergljung@alfresco.com
 */
public class HelloWorldExecutionListener implements ExecutionListener {
    private static Logger logger = LoggerFactory.getLogger(HelloWorldExecutionListener.class);

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        logger.info("[Process=" + execution.getProcessInstanceId() + "][event=" + execution.getEventName() +
                "][ExecutionListener=" + this + "][ActivityId=" + execution.getCurrentActivityId() + "]");

        String initiator = (String)execution.getVariable("initiator", false);
        logger.info("Initiator of the process has user ID = " + initiator);

        execution.setVariable("greeting1Proc", "Hello World!");
        execution.setVariableLocal("greeting1ProcLocal", "Hello World Local!");

        logger.info("--- Process variables:");
        Map<String, Object> procVars = execution.getVariables();
        for (Map.Entry<String, Object> procVar : procVars.entrySet()) {
            logger.info("   [" + procVar.getKey() + " = " + procVar.getValue() + "]");
        }
    }
}
