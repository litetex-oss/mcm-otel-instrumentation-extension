package net.litetex.oie.config;

import net.litetex.oie.shared.config.Configuration;


public record Config(
	String instrumentationName,
	boolean stripIdentifierNamespaces,
	MetricsConfig metrics
)
{
	public Config(final Configuration lowLevelConfig)
	{
		this(
			lowLevelConfig.getString("instrumentation-name", "minecraft"),
			lowLevelConfig.getBoolean("strip-identifier-namespaces", true),
			new MetricsConfig(lowLevelConfig)
		);
	}
}
