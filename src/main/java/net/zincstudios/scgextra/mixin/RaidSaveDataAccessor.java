package net.zincstudios.scgextra.mixin;

import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.ribs.scguns.entity.raid.RaidSaveData;

@Mixin(value = RaidSaveData.class, remap = false)
public interface RaidSaveDataAccessor {

    @Accessor("activeRaidData")
    Map<UUID, ?> scgextra$getActiveRaidDataMap();

    @Accessor("scheduledRaids")
    Map<ResourceLocation, ?> scgextra$getScheduledRaidsMap();

    @Accessor("lastRaidDayByDimension")
    Map<ResourceLocation, Long> scgextra$getLastRaidDayByDimensionMap();
}
