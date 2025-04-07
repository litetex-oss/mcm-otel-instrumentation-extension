package net.litetex.ome;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.DoubleGauge;
import io.opentelemetry.api.metrics.DoubleGaugeBuilder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongCounterBuilder;
import io.opentelemetry.api.metrics.LongGauge;
import io.opentelemetry.api.metrics.LongGaugeBuilder;
import io.opentelemetry.api.metrics.Meter;
import net.litetex.ome.config.MetricsConfig;


public class OMEMetricsCreator
{
	private static final Logger LOG = LoggerFactory.getLogger(OMEMetricsCreator.class);
	
	private final OME ome;
	private final MetricsConfig metricsConfig;
	
	public OMEMetricsCreator(final OME ome)
	{
		this.ome = ome;
		this.metricsConfig = ome.config().getMetrics();
	}
	
	public Meter getMeter()
	{
		return GlobalOpenTelemetry.getMeter(this.ome.instrumentationName());
	}
	
	protected String createMetricName(final String name)
	{
		return this.metricsConfig.getPrefix() + name;
	}
	
	public boolean isMetricActive(final String name)
	{
		if(this.metricsConfig.getEnabledOnly() != null)
		{
			return this.metricsConfig.getEnabledOnly().contains(name);
		}
		
		if(this.metricsConfig.getDisabled().contains(name))
		{
			return false;
		}
		if(this.metricsConfig.getEnabledAdditionally().contains(name))
		{
			return true;
		}
		return !MetricsConfig.DEFAULT_DISABLED.contains(name);
	}
	
	public <M, B> M createMetric(
		final BiFunction<Meter, String, B> createBuilder,
		final Function<B, M> build,
		final String name,
		final Consumer<B> customizeBuilder)
	{
		if(!this.isMetricActive(name))
		{
			return null;
		}
		
		final B builder = createBuilder.apply(this.getMeter(), this.createMetricName(name));
		if(customizeBuilder != null)
		{
			customizeBuilder.accept(builder);
		}
		final M metric = build.apply(builder);
		LOG.debug("Built {} for {}", metric.getClass().getSimpleName(), name);
		return metric;
	}
	
	public LongCounter createLongCounter(final String name)
	{
		return this.createLongCounter(name, null);
	}
	
	public LongCounter createLongCounter(
		final String name,
		final Consumer<LongCounterBuilder> customizeBuilder)
	{
		return this.createMetric(
			Meter::counterBuilder,
			LongCounterBuilder::build,
			name,
			customizeBuilder);
	}
	
	public DoubleGauge createDoubleGauge(final String name)
	{
		return this.createDoubleGauge(name, null);
	}
	
	public DoubleGauge createDoubleGauge(
		final String name,
		final Consumer<DoubleGaugeBuilder> customizeBuilder)
	{
		return this.createMetric(
			Meter::gaugeBuilder,
			DoubleGaugeBuilder::build,
			name,
			customizeBuilder);
	}
	
	public LongGauge createLongGauge(final String name)
	{
		return this.createLongGauge(name, null);
	}
	
	public LongGauge createLongGauge(
		final String name,
		final Consumer<LongGaugeBuilder> customizeBuilder)
	{
		return this.createMetric(
			(meter, gaugeName) -> meter.gaugeBuilder(gaugeName).ofLongs(),
			LongGaugeBuilder::build,
			name,
			customizeBuilder);
	}
}
