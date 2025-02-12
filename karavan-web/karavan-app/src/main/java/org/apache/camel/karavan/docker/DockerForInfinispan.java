/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.karavan.docker;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.karavan.infinispan.model.ContainerStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class DockerForInfinispan {

    private static final Logger LOGGER = Logger.getLogger(DockerForInfinispan.class.getName());

    protected static final String INFINISPAN_CONTAINER_NAME = "infinispan";

    @ConfigProperty(name = "karavan.infinispan.username")
    String infinispanUsername;
    @ConfigProperty(name = "karavan.infinispan.password")
    String infinispanPassword;

    @Inject
    DockerService dockerService;

    @Inject
    EventBus eventBus;

    public void startInfinispan() {
        try {
            LOGGER.info("Infinispan is starting...");
            var compose = dockerService.getInternalDockerComposeService(INFINISPAN_CONTAINER_NAME);
            compose.addEnvironment("USER", infinispanUsername);
            compose.addEnvironment("PASS", infinispanPassword);
            dockerService.createContainerFromCompose(compose, ContainerStatus.ContainerType.internal);
            dockerService.runContainer(INFINISPAN_CONTAINER_NAME);
            LOGGER.info("Infinispan is started");
        } catch (Exception e) {
            LOGGER.error(e.getCause().getMessage());
        }
    }

//    public void checkInfinispanHealth() {
//        dockerService.listContainers(false).stream()
//                .filter(c -> c.getState().equals("running"))
//                .forEach(c -> {
//                    HealthState hs = dockerService.inspectContainer(c.getId()).getState().getHealth();
//                    if (c.getNames()[0].equals("/" + INFINISPAN_CONTAINER_NAME)) {
//                        eventBus.publish(INFINISPAN_STARTED, hs.getStatus());
//                    }
//                });
//    }
}
