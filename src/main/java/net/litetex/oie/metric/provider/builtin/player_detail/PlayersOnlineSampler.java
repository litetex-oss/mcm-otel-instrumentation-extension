package net.litetex.oie.metric.provider.builtin.player_detail;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;

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
				player -> this.playerAttributeCache.computeIfAbsent(
					new AttributeCacheKey(player),
					key -> Attributes.builder()
						.put(CommonAttributeKeys.NAME, key.profile().name())
						.put(CommonAttributeKeys.UUID, key.profile().id().toString())
						.put(CommonAttributeKeys.WORLD, this.oie().formatWorldName(key.world()))
						.put(GAME_MODE, key.gameMode().name())
						.build()
				),
				player -> (long)1));
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
		GameProfile profile,
		ServerLevel world,
		GameType gameMode
	)
	{
		public AttributeCacheKey(final ServerPlayer player)
		{
			this(player.getGameProfile(), player.level(), player.gameMode.getGameModeForPlayer());
		}
	}
}
