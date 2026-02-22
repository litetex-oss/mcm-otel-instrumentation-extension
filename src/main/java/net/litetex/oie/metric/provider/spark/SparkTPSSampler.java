package net.litetex.oie.metric.provider.spark;

import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.api.common.Attributes;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import net.litetex.oie.metric.measurement.TypedObservableDoubleMeasurement;
import net.litetex.oie.metric.provider.CachedMetricSampler;
import net.litetex.oie.metric.provider.PausableMetricSampler;


public class SparkTPSSampler extends PausableMetricSampler<Double, TypedObservableDoubleMeasurement>
{
	public SparkTPSSampler()
	{
		super("spark_tps", CachedMetricSampler::typedDoubleGauge);
		this.attributeAlwaysNull = true;
	}
	
	@Override
	protected Map<Attributes, Double> getSamples()
	{
		final Map<Attributes, Double> map = new HashMap<>();
		map.put(null, SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.MINUTES_1));
		return map;
	}
}
