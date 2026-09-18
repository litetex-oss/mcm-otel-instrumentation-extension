package net.litetex.oie.metric.measurement;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.ObservableMeasurement;


public interface TypedObservableMeasurement<T extends Number> extends ObservableMeasurement
{
	void record(T value);
	
	void record(T value, Attributes attributes);
}
