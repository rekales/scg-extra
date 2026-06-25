package net.zincstudios.scgextra.entity.common.part;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.WeakPointPart;

public class RotatedWeakPointPartEntity <T extends LivingEntity> extends RotatedSegmentPartEntity<T> implements WeakPointPart {

    public RotatedWeakPointPartEntity(T parent, Vec3 offset, float width, float height) {
        super(parent, offset, width, height);
    }
}
