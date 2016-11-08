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

import com.activiti.domain.idm.User;
import com.activiti.extension.bean.service.HelloWorldService;
import com.activiti.service.api.UserService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Service Task Spring Bean Java Delegate
 *
 * Use the following to have one instance created per service task instance
 * @Scope(BeanDefinition.SCOPE_PROTOTYPE)
 *
 * @author martin.bergljung@alfresco.com
 */
@Component("helloWorld")
public class HelloWorldSpringJavaDelegate implements JavaDelegate {
    private static Logger logger = LoggerFactory.getLogger(HelloWorldSpringJavaDelegate.class);

    /**
     * Expression representing the greeting class field
     */
    private Expression greeting;

    @Autowired
    HelloWorldService simpleService;

    @Autowired
    UserService userService;

    public void setGreeting(Expression greeting) {
        this.greeting = greeting;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("[Process=" + execution.getProcessInstanceId() + "][Spring Java Delegate=" + this + "]");

        String greetingText = (String) greeting.getValue(execution);
        logger.info("The greeting set for this service task is: " + greetingText);

        logger.info("Injected Spring Bean greeting: " + simpleService.greeting());

        User user = userService.findUser(Long.parseLong((String) execution.getVariable("initiator")));
        String username = user.getFirstName() + " " + user.getLastName();
        logger.info("Initiator is: " + username);


        List someList = new ArrayList<String>();

        execution.setVariable("someList", someList);
    }
}

