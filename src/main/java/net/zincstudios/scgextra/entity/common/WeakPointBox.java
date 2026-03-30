package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import top.ribs.scguns.interfaces.IHeadshotBox;

// NOTE: Currently considered as just another headshot box, edit later to utilize damageMult and additional effects
@SuppressWarnings("unused")
public class WeakPointBox<T extends Entity> {

    private final IHeadshotBox<T> headshotBox;
    private final float damageMult;

    public WeakPointBox(IHeadshotBox<T> headshotBox, float damageMult) {
        this.headshotBox = headshotBox;
        this.damageMult = damageMult;
    }

    public WeakPointBox(IHeadshotBox<T> headshotBox) {
        this(headshotBox, 2);
    }

    public AABB getBoundingBox(T entity) {
        return this.headshotBox.getHeadshotBox(entity);
    }

    public IHeadshotBox<T> getHeadshotBox() {
        return this.headshotBox;
    }

    public float getDamageMult() {
        return this.damageMult;
    }
}
