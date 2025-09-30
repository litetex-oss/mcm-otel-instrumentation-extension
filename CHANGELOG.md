# 1.2.1
* Now targeting 1.21.9
* Updated opentelemetry-api

# 1.2.0
* Add an option to change the suffix of counters (``counterSuffix``). Defaults to ``_total`` for new configurations.
  * You might have to re-generate your config
* Change dashboard to use native OpenTelemetry ingestion (and not prometheus remote write)
  * This causes some counters to no longer include the ``_total`` suffix.
* Improve error handling when encountering a not parseable config file
* Now targeting 1.21.8
* Updated opentelemetry-api

# 1.1.0
* Now targeting 1.21.6
* [25w21a] Fix compilation

# 1.0.1
* Updated opentelemetry-api

# 1.0.0
_Initial stable release_
* No noteworthy changes since 0.1

# 0.1.X
* Fixed integration with OpenTelemetry Agent, see [OpenTelemetry Agent Helper Extension for Fabric](https://github.com/litetex-oss/otel-fabric-helper-extension) for details
* Updated docs

# 0.0.X
_Initial release for integration testing with monitoring systems_
