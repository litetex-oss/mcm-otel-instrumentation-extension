package net.litetex.ome.metric.provider;

import java.util.Set;

import net.minecraft.server.MinecraftServer;


public interface SamplerProvider
{
	default boolean applicable(final MinecraftServer server)
	{
		return true;
	}
	
	Set<MetricSampler> createSamplers();
}
