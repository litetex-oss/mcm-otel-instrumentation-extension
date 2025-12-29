package net.litetex.oie.metric.provider.builtin.player_detail;

import net.litetex.oie.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.oie.metric.provider.CachedMetricSampler;
import net.minecraft.server.level.ServerPlayer;


public class PlayersXPLevelSampler extends PlayerMetricSampler<Long, TypedObservableLongMeasurement>
{
	public PlayersXPLevelSampler()
	{
		super("players_xp_level", CachedMetricSampler::typedLongGauge);
	}
	
	@Override
	protected Long getValueForPlayer(final ServerPlayer player)
	{
		return (long)player.experienceLevel;
	}
}
