package net.litetex.ome.metric.provider.builtin.player_detail;

import net.litetex.ome.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.ome.metric.provider.CachedMetricSampler;
import net.minecraft.server.network.ServerPlayerEntity;


public class PlayersXPLevelSampler extends PlayerMetricSampler<Long, TypedObservableLongMeasurement>
{
	public PlayersXPLevelSampler()
	{
		super("players_xp_level", CachedMetricSampler::typedLongGauge);
	}
	
	@Override
	protected Long getValueForPlayer(final ServerPlayerEntity player)
	{
		return (long)player.experienceLevel;
	}
}
