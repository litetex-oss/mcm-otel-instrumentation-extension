package net.litetex.ome.metric.provider.builtin;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import net.litetex.ome.metric.provider.AbstractMetricSampler;
import net.minecraft.network.ClientConnection;


public class NetworkConnectionsAvgPacketsSentSampler extends AbstractMetricSampler<ObservableDoubleMeasurement>
{
	public NetworkConnectionsAvgPacketsSentSampler()
	{
		super("network_connections_packets_sent", AbstractMetricSampler::doubleGauge);
	}
	
	@Override
	protected void sample(final ObservableDoubleMeasurement measurement)
	{
		measurement.record(
			this.server.getNetworkIo().getConnections().stream()
				.mapToDouble(ClientConnection::getAveragePacketsSent)
				.sum(),
			Attributes.of(AttributeKey.stringKey("variant"), "average"));
	}
}
