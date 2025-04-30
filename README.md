<!-- modrinth_exclude.start -->

[![Version](https://img.shields.io/modrinth/v/otel-instrumentation-extension)](https://modrinth.com/mod/otel-instrumentation-extension)
[![Build](https://img.shields.io/github/actions/workflow/status/litetex-oss/mcm-otel-instrumentation-extension/check-build.yml?branch=dev)](https://github.com/litetex-oss/mcm-otel-instrumentation-extension/actions/workflows/check-build.yml?query=branch%3Adev)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=litetex-oss_mcm-otel-instrumentation-extension&metric=alert_status)](https://sonarcloud.io/dashboard?id=litetex-oss_mcm-otel-instrumentation-extension)

# OpenTelemetry Instrumentation Extension for FabricMC

<!-- modrinth_exclude.end -->

OpenTelemetry can be used to [report various monitoring data (including metrics, logs and traces)](https://opentelemetry.io/docs/what-is-opentelemetry/) and is a de-facto industry standard.

This mod provides additional instrumentation for the [OpenTelemetry JavaAgent](https://opentelemetry.io/docs/zero-code/java/agent/) so that various game metrics can be reported. The overall functionality is similar to [``fabric-exporter``](https://github.com/ruscalworld/fabric-exporter).

## Setup 

1. Make sure you have an endpoint where OpenTelemetry data can be ingested.<br/>This could be a [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/) hosted by you or by someone else (e.g. in the [Grafana Cloud](https://grafana.com/docs/grafana-cloud/monitor-applications/application-observability/collector/)).
2. [Setup the OpenTelemetry Java Agent](https://opentelemetry.io/docs/zero-code/java/agent/getting-started/) so that it sends data to your ingestion service.
3. Add the [OpenTelemetry Agent Helper Extension for Fabric](https://github.com/litetex-oss/otel-fabric-helper-extension).
    * This is required so that OpenTelemetry Agent is correctly detecting and utilizing the mod.
4. Simply add and configure this mod. It should work out of the box.
5. Import the provided dashboard into Grafana
    * From [Grafana Dashboards](https://grafana.com/grafana/dashboards/23234)
    * From the [local demo in the repo](https://github.com/litetex-oss/mcm-otel-instrumentation-extension/tree/dev/_monitoring_dev_infra/docker-compose.yml)

<details><summary>Diagram showcasing how the overall infrastructure can look</summary>

![Infra Overview](https://raw.githubusercontent.com/litetex-oss/mcm-otel-instrumentation-extension/refs/heads/dev/assets/InfraOverview.svg)

</details>

You may also have a look at the corresponding [development setup](https://github.com/litetex-oss/mcm-otel-instrumentation-extension?tab=readme-ov-file#demo-for-development) as it contains an locally running deployment of the full infrastructure.

<details><summary>Dashboard look</summary>

![Overview](https://raw.githubusercontent.com/litetex-oss/mcm-otel-instrumentation-extension/refs/heads/dev/assets/dashboard-overview.jpg)
![Network](https://raw.githubusercontent.com/litetex-oss/mcm-otel-instrumentation-extension/refs/heads/dev/assets/dashboard-network.jpg)
![Chunk Generation](https://raw.githubusercontent.com/litetex-oss/mcm-otel-instrumentation-extension/refs/heads/dev/assets/dashboard-chunk-generation.jpg)
![Entities](https://raw.githubusercontent.com/litetex-oss/mcm-otel-instrumentation-extension/refs/heads/dev/assets/dashboard-entities.jpg)
![Player](https://raw.githubusercontent.com/litetex-oss/mcm-otel-instrumentation-extension/refs/heads/dev/assets/dashboard-players.jpg)

</details>

## Configuration

The default configuration of the mod should usually work out of the box.<br/>
If you wish to customize it, open ``config/oie.json`` and tune the corresponding values.<br/>
Further documentation can be found directly inside the corresponding [Java Code](https://github.com/litetex-oss/mcm-otel-instrumentation-extension/tree/dev/src/main/java/net/litetex/oie/config/).

<!-- modrinth_exclude.start -->

## Demo for development

* Make sure that you have Docker and IntelliJ installed
* Checkout the repository
* [Download](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases) the OpenTelemetry JavaAgent and put it into the ``run`` folder
* Go into [``_monitoring_dev_infra``](./_monitoring_dev_infra/) and start it with ``docker compose up``
* Log in to Grafana at [``localhost:3000``](http://localhost:3000) (username and password are ``admin``)
* Open IntelliJ and run the ``Minecraft Server (OTEL-AGENT)`` launcher

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

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

### Related documentation
* [OTEL - Extending instrumentations with the API](https://opentelemetry.io/docs/zero-code/java/agent/api/)
* [OTEL - SDK configuration](https://opentelemetry.io/docs/languages/java/configuration)
* [OTEL - Disable default instrumentation](https://opentelemetry.io/docs/zero-code/java/agent/disable/#enable-manual-instrumentation-only)

<!-- modrinth_exclude.end -->
