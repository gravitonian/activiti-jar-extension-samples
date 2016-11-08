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

/**
 *
 * @author martin.bergljung@alfresco.com
 */
import com.activiti.service.runtime.events.RuntimeEventListener;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.springframework.stereotype.Component;

@Component
public class RuntimeEventListenerImpl implements RuntimeEventListener {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        // Handle event here, offload to external system maybe
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}