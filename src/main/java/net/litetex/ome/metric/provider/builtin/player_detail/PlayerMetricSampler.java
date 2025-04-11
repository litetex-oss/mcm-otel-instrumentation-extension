package net.litetex.ome.metric.provider.builtin.player_detail;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.function.TriFunction;

import com.mojang.authlib.GameProfile;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import net.litetex.ome.metric.CommonAttributeKeys;
import net.litetex.ome.metric.measurement.TypedObservableMeasurement;
import net.litetex.ome.metric.provider.PausableMetricSampler;
import net.minecraft.server.network.ServerPlayerEntity;


public abstract class PlayerMetricSampler<T extends Number, M extends TypedObservableMeasurement<T>>
	extends PausableMetricSampler<T, M>
{
	protected final Map<ServerPlayerEntity, Attributes> playerAttributeCache = new WeakHashMap<>();
	
	protected PlayerMetricSampler(
		final String name,
		final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback)
	{
		super(name, buildWithCallback);
	}
	
	protected abstract T getValueForPlayer(ServerPlayerEntity player);
	
	@Override
	protected Map<Attributes, T> getSamples()
	{
		return this.server.getPlayerManager().getPlayerList()
			.stream()
			.collect(Collectors.toMap(
				p -> this.playerAttributeCache.computeIfAbsent(
					p,
					player -> {
						final GameProfile profile = player.getGameProfile();
						
						return Attributes.builder()
							.put(CommonAttributeKeys.NAME, profile.getName())
							.put(CommonAttributeKeys.UUID, profile.getId().toString())
							.build();
					}),
				this::getValueForPlayer));
	}
}
