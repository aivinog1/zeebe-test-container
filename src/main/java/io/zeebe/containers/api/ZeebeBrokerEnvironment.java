/*
 * Copyright © 2019 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.containers.api;

import io.zeebe.containers.impl.EnvVar;

public interface ZeebeBrokerEnvironment extends ZeebeEnvironment {

  EnvVar getDebugFlag();

  EnvVar getEmbedGatewayFlag();

  EnvVar getBrokerNodeId();

  EnvVar getBrokerHost();

  EnvVar getPortOffset();

  EnvVar getContactPoints();

  EnvVar getPartitionsCount();

  EnvVar getReplicationFactor();

  EnvVar getClusterSize();

  EnvVar getClusterName();

  EnvVar getGatewayHost();

  EnvVar getGatewayPort();

  EnvVar getGatewayKeepAliveInterval();

  EnvVar getGatewayRequestTimeout();

  EnvVar getGatewayManagementThreadCount();
}
