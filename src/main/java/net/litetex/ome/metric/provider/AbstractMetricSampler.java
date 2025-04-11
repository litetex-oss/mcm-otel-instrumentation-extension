package net.litetex.ome.metric.provider;

import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import io.opentelemetry.api.metrics.ObservableMeasurement;
import net.litetex.ome.OME;
import net.minecraft.server.MinecraftServer;


public abstract class AbstractMetricSampler<M extends ObservableMeasurement> implements MetricSampler
{
	protected final boolean isDebug;
	protected final Logger logger;
	protected final String name;
	protected final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback;
	
	protected AutoCloseable observable;
	protected MinecraftServer server;
	
	protected AbstractMetricSampler(
		final String name,
		final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback)
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.isDebug = this.logger.isDebugEnabled();
		this.name = name;
		this.buildWithCallback = buildWithCallback;
	}
	
	protected static AutoCloseable doubleGauge(
		final Meter meter,
		final String name,
		final Consumer<ObservableDoubleMeasurement> c)
	{
		return meter.gaugeBuilder(buildMetricName(name)).buildWithCallback(c);
	}
	
	protected static AutoCloseable longGauge(
		final Meter meter,
		final String name,
		final Consumer<ObservableLongMeasurement> c)
	{
		return meter.gaugeBuilder(buildMetricName(name)).ofLongs().buildWithCallback(c);
	}
	
	protected static AutoCloseable longCounter(
		final Meter meter,
		final String name,
		final Consumer<ObservableLongMeasurement> c)
	{
		return meter.counterBuilder(buildMetricName(name)).buildWithCallback(c);
	}
	
	protected static String buildMetricName(final String name)
	{
		return OME.instance().config().getMetrics().getPrefix() + name;
	}
	
	protected OME ome()
	{
		return OME.instance();
	}
	
	@Override
	public String name()
	{
		return this.name;
	}
	
	@Override
	public void register(final Meter meter, final MinecraftServer server)
	{
		if(this.observable != null)
		{
			this.logger.warn("Already registered {}, skipping", this.getClass().getSimpleName());
			return;
		}
		
		this.observable = this.buildWithCallback.apply(meter, this.name, this::doSample);
		this.server = server;
	}
	
	protected void doSample(final M measurement)
	{
		final long startNanos = this.isDebug ? System.nanoTime() : 0;
		try
		{
			this.sample(measurement);
		}
		catch(final Exception ex)
		{
			this.logger.warn("Failed to sample {}", this.name, ex);
		}
		finally
		{
			if(this.isDebug)
			{
				this.logger.debug(
					"Finished sampling {}, took {}ns",
					this.name,
					(System.nanoTime() - startNanos));
			}
		}
	}
	
	protected abstract void sample(M measurement);
	
	@Override
	public void close()
	{
		if(this.observable != null)
		{
			try
			{
				this.observable.close();
			}
			catch(final Exception ex)
			{
				this.logger.warn("Failed to close observable", ex);
			}
			this.observable = null;
		}
	}
}
