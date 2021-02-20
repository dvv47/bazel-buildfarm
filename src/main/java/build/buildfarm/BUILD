load("@io_bazel_rules_docker//container:container.bzl", "container_image", "container_push")
load("//:defs.bzl", "ensure_accurate_metadata")

package(
    default_visibility = ["//src:__subpackages__"],
)

filegroup(
    name = "configs",
    srcs = ["logging.properties"],
    visibility = ["//visibility:public"],
)

genrule(
    name = "tini.binary",
    srcs = ["@tini//file"],
    outs = ["tini"],
    cmd = "cp $< $@ && chmod +x $@",
)

java_binary(
    name = "buildfarm-server",
    classpath_resources = [
        ":configs",
    ],
    jvm_flags = ensure_accurate_metadata(),
    main_class = "build.buildfarm.server.BuildFarmServer",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//src/main/java/build/buildfarm/server",
    ],
)

container_image(
    name = "server.container",
    base = "@java_image_base//image",
    cmd = [
        "buildfarm-server_deploy.jar",
        "/config/server.config",
        "--port",
        "8980",
    ],
    # leverage the implicit target of the buildfarm-server to get a fat jar.
    # this is simply a workaround for the fact that we have so many dependencies,
    # so we'd want some wrappy script. This seemed more straightforward.
    # https://docs.bazel.build/versions/master/be/java.html#java_binary_implicit_outputs
    files = [
        ":buildfarm-server_deploy.jar",
    ],
    tags = ["container"],
    visibility = ["//visibility:public"],
)

java_binary(
    name = "buildfarm-shard-worker",
    classpath_resources = [
        ":configs",
    ],
    jvm_flags = ensure_accurate_metadata(),
    main_class = "build.buildfarm.worker.shard.Worker",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//src/main/java/build/buildfarm/worker/shard",
    ],
)

container_image(
    name = "rwc_buildfarm_shard_worker_image",
    base = "@rwc_build//image",
    entrypoint = [
        "/usr/bin/java",
        "-jar",
    ],
    cmd = [
        "buildfarm-shard-worker_deploy.jar",
        "/config/buildfarm_shard_worker.config",
    ],
    # leverage the implicit target of the buildfarm-shard-worker to get a fat jar.
    # this is simply a workaround for the fact that we have so many dependencies,
    # so we'd want some wrappy script. This seemed more straightforward.
    # https://docs.bazel.build/versions/master/be/java.html#java_binary_implicit_outputs
    files = [
        ":buildfarm-shard-worker_deploy.jar",
    ],
    tags = ["container"],
    visibility = ["//visibility:public"],
)

container_push(
    name = "push_rwc_buildfarm_shard_worker_image",
    format = "Docker",
    image = ":rwc_buildfarm_shard_worker_image",
    registry = "registry.abagy.com",
    repository = "tools/rwc_buildfarm_shard_worker",
    tag = "latest",
    tags = ["container"],
)

java_library(
    name = "worker-cgroup",
    srcs = glob(["worker/cgroup/*.java"]),
    deps = [
        ":worker",
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:com_google_guava_guava",
    ],
)

java_library(
    name = "worker-resources",
    srcs = glob(["worker/resources/*.java"]),
    deps = [
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_googlecode_json_simple_json_simple",
    ],
)

java_library(
    name = "worker",
    srcs = glob(["worker/*.java"]),
    deps = [
        ":cas",
        ":common",
        ":instance",
        ":stub-instance",
        ":worker-resources",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@googleapis//:google_rpc_error_details_java_proto",
        "@maven//:com_github_jnr_jffi_native",
        "@maven//:com_github_jnr_jnr_constants",
        "@maven//:com_github_jnr_jnr_ffi",
        "@maven//:com_github_jnr_jnr_posix",
        "@maven//:com_github_pcj_google_options",
        "@maven//:com_github_serceman_jnr_fuse",
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
    ],
)

java_library(
    name = "operationqueue-worker",
    srcs = glob(["worker/operationqueue/*.java"]),
    deps = [
        ":cas",
        ":common",
        ":common-grpc",
        ":instance",
        ":stub-instance",
        ":worker-resources",
        ":worker",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@maven//:com_github_pcj_google_options",
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
    ],
)

