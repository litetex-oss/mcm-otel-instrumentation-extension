package net.litetex.oie.metric.provider;

import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriFunction;

import io.opentelemetry.api.metrics.Meter;
import net.litetex.oie.metric.ServerPausedSamplerCondition;
import net.litetex.oie.metric.measurement.TypedObservableMeasurement;
import net.minecraft.server.MinecraftServer;


public abstract class PausableNullSettingMetricSampler<T extends Number, M extends TypedObservableMeasurement<T>>
	extends CachedNullSettingMetricSampler<T, M>
{
	protected ServerPausedSamplerCondition serverPausedSamplerCondition;
	
	protected PausableNullSettingMetricSampler(
		final String name,
		final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback,
		final T metricRemovedValue)
	{
		super(name, buildWithCallback, metricRemovedValue);
	}
	
	@Override
	public void register(final Meter meter, final MinecraftServer server)
	{
		this.serverPausedSamplerCondition = this.oie().config().getMetrics().isFreezeWhenServerPaused()
			? ServerPausedSamplerCondition.create(server)
			: null;
		super.register(meter, server);
	}
	
	@Override
	protected boolean shouldSample()
	{
		return this.serverPausedSamplerCondition == null
			|| this.serverPausedSamplerCondition.shouldSample(this.server);
	}
	
	@Override
	public void close()
	{
		super.close();
		this.serverPausedSamplerCondition = null;
	}
}
