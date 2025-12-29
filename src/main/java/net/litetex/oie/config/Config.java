package net.litetex.oie.config;

import java.util.Objects;


public class Config
{
	private String instrumentationName = "minecraft";
	private boolean stripIdentifierNamespaces = true;
	private MetricsConfig metrics = new MetricsConfig();
	
	public String getInstrumentationName()
	{
		return this.instrumentationName;
	}
	
	public void setInstrumentationName(final String instrumentationName)
	{
		this.instrumentationName = Objects.requireNonNull(instrumentationName);
	}
	
	public boolean isStripIdentifierNamespaces()
	{
		return this.stripIdentifierNamespaces;
	}
	
	public void setStripIdentifierNamespaces(final boolean stripIdentifierNamespaces)
	{
		this.stripIdentifierNamespaces = stripIdentifierNamespaces;
	}
	
	public MetricsConfig getMetrics()
	{
		return this.metrics;
	}
	
	public void setMetrics(final MetricsConfig metrics)
	{
		this.metrics = Objects.requireNonNull(metrics);
	}
	
	public static Config createDefault()
	{
		return new Config();
	}
}
