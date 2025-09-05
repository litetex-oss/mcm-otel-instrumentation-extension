package net.litetex.oie.config;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class MetricsConfig
{
	public static final Set<String> DEFAULT_DISABLED = ConcurrentHashMap.newKeySet();
	
	/**
	 * Stop fetching certain metrics when the server is paused and uses previously cached values. This optimization
	 * usually improves the sampling speed for affected metrics by >10x. Outcomes vary widely and depend on the work
	 * required for fetching and how often the server enters idle mode.
	 * <p>
	 * If the server is not using pauseWhenEmptySeconds (set to <= 0) this optimization will be ignored.
	 * </p>
	 */
	private boolean freezeWhenServerPaused = true;
	
	/**
	 * Prefix for all metrics
	 */
	private String prefix = "minecraft_";
	
	/**
	 * Suffix for counters
	 */
	private String counterSuffix = "_total";
	
	/**
	 * Only enables the specified metrics.
	 * <p>
	 * <b>ALL OTHER METRICS WILL BE DISABLED!</b>
	 * </p>
	 */
	private Set<String> enabledOnly = new HashSet<>();
	/**
	 * Additionally enable the specified metrics.
	 * <p>
	 *     This is only relevant for metrics that are not enabled out of the box.
	 * </p>
	 */
	private Set<String> enabledAdditionally = new HashSet<>();
	/**
	 * Disable specified metrics.
	 */
	private Set<String> disabled = new HashSet<>();
	
	/**
	 * Determines if the player Details Samplers are enabled
	 *
	 * @see net.litetex.oie.metric.provider.builtin.player_detail.BuiltInPlayerDetailSamplerProvider
	 */
	private boolean enablePlayerDetailsSamplers = true;
	
	public void setFreezeWhenServerPaused(final boolean freezeWhenServerPaused)
	{
		this.freezeWhenServerPaused = freezeWhenServerPaused;
	}
	
	public boolean isFreezeWhenServerPaused()
	{
		return this.freezeWhenServerPaused;
	}
	
	public String getPrefix()
	{
		return this.prefix;
	}
	
	public void setPrefix(final String prefix)
	{
		this.prefix = Objects.requireNonNullElse(prefix, "");
	}
	
	public String getCounterSuffix()
	{
		return this.counterSuffix;
	}
	
	public void setCounterSuffix(final String counterSuffix)
	{
		this.counterSuffix = Objects.requireNonNullElse(counterSuffix, "");
	}
	
	public Set<String> getEnabledOnly()
	{
		return this.enabledOnly;
	}
	
	public void setEnabledOnly(final Set<String> enabledOnly)
	{
		this.enabledOnly = Objects.requireNonNullElseGet(this.enabledAdditionally, HashSet::new);
	}
	
	public Set<String> getEnabledAdditionally()
	{
		return this.enabledAdditionally;
	}
	
	public void setEnabledAdditionally(final Set<String> enabledAdditionally)
	{
		this.enabledAdditionally = Objects.requireNonNullElseGet(enabledAdditionally, HashSet::new);
	}
	
	public Set<String> getDisabled()
	{
		return this.disabled;
	}
	
	public void setDisabled(final Set<String> disabled)
	{
		this.disabled = Objects.requireNonNullElseGet(disabled, HashSet::new);
	}
	
	public boolean isEnablePlayerDetailsSamplers()
	{
		return this.enablePlayerDetailsSamplers;
	}
	
	public void setEnablePlayerDetailsSamplers(final boolean enablePlayerDetailsSamplers)
	{
		this.enablePlayerDetailsSamplers = enablePlayerDetailsSamplers;
	}
}
