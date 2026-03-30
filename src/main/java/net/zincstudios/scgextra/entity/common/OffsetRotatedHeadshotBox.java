package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.common.headshot.RotatedHeadshotBox;

import javax.annotation.Nullable;

public class OffsetRotatedHeadshotBox<T extends LivingEntity> extends RotatedHeadshotBox<T> {

    protected final float yawOffset;

    public OffsetRotatedHeadshotBox(double headSize, double headYOffset, double headZOffset, float yawOffset, boolean rotatePitch, boolean rotateYaw) {
        super(headSize, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
    }

    public OffsetRotatedHeadshotBox(double headWidth, double headHeight, double headYOffset, float yawOffset, double headZOffset, boolean rotatePitch, boolean rotateYaw) {
        super(headWidth, headHeight, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
    }

    @Nullable
    public AABB getHeadshotBox(T entity) {
        AABB headBox = super.getHeadshotBox(entity);
        if (headBox != null) {
            headBox = headBox.move(Vec3.directionFromRotation(this.rotatePitch ? entity.getXRot() : 0.0F, this.rotateYaw ? entity.yBodyRot + this.yawOffset : 0.0F).normalize().scale(this.headZOffset * (double)0.0625F));
            return headBox;
        } else {
            return null;
        }
    }
}
