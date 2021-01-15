workspace(name = "build_buildfarm")

load(":deps.bzl", "buildfarm_dependencies")

buildfarm_dependencies()

load(
    "@io_bazel_rules_docker//toolchains/docker:toolchain.bzl",
    docker_toolchain_configure = "toolchain_configure",
)

# Setup credentials for docker
docker_toolchain_configure(
    name = "docker_config",
    client_config = "/rwc/bazel/docker",
)

load(":defs.bzl", "buildfarm_init")
buildfarm_init()

load(":images.bzl", "buildfarm_images")

buildfarm_images()

load(":pip.bzl", "buildfarm_pip")

buildfarm_pip()
