package net.litetex.ome.metric.provider.builtin;

import java.util.Set;

import net.litetex.ome.metric.provider.MetricSampler;
import net.litetex.ome.metric.provider.SamplerProvider;


public class BuiltinSamplerProvider implements SamplerProvider
{
	@Override
	public Set<MetricSampler> createSamplers()
	{
		return Set.of(
			new AverageTickTimeSampler(),
			new EntitiesSampler(),
			new LoadedChunksSampler(),
			new NetworkConnectionsAvgPacketsReceivedSampler(),
			new NetworkConnectionsAvgPacketsSentSampler(),
			new NetworkConnectionsSampler(),
			new OnlinePlayersSampler(),
			new ServerPausedSampler()
		);
	}
}
