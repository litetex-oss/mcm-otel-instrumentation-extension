package net.litetex.ome.metric.provider.builtin;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import net.litetex.ome.metric.provider.AbstractMetricSampler;


public class NetworkConnectionsSampler extends AbstractMetricSampler<ObservableLongMeasurement>
{
	public NetworkConnectionsSampler()
	{
		super("network_connections", AbstractMetricSampler::longGauge);
	}
	
	@Override
	protected void sample(final ObservableLongMeasurement measurement)
	{
		measurement.record(
			this.server.getNetworkIo().getConnections().size(),
			Attributes.of(AttributeKey.stringKey("variant"), "current"));
	}
}
