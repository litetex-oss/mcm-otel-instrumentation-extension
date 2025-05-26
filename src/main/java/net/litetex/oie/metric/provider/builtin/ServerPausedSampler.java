package net.litetex.oie.metric.provider.builtin;

import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import net.litetex.oie.metric.provider.AbstractMetricSampler;
import net.minecraft.server.MinecraftServer;


public class ServerPausedSampler extends AbstractMetricSampler<ObservableLongMeasurement>
{
	private static final int TPS = 20;
	
	private int pauseWhenEmptyTicks;
	
	public ServerPausedSampler()
	{
		super("server_paused", AbstractMetricSampler::longGauge);
	}
	
	@Override
	public void register(final Meter meter, final MinecraftServer server)
	{
		super.register(meter, server);
		this.pauseWhenEmptyTicks = server.getPauseWhenEmptySeconds() * TPS;
	}
	
	@Override
	protected void sample(final ObservableLongMeasurement measurement)
	{
		measurement.record(this.server.idleTickCount >= this.pauseWhenEmptyTicks ? 1 : 0);
	}
}
