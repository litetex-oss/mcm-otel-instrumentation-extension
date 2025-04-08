package net.litetex.ome.config;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class MetricsConfig
{
	public static final Set<String> DEFAULT_DISABLED = Set.of();
	
	// Stop fetching certain metrics when the server is paused and uses previously cached values
	// This optimization usually improves the sampling speed for affected metrics by >10x
	// Outcomes vary widely and depend on the work required for fetching
	private boolean freezeWhenServerPaused = true;
	
	private String prefix = "minecraft_";
	private Set<String> enabledOnly; // Can be null!
	private Set<String> enabledAdditionally = new HashSet<>();
	private Set<String> disabled = new HashSet<>();
	
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
		this.prefix = Objects.requireNonNull(prefix);
	}
	
	public Set<String> getEnabledOnly()
	{
		return this.enabledOnly;
	}
	
	public void setEnabledOnly(final Set<String> enabledOnly)
	{
		this.enabledOnly = enabledOnly;
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
}
