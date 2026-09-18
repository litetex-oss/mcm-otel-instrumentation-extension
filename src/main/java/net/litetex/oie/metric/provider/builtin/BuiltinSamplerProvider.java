package net.litetex.oie.metric.provider.builtin;

import java.util.Set;

import net.litetex.oie.metric.provider.MetricSampler;
import net.litetex.oie.metric.provider.SamplerProvider;


public class BuiltinSamplerProvider implements SamplerProvider
{
	@Override
	public Set<MetricSampler> createSamplers()
	{
		return Set.of(
			new AverageTickTimeSampler(),
			new LoadedEntitiesSampler(),
			new LoadedChunksSampler(),
			new NetworkConnectionsAvgPacketsReceivedSampler(),
			new NetworkConnectionsAvgPacketsSentSampler(),
			new NetworkConnectionsSampler(),
			new OnlinePlayersSampler(),
			new ServerPausedSampler()
		);
	}
}
