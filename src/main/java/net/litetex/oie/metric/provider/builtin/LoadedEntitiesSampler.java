package net.litetex.oie.metric.provider.builtin;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.opentelemetry.api.common.Attributes;
import net.litetex.oie.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.oie.metric.provider.CachedMetricSampler;
import net.litetex.oie.metric.provider.PausableNullSettingMetricSampler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;


public class LoadedEntitiesSampler extends PausableNullSettingMetricSampler<Long, TypedObservableLongMeasurement>
{
	private final Map<EntityType<?>, ResourceLocation> cachedEntityTypeIds = new WeakHashMap<>();
	
	public LoadedEntitiesSampler()
	{
		super("loaded_entities", CachedMetricSampler::typedLongGauge, 0L);
	}
	
	@Override
	protected Map<Attributes, Long> getSamples()
	{
		return this.server.levels.values()
			.stream()
			.flatMap(world -> {
				final String formattedWorldName = this.oie().formatWorldName(world);
				
				return StreamSupport.stream(world.getEntities().getAll().spliterator(), false)
					.collect(Collectors.groupingBy(Entity::getType))
					.entrySet()
					.stream()
					.map(e -> Map.entry(
						Attributes.builder()
							.put("world", formattedWorldName)
							.put("group", e.getKey().getCategory().getName())
							.put(
								"type",
								this.oie().formatIdentifier(
									this.cachedEntityTypeIds.computeIfAbsent(
										e.getKey(),
										BuiltInRegistries.ENTITY_TYPE::getKey)))
							.build(),
						(long)e.getValue().size()
					));
			})
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
