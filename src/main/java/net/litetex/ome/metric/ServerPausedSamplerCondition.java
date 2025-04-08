package net.litetex.ome.metric;

import net.minecraft.server.MinecraftServer;


public class ServerPausedSamplerCondition
{
	private static final int TPS = 20;
	
	private boolean wasRunning = true;
	private final int pauseWhenEmptyTicks;
	
	public ServerPausedSamplerCondition(final MinecraftServer server)
	{
		this.pauseWhenEmptyTicks = server.getPauseWhenEmptySeconds() * TPS;
	}
	
	public boolean shouldSample(final MinecraftServer server)
	{
		final boolean isRunning = server.idleTickCount < this.pauseWhenEmptyTicks;
		final boolean wasRunningBefore = this.wasRunning;
		this.wasRunning = isRunning;
		return isRunning || wasRunningBefore;
	}
}
