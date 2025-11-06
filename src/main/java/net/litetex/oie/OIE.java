package net.litetex.oie;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongGauge;
import io.opentelemetry.api.metrics.Meter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.litetex.oie.config.Config;
import net.litetex.oie.external.org.springframework.util.ConcurrentReferenceHashMap;
import net.litetex.oie.metric.provider.MetricSampler;
import net.litetex.oie.metric.provider.SamplerProvider;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;


public class OIE
{
	private static final Logger LOG = LoggerFactory.getLogger(OIE.class);
	
	private static OIE instance;
	
	private final Config config;
	private final OIEMetricsCreator metricsCreator;
	
	private List<MetricSampler> registeredSamplers;
	private LongGauge up;
	
	public OIE(final Config config)
	{
		this.config = config;
		this.metricsCreator = new OIEMetricsCreator(this);
		
		this.validateOpenTelemetry();
	}
	
	public Config config()
	{
		return this.config;
	}
	
	public String instrumentationName()
	{
		return this.config().getInstrumentationName();
	}
	
	public void validateOpenTelemetry()
	{
		LOG.info("Validating used OpenTelemetry implementation...");
		
		final String otelImplName = GlobalOpenTelemetry.get().getClass().getName();
		switch(otelImplName)
		{
			case "io.opentelemetry.api.DefaultOpenTelemetry":
				LOG.error("Failed to detect presence of OpenTelemetry Agent!");
				break;
			case "io.opentelemetry.javaagent.instrumentation.opentelemetryapi.ApplicationOpenTelemetry":
				LOG.error("OpenTelemetry Agent failed to properly hook API (it's using outdated v1)!");
				LOG.warn("You can work around this problem by either:");
				LOG.warn("* Using the OpenTelemetry Agent Helper Extension for Fabric: "
					+ "https://github.com/litetex-oss/otel-fabric-helper-extension");
				LOG.warn("* Disabling conflicting instrumentations with "
					+ "-Dotel.instrumentation.common.default-enabled=false");
				break;
			case "io.opentelemetry.api.GlobalOpenTelemetry$ObfuscatedOpenTelemetry":
				LOG.error("OpenTelemetry Agent extension likely crashed: Unexpected ObfuscatedOpenTelemetry in use");
				break;
			default:
				LOG.info("OK - Using {}", otelImplName);
		}
	}
	
	public OIEMetricsCreator metricsCreator()
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
			
			this.up = this.metricsCreator().createLongGauge("up");
			this.up.set(
				1,
				Attributes.builder()
					.put(
						AttributeKey.stringKey("minecraft_version"),
						SharedConstants.getCurrentVersion().name())
					.put(
						AttributeKey.stringKey("mod_loader_name"),
						"fabric")
					.put(
						AttributeKey.stringKey("mod_loader_version"),
						FabricLoader.getInstance()
							.getModContainer("fabricloader")
							.map(ModContainer::getMetadata)
							.map(ModMetadata::getVersion)
							.map(Version::getFriendlyString)
							.orElse("unknown"))
					.build());
			
			OIECustomMetricInitializer.invokeReady(this.metricsCreator());
			
			LOG.debug("Finished bootstrapping, took {}ms", System.currentTimeMillis() - start);
		});
		thread.setName("Minecraft-OpenTelemetry-Bootstrap");
		thread.setDaemon(true);
		thread.start();
	}
	
	public void onSeverStopping()
	{
		LOG.debug("Handling onServerStopping");
		
		this.registeredSamplers.forEach(MetricSampler::close);
		
		if(this.up != null)
		{
			this.up.set(0);
			this.up = null;
		}
	}
	
	private final Map<ResourceLocation, String> formatCache = new ConcurrentReferenceHashMap<>(
		16,
		ConcurrentReferenceHashMap.ReferenceType.WEAK);
	
	public String formatIdentifier(final ResourceLocation identifier)
	{
		return this.formatCache.computeIfAbsent(
			identifier,
			id -> this.config.isStripIdentifierNamespaces()
				? id.getPath()
				: id.toString());
	}
	
	public String formatWorldName(final ServerLevel world)
	{
		return this.formatIdentifier(world.dimension().location());
	}
	
	public static OIE instance()
	{
		return instance;
	}
	
	public static void setInstance(final OIE instance)
	{
		OIE.instance = instance;
	}
}
