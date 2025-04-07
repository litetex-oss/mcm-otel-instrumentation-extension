package net.litetex.ome.metric.provider.spark;

import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import net.litetex.ome.metric.provider.AbstractMetricSampler;


public class SparkTPSSampler extends AbstractMetricSampler<ObservableDoubleMeasurement>
{
	public SparkTPSSampler()
	{
		super("spark_tps", AbstractMetricSampler::doubleGauge);
	}
	
	@Override
	protected void sample(final ObservableDoubleMeasurement measurement)
	{
		measurement.record(SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.MINUTES_1));
	}
}
