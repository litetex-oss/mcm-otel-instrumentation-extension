package net.litetex.oie.metric.measurement;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;


public class TypedObservableDoubleMeasurement implements TypedObservableMeasurement<Double>
{
	private final ObservableDoubleMeasurement measurement;
	
	public TypedObservableDoubleMeasurement(final ObservableDoubleMeasurement measurement)
	{
		this.measurement = measurement;
	}
	
	@Override
	public void record(final Double value)
	{
		this.measurement.record(value);
	}
	
	@Override
	public void record(final Double value, final Attributes attributes)
	{
		this.measurement.record(value, attributes);
	}
}
