package net.litetex.oie.metric.provider.builtin;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import net.litetex.oie.metric.CommonAttributeKeys;
import net.litetex.oie.metric.provider.AbstractMetricSampler;
import net.minecraft.server.world.ServerWorld;


public class OnlinePlayersSampler extends AbstractMetricSampler<ObservableLongMeasurement>
{
	public OnlinePlayersSampler()
	{
		super("online_players", AbstractMetricSampler::longGauge);
	}
	
	@Override
	protected void sample(final ObservableLongMeasurement measurement)
	{
		for(final ServerWorld world : this.server.getWorlds())
		{
			measurement.record(
				world.getPlayers().size(),
				Attributes.of(
					CommonAttributeKeys.WORLD,
					this.oie().formatWorldName(world)));
		}
	}
}
