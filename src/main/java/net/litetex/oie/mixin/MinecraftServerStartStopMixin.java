package net.litetex.oie.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.litetex.oie.OIE;
import net.minecraft.server.MinecraftServer;


// See also:
// https://github.com/FabricMC/fabric/blob/1.21.5/fabric-lifecycle-events-v1/src/main/java/net/fabricmc/fabric/mixin/event/lifecycle/MinecraftServerMixin.java
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerStartStopMixin
{
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()"
			+ "Lnet/minecraft/network/protocol/status/ServerStatus;",
		ordinal = 0),
		method = "runServer")
	@SuppressWarnings("javabugs:S6320")
	private void afterSetupServer(final CallbackInfo info)
	{
		if(OIE.instance() != null)
		{
			OIE.instance().onServerStarted((MinecraftServer)(Object)this);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "stopServer")
	private void beforeShutdownServer(final CallbackInfo info)
	{
		if(OIE.instance() != null)
		{
			OIE.instance().onSeverStopping();
		}
	}
}
