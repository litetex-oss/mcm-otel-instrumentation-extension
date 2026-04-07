package net.litetex.oie.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.litetex.oie.OIE;
import net.litetex.oie.shared.config.Configuration;
import net.litetex.oie.shared.config.FileConfiguration;
import net.litetex.oie.shared.config.RuntimeConfiguration;


public class FabricOIE implements ModInitializer
{
	private static final Logger LOG = LoggerFactory.getLogger(FabricOIE.class);
	
	@Override
	public void onInitialize()
	{
		OIE.setInstance(new OIE(
			Configuration.combining(
				RuntimeConfiguration.environmentVariables("OIE"),
				RuntimeConfiguration.systemProperties("oie"),
				new FileConfiguration(FabricLoader.getInstance().getConfigDir().resolve("oie.json")))
		));
		
		LOG.debug("Initialized");
	}
}
