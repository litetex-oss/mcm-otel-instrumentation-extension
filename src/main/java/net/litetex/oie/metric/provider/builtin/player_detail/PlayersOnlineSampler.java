package net.litetex.oie.metric.provider.builtin.player_detail;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import net.litetex.oie.metric.CommonAttributeKeys;
import net.litetex.oie.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.oie.metric.provider.CachedMetricSampler;
import net.litetex.oie.metric.provider.PausableNullSettingMetricSampler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;


public class PlayersOnlineSampler extends PausableNullSettingMetricSampler<Long, TypedObservableLongMeasurement>
{
	private static final AttributeKey<String> GAME_MODE = AttributeKey.stringKey("game_mode");
	
	protected BiMap<AttributeCacheKey, Attributes> playerAttributeCache = HashBiMap.create();
	
	public PlayersOnlineSampler()
	{
		super("players_online", CachedMetricSampler::typedLongGauge, 0L);
	}
	
	@Override
	protected Map<Attributes, Long> getSamples()
	{
		return this.server.getPlayerList().getPlayers()
			.stream()
			.collect(Collectors.toMap(
				player -> forceComputeIfAbsent(
					this.playerAttributeCache,
					new AttributeCacheKey(player),
					key -> Attributes.builder()
						.put(CommonAttributeKeys.NAME, key.profileName())
						.put(CommonAttributeKeys.UUID, key.profileId().toString())
						.put(CommonAttributeKeys.WORLD, this.oie().formatWorldName(key.world()))
						.put(GAME_MODE, key.gameMode().name())
						.build()
				),
				_ -> (long)1));
	}
	
	@Override
	protected void handlePreviousRemoved(final TypedObservableLongMeasurement measurement, final Attributes attr)
	{
		super.handlePreviousRemoved(measurement, attr);
		this.playerAttributeCache.inverse().remove(attr); // Prevent memory leak
	}
	
	@Override
	public void close()
	{
		super.close();
		this.playerAttributeCache = null;
	}
	
	protected record AttributeCacheKey(
		String profileName,
		UUID profileId,
		ServerLevel world,
		GameType gameMode
	)
	{
		AttributeCacheKey(final ServerPlayer player)
		{
			this(
				// GameProfile is not stable due to changing signatures between logins!
				// -> Use only needed and stable fields in cache
				player.getGameProfile().name(),
				player.getGameProfile().id(),
				player.level(),
				player.gameMode.getGameModeForPlayer());
		}
	}
	
	// Identical to computeIfAbsent but forcePuts the value so that it does not crash when already present
	private static <K, V> V forceComputeIfAbsent(
		final BiMap<K, V> map,
		final K key,
		final Function<? super K, ? extends V> mappingFunction)
	{
		Objects.requireNonNull(mappingFunction);
		final V v;
		if((v = map.get(key)) == null)
		{
			final V newValue;
			if((newValue = mappingFunction.apply(key)) != null)
			{
				map.forcePut(key, newValue);
				return newValue;
			}
		}
		return v;
	}
	
}
