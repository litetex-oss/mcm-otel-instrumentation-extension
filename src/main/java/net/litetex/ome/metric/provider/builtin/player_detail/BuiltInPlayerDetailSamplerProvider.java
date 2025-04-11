package net.litetex.ome.metric.provider.builtin.player_detail;

import java.util.Set;

import net.litetex.ome.OME;
import net.litetex.ome.metric.provider.MetricSampler;
import net.litetex.ome.metric.provider.SamplerProvider;
import net.minecraft.server.MinecraftServer;


public class BuiltInPlayerDetailSamplerProvider implements SamplerProvider
{
	@Override
	public boolean applicable(final MinecraftServer server)
	{
		return OME.instance().config().getMetrics().isEnablePlayerDetailsSamplers();
	}
	
	@Override
	public Set<MetricSampler> createSamplers()
	{
		return Set.of(
			new PlayersOnlineSampler(),
			new PlayersTotalXPSampler(),
			new PlayersXPLevelSampler()
		);
	}
}
