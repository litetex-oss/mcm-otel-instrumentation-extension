package net.litetex.oie.fabric;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.litetex.oie.OIE;
import net.litetex.oie.config.Config;


public class FabricOIE implements ModInitializer
{
	private static final Logger LOG = LoggerFactory.getLogger(FabricOIE.class);
	
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public void onInitialize()
	{
		final Config config = this.loadConfig();
		OIE.setInstance(new OIE(
			config
		));
		
		LOG.debug("Initialized");
	}
	
	private Path configFilePath()
	{
		return FabricLoader.getInstance().getConfigDir().resolve("oie.json");
	}
	
	private Config loadConfig()
	{
		final Path configFilePath = this.configFilePath();
		if(Files.exists(configFilePath))
		{
			try
			{
				return this.gson.fromJson(Files.readString(configFilePath), Config.class);
			}
			catch(final Exception ex)
			{
				LOG.warn("Failed to read config file", ex);
			}
		}
		
		final Config defaultConfig = Config.createDefault();
		this.saveConfig(defaultConfig);
		return defaultConfig;
	}
	
	private void saveConfig(final Config config)
	{
		try
		{
			Files.writeString(
				this.configFilePath(),
				this.gson.toJson(config));
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException("Failed to save config", ioe);
		}
	}
}
