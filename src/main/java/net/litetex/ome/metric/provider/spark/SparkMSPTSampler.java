package net.litetex.ome.metric.provider.spark;

import java.util.Map;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import net.litetex.ome.metric.CommonAttributeKeys;
import net.litetex.ome.metric.measurement.TypedObservableDoubleMeasurement;
import net.litetex.ome.metric.provider.CachedMetricSampler;
import net.litetex.ome.metric.provider.PauseableMetricSampler;


public class SparkMSPTSampler extends PauseableMetricSampler<Double, TypedObservableDoubleMeasurement>
{
	public SparkMSPTSampler()
	{
		super("spark_mspt", CachedMetricSampler::typedDoubleGauge);
	}
	
	@Override
	protected Map<Attributes, Double> getSamples()
	{
		final DoubleAverageInfo averageInfo =
			SparkProvider.get().mspt().poll(StatisticWindow.MillisPerTick.MINUTES_1);
		
		final AttributeKey<String> attributeKey = CommonAttributeKeys.VARIANT;
		return Map.ofEntries(
			Map.entry(Attributes.of(attributeKey, "min"), averageInfo.min()),
			Map.entry(Attributes.of(attributeKey, "mean"), averageInfo.mean()),
			Map.entry(Attributes.of(attributeKey, "median"), averageInfo.median()),
			Map.entry(Attributes.of(attributeKey, "max"), averageInfo.max())
		);
	}
}
