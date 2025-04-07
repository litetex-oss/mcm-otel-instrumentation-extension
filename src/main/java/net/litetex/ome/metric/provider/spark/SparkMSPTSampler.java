package net.litetex.ome.metric.provider.spark;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import net.litetex.ome.metric.provider.AbstractMetricSampler;


public class SparkMSPTSampler extends AbstractMetricSampler<ObservableDoubleMeasurement>
{
	public SparkMSPTSampler()
	{
		super("spark_mspt", AbstractMetricSampler::doubleGauge);
	}
	
	@Override
	protected void sample(final ObservableDoubleMeasurement measurement)
	{
		final DoubleAverageInfo averageInfo =
			SparkProvider.get().mspt().poll(StatisticWindow.MillisPerTick.MINUTES_1);
		
		final AttributeKey<String> attributeKey = AttributeKey.stringKey("variant");
		measurement.record(averageInfo.min(), Attributes.of(attributeKey, "min"));
		measurement.record(averageInfo.mean(), Attributes.of(attributeKey, "mean"));
		measurement.record(averageInfo.median(), Attributes.of(attributeKey, "median"));
		measurement.record(averageInfo.max(), Attributes.of(attributeKey, "max"));
	}
}
