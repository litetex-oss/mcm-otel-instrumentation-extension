package net.litetex.ome.metric.provider;

import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriFunction;

import io.opentelemetry.api.metrics.Meter;
import net.litetex.ome.metric.ServerPausedSamplerCondition;
import net.litetex.ome.metric.measurement.TypedObservableMeasurement;
import net.minecraft.server.MinecraftServer;


public abstract class PauseableMetricSampler<T extends Number, M extends TypedObservableMeasurement<T>>
	extends CachedMetricSampler<T, M>
{
	protected ServerPausedSamplerCondition serverPausedSamplerCondition;
	
	protected PauseableMetricSampler(
		final String name,
		final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback)
	{
		super(name, buildWithCallback);
	}
	
	@Override
	public void register(final Meter meter, final MinecraftServer server)
	{
		this.serverPausedSamplerCondition = new ServerPausedSamplerCondition(server);
		super.register(meter, server);
	}
	
	@Override
	protected boolean shouldSample()
	{
		return this.serverPausedSamplerCondition.shouldSample(this.server);
	}
	
	@Override
	public void close()
	{
		super.close();
		this.serverPausedSamplerCondition = null;
	}
}
