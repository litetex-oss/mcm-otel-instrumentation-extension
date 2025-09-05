package net.litetex.oie.metric;

import io.opentelemetry.api.common.AttributeKey;


public final class CommonAttributeKeys
{
	public static final AttributeKey<String> NAME = AttributeKey.stringKey("name");
	public static final AttributeKey<String> UUID = AttributeKey.stringKey("uuid");
	public static final AttributeKey<String> VARIANT = AttributeKey.stringKey("variant");
	public static final AttributeKey<String> WORLD = AttributeKey.stringKey("world");
	
	private CommonAttributeKeys()
	{
	}
}
