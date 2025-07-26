package net.litetex.oie.metric.provider.builtin.player_detail;

import java.util.Set;

import net.litetex.oie.OIE;
import net.litetex.oie.metric.provider.MetricSampler;
import net.litetex.oie.metric.provider.SamplerProvider;
import net.minecraft.server.MinecraftServer;


public class BuiltInPlayerDetailSamplerProvider implements SamplerProvider
{
	@Override
	public boolean applicable(final MinecraftServer server)
	{
		return OIE.instance().config().getMetrics().isEnablePlayerDetailsSamplers();
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
