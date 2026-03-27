package net.litetex.oie.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.litetex.oie.shared.config.Configuration;


public record MetricsConfig(
	/*
	 * Stop fetching certain metrics when the server is paused and uses previously cached values. This optimization
	 * usually improves the sampling speed for affected metrics by >10x. Outcomes vary widely and depend on the work
	 * required for fetching and how often the server enters idle mode.
	 * <p>
	 * If the server is not using pauseWhenEmptySeconds (set to <= 0) this optimization will be ignored.
	 * </p>
	 */
	boolean freezeWhenServerPaused,
	/*
	 * Prefix for all metrics
	 */
	String prefix,
	/*
	 * Suffix for counters
	 */
	String counterSuffix,
	/*
	 * Only enables the specified metrics.
	 * <p>
	 * <b>ALL OTHER METRICS WILL BE DISABLED!</b>
	 * </p>
	 */
	Set<String> enabledOnly,
	/*
	 * Additionally enable the specified metrics.
	 * <p>
	 *     This is only relevant for metrics that are not enabled out of the box.
	 * </p>
	 */
	Set<String> enabledAdditionally,
	/*
	 * Disable specified metrics.
	 */
	Set<String> disabled,
	/*
	 * Determines if the player Details Samplers are enabled
	 *
	 * @see net.litetex.oie.metric.provider.builtin.player_detail.BuiltInPlayerDetailSamplerProvider
	 */
	boolean enablePlayerDetailsSamplers
)
{
	public static final Set<String> DEFAULT_DISABLED = ConcurrentHashMap.newKeySet();
	
	private static final String PREFIX = "metrics.";
	
	public MetricsConfig(final Configuration lowLevelConfig)
	{
		this(
			lowLevelConfig.getBoolean(PREFIX + "freeze-when-server-paused", true),
			lowLevelConfig.getString(PREFIX + "prefix", "minecraft_"),
			lowLevelConfig.getString(PREFIX + "counter-suffix", "_total"),
			Set.copyOf(lowLevelConfig.getStringList(PREFIX + "enabled-only")),
			Set.copyOf(lowLevelConfig.getStringList(PREFIX + "enabled-additionally")),
			Set.copyOf(lowLevelConfig.getStringList(PREFIX + "disabled")),
			lowLevelConfig.getBoolean(PREFIX + "enable-player-details-samplers", true)
		);
	}
}
