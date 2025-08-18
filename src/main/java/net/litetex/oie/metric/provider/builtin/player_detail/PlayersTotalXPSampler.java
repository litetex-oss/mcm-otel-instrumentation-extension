package net.litetex.oie.metric.provider.builtin.player_detail;

import net.litetex.oie.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.oie.metric.provider.CachedMetricSampler;
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
