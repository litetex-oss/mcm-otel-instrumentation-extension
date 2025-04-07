package net.litetex.ome;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.metrics.Meter;
import net.litetex.external.org.springframework.util.ConcurrentReferenceHashMap;
import net.litetex.ome.config.Config;
import net.litetex.ome.metric.provider.MetricSampler;
import net.litetex.ome.metric.provider.SamplerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;


public class OME
{
	private static final Logger LOG = LoggerFactory.getLogger(OME.class);
	
	private static OME instance;
	
	public static OME instance()
	{
		return instance;
	}
	
	public static void setInstance(final OME instance)
	{
		OME.instance = instance;
	}
	
	private final Config config;
	private final OMEMetricsCreator metricsCreator;
	
	private List<MetricSampler> registeredSamplers;
	
	public OME(final Config config)
	{
		this.config = config;
		this.metricsCreator = new OMEMetricsCreator(this);
	}
	
	public Config config()
	{
		return this.config;
	}
	
	public String instrumentationName()
	{
		return this.config().getInstrumentationName();
	}
	
	public OMEMetricsCreator metricsCreator()
	{
		return this.metricsCreator;
	}
	
	public void onServerStarted(final MinecraftServer server)
	{
		LOG.debug("Handling onServerStarted");
		final Thread thread = new Thread(() -> {
			LOG.debug("Bootstrapping...");
			final long start = System.currentTimeMillis();
			
			final Meter meter = this.metricsCreator().getMeter();
			
			this.registeredSamplers = ServiceLoader.load(SamplerProvider.class)
				.stream()
				.map(ServiceLoader.Provider::get)
				.filter(p -> p.applicable(server))
				.flatMap(provider -> {
					final List<MetricSampler> samplers = provider.createSamplers().stream()
						.filter(m -> this.metricsCreator().isMetricActive(m.name()))
						.toList();
					
					samplers.forEach(s -> s.register(meter, server));
					return samplers.stream();
				})
				.toList();
			
			if(LOG.isInfoEnabled())
			{
				LOG.info(
					"Registered metric samplers (took {}ms): {}",
					System.currentTimeMillis() - start,
					this.registeredSamplers.stream()
						.map(MetricSampler::name)
						.toList());
			}
			
		});
		thread.setName("Minecraft-OpenTelemetry-Sampler-Bootstrap");
		thread.setDaemon(true);
		thread.start();
	}
	
	public void onSeverStopping()
	{
		LOG.debug("Handling onServerStopping");
		
		this.registeredSamplers.forEach(MetricSampler::close);
		this.registeredSamplers.clear();
	}
	
	private final Map<Identifier, String> formatCache = new ConcurrentReferenceHashMap<>(
		16,
		ConcurrentReferenceHashMap.ReferenceType.WEAK);
	
	public String formatIdentifier(final Identifier identifier)
	{
		return this.formatCache.computeIfAbsent(
			identifier,
			id -> this.config.isStripIdentifierNamespaces()
				? id.getPath()
				: id.toString());
	}
	
	public String formatWorldName(final ServerWorld world)
	{
		return this.formatIdentifier(world.getRegistryKey().getValue());
	}
}