java_library(
    name = "cas",
    srcs = glob(["cas/**/*.java"]),
    runtime_deps = [
        "@maven//:org_xerial_sqlite_jdbc",
    ],
    deps = [
        ":common",
        ":common-grpc",
        ":stub-instance",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@googleapis//:google_bytestream_bytestream_java_grpc",
        "@googleapis//:google_bytestream_bytestream_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@maven//:com_github_jnr_jnr_ffi",
        "@maven//:com_googlecode_json_simple_json_simple",
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_grpc",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_library(
    name = "ac",
    srcs = glob(["ac/*.java"]),
    deps = [
        ":common",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_core",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_grpc",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_binary(
    name = "buildfarm-operationqueue-worker",
    jvm_flags = ensure_accurate_metadata(),
    main_class = "build.buildfarm.worker.operationqueue.Worker",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//src/main/java/build/buildfarm/worker/operationqueue",
    ],
)

container_image(
    name = "rwc_buildfarm_worker_image",
    base = "@rwc_build//image",
    entrypoint = [
        "/usr/bin/java",
        "-jar"
    ],
    cmd = [
        "buildfarm-operationqueue-worker_deploy.jar",
        "/config/buildfarm_worker.config",
    ],
    # leverage the implicit target of the buildfarm-server to get a fat jar.
    # this is simply a workaround for the fact that we have so many dependencies,
    # so we'd want some wrappy script. This seemed more straightforward.
    # https://docs.bazel.build/versions/master/be/java.html#java_binary_implicit_outputs
    files = [
        ":buildfarm-operationqueue-worker_deploy.jar",
    ],
    tags = ["container"],
    visibility = ["//visibility:public"],
)

container_push(
    name = "push_rwc_buildfarm_worker_image",
    format = "Docker",
    image = ":rwc_buildfarm_worker_image",
    registry = "registry.abagy.com",
    repository = "tools/rwc_buildfarm_worker",
    tag = "latest",
    tags = ["container"],
)

java_binary(
    name = "bf-mount",
    srcs = ["Mount.java"],
    main_class = "build.buildfarm.Mount",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/stub",
        "//src/main/java/build/buildfarm/worker",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
    ],
)

java_binary(
    name = "bf-cache-load",
    srcs = ["CacheLoad.java"],
    classpath_resources = [
        ":configs",
    ],
    main_class = "build.buildfarm.CacheLoad",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/cas",
        "//src/main/java/build/buildfarm/common",
        "@maven//:com_github_jnr_jnr_constants",
        "@maven//:com_github_jnr_jnr_ffi",
        "@maven//:com_github_jnr_jnr_posix",
        "@maven//:com_google_guava_guava",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_plugin(
    name = "io-benchmark-plugins",
    processor_class = "org.openjdk.jmh.generators.BenchmarkProcessor",
    deps = [
        "@maven//:org_openjdk_jmh_jmh_core",
        "@maven//:org_openjdk_jmh_jmh_generator_annprocess",
    ],
)

java_binary(
    name = "bf-executor",
    srcs = ["Executor.java"],
    main_class = "build.buildfarm.Executor",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance/stub",
        "//src/main/java/build/buildfarm/worker",
        "@googleapis//:google_bytestream_bytestream_java_grpc",
        "@googleapis//:google_bytestream_bytestream_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@googleapis//:google_rpc_status_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_grpc",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_binary(
    name = "bf-extractor",
    srcs = ["Extract.java"],
    main_class = "build.buildfarm.Extract",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/common/grpc",
        "@googleapis//:google_bytestream_bytestream_java_grpc",
        "@googleapis//:google_bytestream_bytestream_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_binary(
    name = "bf-cat",
    srcs = ["Cat.java"],
    classpath_resources = [
        ":configs",
    ],
    main_class = "build.buildfarm.Cat",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/stub",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@googleapis//:google_rpc_error_details_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
    ],
)

java_binary(
    name = "bf-workerprofile",
    srcs = ["WorkerProfile.java"],
    classpath_resources = [
        ":configs",
    ],
    main_class = "build.buildfarm.WorkerProfile",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/common/redis",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/shard",
        "//src/main/java/build/buildfarm/instance/stub",
        "//src/main/java/build/buildfarm/worker/shard",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "//third_party/jedis",
        "@maven//:com_github_pcj_google_options",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
    ],
)

java_binary(
    name = "GracefulShutdownTest",
    srcs = ["GracefulShutdownTest.java"],
    main_class = "build.buildfarm.GracefulShutdownTest",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_grpc",
        "//src/main/protobuf:build_buildfarm_v1test_buildfarm_java_proto",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_netty",
    ],
)

java_binary(
    name = "bf-hist",
    srcs = ["Hist.java"],
    main_class = "build.buildfarm.Hist",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/stub",
        "@googleapis//:google_longrunning_operations_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_binary(
    name = "bf-index-worker",
    srcs = ["IndexWorker.java"],
    main_class = "build.buildfarm.IndexWorker",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/stub",
        "@googleapis//:google_longrunning_operations_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_binary(
    name = "bf-find-operations",
    srcs = ["FindOperations.java"],
    main_class = "build.buildfarm.FindOperations",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/stub",
        "//src/main/java/build/buildfarm/operations",
        "@googleapis//:google_longrunning_operations_java_proto",
        "@googleapis//:google_rpc_code_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@remote_apis//:build_bazel_remote_execution_v2_remote_execution_java_proto",
    ],
)

java_binary(
    name = "buildfarm-http-proxy",
    main_class = "build.buildfarm.proxy.http.HttpProxy",
    visibility = ["//visibility:public"],
    runtime_deps = ["//src/main/java/build/buildfarm/proxy/http"],
)

java_binary(
    name = "bf-cancel",
    srcs = ["Cancel.java"],
    main_class = "build.buildfarm.Cancel",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/build/buildfarm/common",
        "//src/main/java/build/buildfarm/instance",
        "//src/main/java/build/buildfarm/instance/stub",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_context",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
    ],
)
