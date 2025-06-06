package net.litetex.oie.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import net.litetex.oie.OIE;
import net.litetex.oie.OIECustomMetricInitializer;
import net.litetex.oie.metric.CommonAttributeKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.function.Finishable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.GenerationDependencies;
import net.minecraft.world.chunk.GenerationTask;


@Mixin(ChunkGenerationStep.class)
public abstract class ChunkGenerationStepMixin
{
	@Unique
	private static final AttributeKey<String> STATUS = AttributeKey.stringKey("status");
	
	@Unique
	private static final AttributeKey<Long> STATUS_INDEX = AttributeKey.longKey("status_index");
	
	@Unique
	private static LongCounter timesCalled;
	
	@Unique
	private static LongCounter duration;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	public void init(
		final ChunkStatus chunkStatus,
		final GenerationDependencies generationDependencies,
		final GenerationDependencies generationDependencies2,
		final int i,
		final GenerationTask generationTask,
		final CallbackInfo ci)
	{
		OIECustomMetricInitializer.executeWhenReady(
			ChunkGenerationStepMixin.class,
			creator -> {
				timesCalled = creator.createLongCounter("chunk_generation_step_count");
				duration = creator.createLongCounter("chunk_generation_step_duration_ms");
			});
	}
	
	@Redirect(
		method = "run",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/FlightProfiler;"
			+ "startChunkGenerationProfiling(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/registry/RegistryKey;"
			+ "Ljava/lang/String;)Lnet/minecraft/util/function/Finishable;")
	)
	public Finishable redirectFlightProfilerStartChunkGenerationProfiling(
		final FlightProfiler profiler,
		final ChunkPos pos,
		final RegistryKey<World> worldRegistryKey,
		final String id)
	{
		if(timesCalled == null && duration == null)
		{
			return profiler.startChunkGenerationProfiling(pos, worldRegistryKey, id);
		}
		
		final Attributes attributes = Attributes.builder()
			.put(CommonAttributeKeys.WORLD, OIE.instance().formatIdentifier(worldRegistryKey.getValue()))
			.put(STATUS, id)
			.put(STATUS_INDEX, (long)this.targetStatus.getIndex())
			.build();
		if(timesCalled != null)
		{
			timesCalled.add(
				1,
				attributes);
		}
		
		final Finishable originalFinishable =
			profiler.startChunkGenerationProfiling(pos, worldRegistryKey, id);
		if(duration == null)
		{
			return originalFinishable;
		}
		
		final long startMs = System.currentTimeMillis();
		
		return success -> {
			if(originalFinishable != null)
			{
				originalFinishable.finish(success);
			}
			duration.add(System.currentTimeMillis() - startMs, attributes);
		};
	}
	
	@Shadow
	@Final
	ChunkStatus targetStatus;
}
