package net.litetex.ome.metric.provider.builtin;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.opentelemetry.api.common.Attributes;
import net.litetex.ome.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.ome.metric.provider.CachedMetricSampler;
import net.litetex.ome.metric.provider.PausableNullSettingMetricSampler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;


public class LoadedEntitiesSampler extends PausableNullSettingMetricSampler<Long, TypedObservableLongMeasurement>
{
	private final Map<EntityType<?>, Identifier> cachedEntityTypeIds = new WeakHashMap<>();
	
	public LoadedEntitiesSampler()
	{
		super("loaded_entities", CachedMetricSampler::typedLongGauge, 0L);
	}
	
	@Override
	protected Map<Attributes, Long> getSamples()
	{
		return this.server.worlds.values()
			.stream()
			.flatMap(world -> {
				final String formattedWorldName = this.ome().formatWorldName(world);
				
				return StreamSupport.stream(world.getEntityLookup().iterate().spliterator(), false)
					.collect(Collectors.groupingBy(Entity::getType))
					.entrySet()
					.stream()
					.map(e -> Map.entry(
						Attributes.builder()
							.put("world", formattedWorldName)
							.put("group", e.getKey().getSpawnGroup().getName())
							.put(
								"type",
								this.ome().formatIdentifier(
									this.cachedEntityTypeIds.computeIfAbsent(
										e.getKey(),
										Registries.ENTITY_TYPE::getId)))
							.build(),
						(long)e.getValue().size()
					));
			})
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
