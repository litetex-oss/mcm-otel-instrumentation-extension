package net.litetex.ome.metric.provider;

import io.opentelemetry.api.metrics.Meter;
import net.minecraft.server.MinecraftServer;


public interface MetricSampler extends AutoCloseable
{
	String name();
	
	void register(Meter meter, MinecraftServer server);
	
	@Override
	void close();
}
