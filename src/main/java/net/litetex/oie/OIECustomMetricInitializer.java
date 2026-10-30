package net.litetex.oie;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public final class OIECustomMetricInitializer
{
	private static final Object LOCK = new Object();
	private static Map<Object, Consumer<OIEMetricsCreator>> executeWhenReady = new HashMap<>();
	
	public static void executeWhenReady(final Consumer<OIEMetricsCreator> callback)
	{
		executeWhenReady(new Object(), callback);
	}
	
	public static void executeWhenReady(
		final Object uniquenessIdentifier,
		final Consumer<OIEMetricsCreator> callback)
	{
		synchronized(LOCK)
		{
			if(executeWhenReady != null)
			{
				executeWhenReady.putIfAbsent(uniquenessIdentifier, callback);
				return;
			}
		}
		callback.accept(OIE.instance().metricsCreator());
	}
	
	static void invokeReady(final OIEMetricsCreator creator)
	{
		synchronized(LOCK)
		{
			executeWhenReady.values().forEach(c -> c.accept(creator));
			executeWhenReady = null;
		}
	}
	
	private OIECustomMetricInitializer()
	{
	}
}
