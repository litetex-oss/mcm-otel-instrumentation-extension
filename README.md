<!-- modrinth_exclude.start -->

[![Version](https://img.shields.io/modrinth/v/mcm-otel-instrumentation-extension)](https://modrinth.com/mod/mcm-otel-instrumentation-extension)
[![Build](https://img.shields.io/github/actions/workflow/status/litetex-oss/mcm-otel-instrumentation-extension/check-build.yml?branch=dev)](https://github.com/litetex-oss/mcm-otel-instrumentation-extension/actions/workflows/check-build.yml?query=branch%3Adev)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=litetex-oss_mcm-otel-instrumentation-extension&metric=alert_status)](https://sonarcloud.io/dashboard?id=litetex-oss_mcm-otel-instrumentation-extension)

# OpenTelemetry Instrumentation Extension for Minecraft (Fabric)

<!-- modrinth_exclude.end -->

OpenTelemetry can be used to [report various monitoring data (including metrics, logs and traces)](https://opentelemetry.io/docs/what-is-opentelemetry/) and is a de-facto industry standard.

This mod provides additional instrumentation for the [OpenTelemetry JavaAgent](https://opentelemetry.io/docs/zero-code/java/agent/) so that various game metrics can be reported. The overall functionality is similar to [``fabric-exporter``](https://github.com/ruscalworld/fabric-exporter).

<!-- modrinth_exclude.start -->

## Installation
[Installation guide for the latest release](https://github.com/litetex-oss/mcm-otel-instrumentation-extension/releases/latest#Installation)

### Usage in other mods

Add the following to ``build.gradle``:
```groovy
dependencies {
    modImplementation 'net.litetex.mcm:otel-instrumentation-extension:<version>'
    // Further documentation: https://wiki.fabricmc.net/documentation:fabric_loom
}
```

> [!NOTE]
> The contents are hosted on [Maven Central](https://repo.maven.apache.org/maven2/net/litetex/mcm/). You shouldn't have to change anything as this is the default maven repo.<br/>
> If this somehow shouldn't work you can also try [Modrinth Maven](https://support.modrinth.com/en/articles/8801191-modrinth-maven).

## Related documentation
* [OTEL - Extending instrumentations with the API](https://opentelemetry.io/docs/zero-code/java/agent/api/)
* [OTEL - SDK configuration](https://opentelemetry.io/docs/languages/java/configuration)
* [OTEL - Disable default instrumentation](https://opentelemetry.io/docs/zero-code/java/agent/disable/#enable-manual-instrumentation-only)

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

<!-- modrinth_exclude.end -->
