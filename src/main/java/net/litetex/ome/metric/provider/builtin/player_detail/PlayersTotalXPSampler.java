package net.litetex.ome.metric.provider.builtin.player_detail;

import net.litetex.ome.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.ome.metric.provider.CachedMetricSampler;
import net.minecraft.server.network.ServerPlayerEntity;


public class PlayersTotalXPSampler extends PlayerMetricSampler<Long, TypedObservableLongMeasurement>
{
	public PlayersTotalXPSampler()
	{
		super("players_total_xp", CachedMetricSampler::typedLongGauge);
	}
	
	@Override
	protected Long getValueForPlayer(final ServerPlayerEntity player)
	{
		return (long)player.totalExperience;
	}
}
