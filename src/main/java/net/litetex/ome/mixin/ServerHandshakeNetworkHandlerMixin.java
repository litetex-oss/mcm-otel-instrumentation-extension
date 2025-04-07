package net.litetex.ome.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import net.litetex.ome.OME;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;


@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin
{
	@Unique
	private LongCounter counter;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	public void init(final MinecraftServer server, final ClientConnection connection, final CallbackInfo ci)
	{
		this.counter = OME.instance().metricsCreator().createLongCounter("handshakes");
	}
	
	@Inject(method = "onHandshake", at = @At("HEAD"))
	public void onHandShake(final HandshakeC2SPacket packet, final CallbackInfo ci)
	{
		if(this.counter != null)
		{
			this.counter.add(
				1,
				Attributes.of(
					AttributeKey.stringKey("intent"),
					packet.intendedState().name().toLowerCase()));
		}
	}
}
