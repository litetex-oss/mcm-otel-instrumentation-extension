package net.litetex.oie.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import net.litetex.oie.OIECustomMetricInitializer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;


@Mixin(ServerHandshakePacketListenerImpl.class)
public abstract class ServerHandshakeNetworkHandlerMixin
{
	@Unique
	private static final AttributeKey<String> INTENT = AttributeKey.stringKey("intent");
	
	@Unique
	private LongCounter counter;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	public void init(final MinecraftServer server, final Connection connection, final CallbackInfo ci)
	{
		OIECustomMetricInitializer.executeWhenReady(creator ->
			this.counter = creator.createLongCounter("handshakes"));
	}
	
	@Inject(method = "handleIntention", at = @At("HEAD"))
	public void onHandShake(final ClientIntentionPacket packet, final CallbackInfo ci)
	{
		if(this.counter != null)
		{
			this.counter.add(
				1,
				Attributes.of(
					INTENT,
					packet.intention().name().toLowerCase()));
		}
	}
}
