package net.litetex.oie.metric.provider.spark;

import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.litetex.oie.metric.provider.MetricSampler;
import net.litetex.oie.metric.provider.SamplerProvider;
import net.minecraft.server.MinecraftServer;


public class SparkSamplerProvider implements SamplerProvider
{
	@Override
	public boolean applicable(final MinecraftServer server)
	{
		return FabricLoader.getInstance().getModContainer("spark").isPresent();
	}
	
	@Override
	public Set<MetricSampler> createSamplers()
	{
		return Set.of(
			new SparkTPSSampler(),
			new SparkMSPTSampler()
		);
	}
}
