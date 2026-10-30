package net.litetex.oie.shared.config;

import java.util.List;

import net.fabricmc.loader.api.FabricLoader;


public interface Configuration
{
	static Configuration standard()
	{
		return Configuration.combining(
			RuntimeConfiguration.environmentVariables("OIE"),
			RuntimeConfiguration.systemProperties("oie"),
			new FileConfiguration(FabricLoader.getInstance().getConfigDir().resolve("oie.json")));
	}
	
	static Configuration combining(final Configuration... configurations)
	{
		return new CombinedConfiguration(configurations);
	}
	
	void load();
	
	void save();
	
	String getString(String path, String def);
	
	boolean getBoolean(String path, boolean def);
	
	int getInteger(String path, int def);
	
	List<String> getStringList(String path);
	
	void setString(String path, String value);
	
	void setBoolean(String path, boolean value);
	
	void setInteger(String path, int value);
	
	void setStringList(String path, List<String> value);
	
	boolean contains(String path);
	
	void remove(String path);
}
