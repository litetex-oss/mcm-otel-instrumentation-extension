package net.litetex.oie.metric;

import net.minecraft.server.MinecraftServer;


public class ServerPausedSamplerCondition
{
	private static final int TPS = 20;
	
	private boolean wasRunning = true;
	private final int pauseWhenEmptyTicks;
	
	protected ServerPausedSamplerCondition(final int pauseWhenEmptyTicks)
	{
		this.pauseWhenEmptyTicks = pauseWhenEmptyTicks;
	}
	
	public boolean shouldSample(final MinecraftServer server)
	{
		final boolean isRunning = server.emptyTicks < this.pauseWhenEmptyTicks;
		final boolean wasRunningBefore = this.wasRunning;
		this.wasRunning = isRunning;
		return isRunning || wasRunningBefore;
	}
	
	public static ServerPausedSamplerCondition create(final MinecraftServer server)
	{
		if(server.pauseWhenEmptySeconds() <= 0)
		{
			return null;
		}
		
		return new ServerPausedSamplerCondition(server.pauseWhenEmptySeconds() * TPS);
	}
}
