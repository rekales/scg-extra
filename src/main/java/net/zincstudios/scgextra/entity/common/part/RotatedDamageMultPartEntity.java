package net.zincstudios.scgextra.entity.common.part;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RotatedDamageMultPartEntity<T extends LivingEntity> extends RotatedSegmentPartEntity<T> {

    private final float damageMult;

    public RotatedDamageMultPartEntity(T parent, float damageMult, Vec3 offset, float width, float height) {
        super(parent, offset, width, height);
        this.damageMult = damageMult;
    }

    public RotatedDamageMultPartEntity(T parent, float damageMult, Vec3 offset, float width, float height, boolean collision) {
        super(parent, offset, width, height, collision);
        this.damageMult = damageMult;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.getParent().hurt(source, amount * this.damageMult);
    }
}
