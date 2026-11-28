package net.litetex.oie.shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public final class JSONSerializer
{
	public static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.create();
	
	private JSONSerializer()
	{
	}
}
