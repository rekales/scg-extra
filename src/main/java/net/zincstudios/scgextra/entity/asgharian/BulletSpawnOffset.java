package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.world.phys.Vec3;

public interface BulletSpawnOffset {

    Vec3 getBulletSpawnOffset(int gunIndex);

    default Vec3 getBulletSpawnOffset() {
        return this.getBulletSpawnOffset(0);
    }
}
