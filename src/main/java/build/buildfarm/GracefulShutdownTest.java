// Copyright 2020 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package build.buildfarm;

import build.buildfarm.v1test.AdminGrpc;
import build.buildfarm.v1test.DisableScaleInProtectionRequest;
import build.buildfarm.v1test.PrepareWorkerForGracefulShutDownRequest;
import build.buildfarm.v1test.ShutDownWorkerGracefullyRequest;
import build.buildfarm.v1test.ShutDownWorkerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

class GracefulShutdownTest {
  private static ManagedChannel createChannel(String target) {
    NettyChannelBuilder builder =
        NettyChannelBuilder.forTarget(target).negotiationType(NegotiationType.PLAINTEXT);
    return builder.build();
  }

  /**
   * Example command: GracefulShutdownTest ShutDown workerIp buildfarm-endpoint
   *
   * @param args
   */
  private static void shutDownGracefully(String[] args) {
    String workerName = args[1];
    String bfEndpoint = args[2];
    System.out.println(
        "Sending ShutDownWorkerGracefully request to bf "
            + bfEndpoint
            + " to shut down worker "
            + workerName
            + " gracefully");
    ManagedChannel channel = createChannel(bfEndpoint);
    AdminGrpc.AdminBlockingStub adminBlockingStub = AdminGrpc.newBlockingStub(channel);
    adminBlockingStub.shutDownWorkerGracefully(
        ShutDownWorkerGracefullyRequest.newBuilder().setWorkerName(workerName).build());

    System.out.println("Request is sent");
  }

  /**
   * Example command: GracefulShutdownTest PrepareWorker WorkerIp:port
   *
   * @param args
   */
  private static void prepareWorkerForShutDown(String[] args) {
    String workerIpWithPort = args[1];
    System.out.println("Inform worker " + workerIpWithPort + " to prepare for shutdown!");
    ManagedChannel channel = createChannel(workerIpWithPort);
    ShutDownWorkerGrpc.ShutDownWorkerBlockingStub shutDownWorkerBlockingStub =
        ShutDownWorkerGrpc.newBlockingStub(channel);
    shutDownWorkerBlockingStub.prepareWorkerForGracefulShutdown(
        PrepareWorkerForGracefulShutDownRequest.newBuilder().build());
    System.out.println("Worker " + workerIpWithPort + " informed!");
  }

  /**
   * Example command: GracefulShutdownTest DisableProtection WorkerIp buildfarm_endpoint
   *
   * @param args
   */
  private static void disableScaleInProtection(String[] args) {
    String instancePrivateIp = args[1];
    String bfEndpoint = args[2];
    System.out.println("Ready to disable scale in protection of " + instancePrivateIp);
    ManagedChannel channel = createChannel(bfEndpoint);
    AdminGrpc.AdminBlockingStub adminBlockingStub = AdminGrpc.newBlockingStub(channel);
    adminBlockingStub.disableScaleInProtection(
        DisableScaleInProtectionRequest.newBuilder().setInstanceName(instancePrivateIp).build());
    System.out.println("Request for " + instancePrivateIp + " sent");
  }

  public static void main(String[] args) throws Exception {
    if (args[0].equals("ShutDown")) {
      shutDownGracefully(args);
    } else if (args[0].equals("PrepareWorker")) {
      prepareWorkerForShutDown(args);
    } else if (args[0].equals("DisableProtection")) {
      disableScaleInProtection(args);
    } else {
      System.out.println(
          "The action your choose is wrong. Please choose one from ShutDown, PrepareWorker, and DisableProtection");
    }
  }
}
