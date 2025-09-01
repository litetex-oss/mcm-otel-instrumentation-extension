package net.litetex.oie.metric.provider.builtin;

import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import net.litetex.oie.metric.provider.AbstractMetricSampler;


public class AverageTickTimeSampler extends AbstractMetricSampler<ObservableDoubleMeasurement>
{
	public AverageTickTimeSampler()
	{
		super("average_tick_time", AbstractMetricSampler::doubleGauge);
	}
	
	@Override
	protected void sample(final ObservableDoubleMeasurement measurement)
	{
		measurement.record(this.server.getAverageTickTime());
	}
}
