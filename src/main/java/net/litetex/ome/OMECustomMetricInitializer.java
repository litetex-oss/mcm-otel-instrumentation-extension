package net.litetex.ome;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public final class OMECustomMetricInitializer
{
	private static final Object LOCK = new Object();
	private static Map<Object, Consumer<OMEMetricsCreator>> executeWhenReady = new HashMap<>();
	
	public static void executeWhenReady(final Consumer<OMEMetricsCreator> callback)
	{
		executeWhenReady(new Object(), callback);
	}
	
	public static void executeWhenReady(
		final Object uniquenessIdentifier,
		final Consumer<OMEMetricsCreator> callback)
	{
		synchronized(LOCK)
		{
			if(executeWhenReady != null)
			{
				executeWhenReady.putIfAbsent(uniquenessIdentifier, callback);
				return;
			}
		}
		callback.accept(OME.instance().metricsCreator());
	}
	
	static void invokeReady(final OMEMetricsCreator creator)
	{
		synchronized(LOCK)
		{
			executeWhenReady.values().forEach(c -> c.accept(creator));
			executeWhenReady = null;
		}
	}
	
	private OMECustomMetricInitializer()
	{
	}
}
