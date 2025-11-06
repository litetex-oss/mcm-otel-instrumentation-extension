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
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStatusTask;
import net.minecraft.world.level.chunk.status.ChunkStep;


@Mixin(ChunkStep.class)
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
		final ChunkDependencies generationDependencies,
		final ChunkDependencies generationDependencies2,
		final int i,
		final ChunkStatusTask generationTask,
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
		method = "apply",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/JvmProfiler;onChunkGenerate"
			+ "(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/resources/ResourceKey;Ljava/lang/String;)"
			+ "Lnet/minecraft/util/profiling/jfr/callback/ProfiledDuration;")
	)
	public ProfiledDuration redirectFlightProfilerStartChunkGenerationProfiling(
		final JvmProfiler profiler,
		final ChunkPos pos,
		final ResourceKey<Level> worldRegistryKey,
		final String id)
	{
		if(timesCalled == null && duration == null)
		{
			return profiler.onChunkGenerate(pos, worldRegistryKey, id);
		}
		
		final Attributes attributes = Attributes.builder()
			.put(CommonAttributeKeys.WORLD, OIE.instance().formatIdentifier(worldRegistryKey.location()))
			.put(STATUS, id)
			.put(STATUS_INDEX, (long)this.targetStatus.getIndex())
			.build();
		if(timesCalled != null)
		{
			timesCalled.add(
				1,
				attributes);
		}
		
		final ProfiledDuration originalFinishable =
			profiler.onChunkGenerate(pos, worldRegistryKey, id);
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
