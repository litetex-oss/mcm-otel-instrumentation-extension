package net.litetex.ome.metric.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriFunction;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import net.litetex.ome.metric.measurement.TypedObservableDoubleMeasurement;
import net.litetex.ome.metric.measurement.TypedObservableLongMeasurement;
import net.litetex.ome.metric.measurement.TypedObservableMeasurement;


public abstract class CachedMetricSampler<T extends Number, M extends TypedObservableMeasurement<T>>
	extends AbstractMetricSampler<M>
{
	protected Map<Attributes, T> cachedSamples = new HashMap<>();
	protected boolean attributeAlwaysNull;
	
	protected CachedMetricSampler(
		final String name,
		final TriFunction<Meter, String, Consumer<M>, AutoCloseable> buildWithCallback)
	{
		super(name, buildWithCallback);
	}
	
	protected static AutoCloseable typedDoubleGauge(
		final Meter meter,
		final String name,
		final Consumer<TypedObservableDoubleMeasurement> c)
	{
		return meter.gaugeBuilder(name).buildWithCallback(m ->
			c.accept(new TypedObservableDoubleMeasurement(m)));
	}
	
	protected static AutoCloseable typedLongGauge(
		final Meter meter,
		final String name,
		final Consumer<TypedObservableLongMeasurement> c)
	{
		return meter.gaugeBuilder(name)
			.ofLongs()
			.buildWithCallback(m ->
				c.accept(new TypedObservableLongMeasurement(m)));
	}
	
	protected static AutoCloseable typedLongCounter(
		final Meter meter,
		final String name,
		final Consumer<TypedObservableLongMeasurement> c)
	{
		return meter.counterBuilder(name).buildWithCallback(m ->
			c.accept(new TypedObservableLongMeasurement(m)));
	}
	
	protected abstract boolean shouldSample();
	
	@Override
	protected void sample(final M measurement)
	{
		if(!this.shouldSample())
		{
			this.cachedSamples.forEach((attr, val) -> this.record(measurement, val, attr));
			return;
		}
		
		final Map<Attributes, T> samples = this.getSamples();
		samples.forEach((attr, val) -> this.record(measurement, val, attr));
		this.onSampled(samples, measurement);
	}
	
	protected void onSampled(final Map<Attributes, T> samples, final M measurement)
	{
		this.cachedSamples = samples;
	}
	
	protected void record(final M measurement, final T value, final Attributes attr)
	{
		if(this.attributeAlwaysNull)
		{
			measurement.record(value);
			return;
		}
		
		measurement.record(value, attr);
	}
	
	protected abstract Map<Attributes, T> getSamples();
	
	@Override
	public void close()
	{
		super.close();
		this.cachedSamples = null;
	}
}
