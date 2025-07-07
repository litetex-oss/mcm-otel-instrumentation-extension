package net.litetex.oie.metric.provider.builtin;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import net.litetex.oie.metric.CommonAttributeKeys;
import net.litetex.oie.metric.provider.AbstractMetricSampler;
import net.minecraft.server.world.ServerWorld;


public class LoadedChunksSampler extends AbstractMetricSampler<ObservableLongMeasurement>
{
	public LoadedChunksSampler()
	{
		super("loaded_chunks", AbstractMetricSampler::longGauge);
	}
	
	@Override
	protected void sample(final ObservableLongMeasurement measurement)
	{
		for(final ServerWorld world : this.server.getWorlds())
		{
			measurement.record(
				world.getChunkManager().getLoadedChunkCount(),
				Attributes.of(
					CommonAttributeKeys.WORLD,
					this.oie().formatWorldName(world)));
		}
	}
}
