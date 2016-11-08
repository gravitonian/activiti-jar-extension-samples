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

package com.activiti.extension.bean.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A simple service
 *
 * @author martin.bergljung@alfresco.com
 */
@Service
public class HelloWorldService {
    private static Logger logger = LoggerFactory.getLogger(HelloWorldService.class);

    public String greeting() {
        logger.info("Custom Service method greeting() was called");

        return "Hello World from Service!";
    }

    public String customGreetingFromExecListener(DelegateExecution execution, String text) {
        logger.info("[Process=" + execution.getProcessInstanceId() + "][Java Delegate=" + this + "]");
        logger.info("Hello World: " + text);

        return "Something back from service!";
    }

    public String customGreetingFromTaskListener(DelegateTask task, String text) {
        logger.info("[Process=" + task.getExecution().getProcessInstanceId() + "][Java Delegate=" + this + "]");
        logger.info("Hello World: " + text);


        return "Something back from service!";
    }

}

