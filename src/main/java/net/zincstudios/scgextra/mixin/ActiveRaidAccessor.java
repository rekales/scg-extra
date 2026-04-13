package net.zincstudios.scgextra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.ribs.scguns.entity.raid.ActiveRaid;

@Mixin(value = ActiveRaid.class, remap = false)
public interface ActiveRaidAccessor {

    @Accessor("spawnTimer")
    int scgextra$getSpawnTimer();

    @Accessor("spawnTimer")
    void scgextra$setSpawnTimer(int value);
}
