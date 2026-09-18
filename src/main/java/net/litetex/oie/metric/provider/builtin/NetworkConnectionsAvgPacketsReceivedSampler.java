package net.litetex.oie.metric.provider.builtin;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import net.litetex.oie.metric.CommonAttributeKeys;
import net.litetex.oie.metric.provider.AbstractMetricSampler;
import net.minecraft.network.Connection;


public class NetworkConnectionsAvgPacketsReceivedSampler extends AbstractMetricSampler<ObservableDoubleMeasurement>
{
	public NetworkConnectionsAvgPacketsReceivedSampler()
	{
		super("network_connections_packets_received", AbstractMetricSampler::doubleGauge);
	}
	
	@Override
	protected void sample(final ObservableDoubleMeasurement measurement)
	{
		measurement.record(
			this.server.getConnection().getConnections().stream()
				.mapToDouble(Connection::getAverageReceivedPackets)
				.sum(),
			Attributes.of(CommonAttributeKeys.VARIANT, "average"));
	}
}
