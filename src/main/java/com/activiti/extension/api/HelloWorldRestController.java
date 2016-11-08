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

package com.activiti.extension.api;

import com.activiti.domain.idm.User;
import com.activiti.extension.bean.service.HelloWorldService;
import com.activiti.extension.rest.GreetingMessage;
import com.activiti.service.api.UserService;
import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author martin.bergljung@alfresco.com
 */
@RestController
@RequestMapping("/enterprise")
public class HelloWorldRestController {

    @Autowired
    HelloWorldService helloWorldService;

    @Autowired
    UserService userService;

    @Timed
    @RequestMapping(value = "/hello", method= RequestMethod.GET)
    public GreetingMessage sayHello(@RequestParam(value="name", required=false, defaultValue="World") String name) {

        long userId = 1; // Admin
        User user = userService.findUser(userId);
        String username = user.getFirstName() + " " + user.getLastName();

        GreetingMessage msg = new GreetingMessage(name, "Hello " + name +
                ", service message: " + helloWorldService.greeting() + ", userId = 1 = " + username);



        return msg;
    }

    @Timed
    @RequestMapping(value = "/hello/{name}", method= RequestMethod.GET)
    public GreetingMessage sayHelloAgain(@PathVariable String name) {

        GreetingMessage msg = new GreetingMessage(name, "Hello " + name + "!");

        return msg;
    }
}
