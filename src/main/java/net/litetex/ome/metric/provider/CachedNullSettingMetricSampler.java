package net.litetex.ome.metric.provider;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriFunction;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import net.litetex.ome.metric.measurement.TypedObservableMeasurement;


public abstract class CachedNullSettingMetricSampler<T extends Number, M extends TypedObservableMeasurement<T>>
	extends CachedMetricSampler<T, M>
{
	protected CachedNullSettingMetricSampler(
		final String name,
		final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback)
	{
		super(name, buildWithCallback);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onSampled(final Map<Attributes, T> samples, final M measurement)
	{
		this.cachedSamples.keySet()
			.stream()
			.filter(attr -> !samples.containsKey(attr))
			.forEach(attr -> this.record(measurement, (T)(Object)0, attr));
		
		this.cachedSamples = samples;
	}
}
