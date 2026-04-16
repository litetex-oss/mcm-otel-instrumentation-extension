package net.litetex.oie.metric.measurement;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;


public class TypedObservableLongMeasurement implements TypedObservableMeasurement<Long>
{
	private final ObservableLongMeasurement measurement;
	
	public TypedObservableLongMeasurement(final ObservableLongMeasurement measurement)
	{
		this.measurement = measurement;
	}
	
	@Override
	public void record(final Long value)
	{
		this.measurement.record(value);
	}
	
	@Override
	public void record(final Long value, final Attributes attributes)
	{
		this.measurement.record(value, attributes);
	}
}
