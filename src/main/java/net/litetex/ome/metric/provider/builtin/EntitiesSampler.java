package net.litetex.ome.metric.provider.builtin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import net.litetex.ome.metric.provider.AbstractMetricSampler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


public class EntitiesSampler extends AbstractMetricSampler<ObservableLongMeasurement>
{
	private final Map<EntityType<?>, Identifier> cachedEntityTypeIds = new WeakHashMap<>();
	private final Map<World, Set<EntityType<?>>> previousWorldEntityTypes = new WeakHashMap<>();
	
	public EntitiesSampler()
	{
		super("entities", AbstractMetricSampler::longGauge);
	}
	
	@Override
	protected void sample(final ObservableLongMeasurement measurement)
	{
		for(final ServerWorld world : this.server.getWorlds())
		{
			final String formattedWorldName = this.ome().formatWorldName(world);
			
			final Map<EntityType<?>, ? extends List<?>> currentEntities =
				StreamSupport.stream(world.getEntityLookup().iterate().spliterator(), false)
					.collect(Collectors.groupingBy(Entity::getType));
			currentEntities
				.forEach((type, entities) -> this.measure(measurement, type, entities.size(), formattedWorldName));
			
			// To prevent metrics from showing an incorrect value before they vanish set the value once to 0
			// when it's no longer present
			final Set<EntityType<?>> currentEntityTypes = currentEntities.keySet();
			final Set<EntityType<?>> prevEntityTypes = this.previousWorldEntityTypes.get(world);
			if(prevEntityTypes != null)
			{
				prevEntityTypes.stream()
					.filter(type -> !currentEntityTypes.contains(type))
					.forEach(type -> this.measure(measurement, type, 0, formattedWorldName));
			}
			this.previousWorldEntityTypes.put(world, currentEntityTypes);
		}
	}
	
	private void measure(
		final ObservableLongMeasurement measurement,
		final EntityType<?> type,
		final long size,
		final String formattedWorldName)
	{
		measurement.record(
			size,
			Attributes.builder()
				.put("world", formattedWorldName)
				.put("group", type.getSpawnGroup().getName())
				.put(
					"type",
					this.ome().formatIdentifier(
						this.cachedEntityTypeIds.computeIfAbsent(
							type,
							Registries.ENTITY_TYPE::getId)))
				.build());
	}
}
