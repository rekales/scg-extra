package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.common.headshot.RotatedHeadshotBox;

import javax.annotation.Nullable;

public class OffsetRotatedHeadshotBox<T extends LivingEntity> extends RotatedHeadshotBox<T> {

    protected final float yawOffset;
    protected final boolean useHeadYaw;
    protected final double headXOffset;

    public OffsetRotatedHeadshotBox(double headSize, double headYOffset, double headZOffset, float yawOffset, boolean rotatePitch, boolean rotateYaw) {
        super(headSize, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
        this.useHeadYaw = false;
        this.headXOffset = 0.0;
    }

    public OffsetRotatedHeadshotBox(double headWidth, double headHeight, double headYOffset, float yawOffset, double headZOffset, boolean rotatePitch, boolean rotateYaw) {
        super(headWidth, headHeight, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
        this.useHeadYaw = false;
        this.headXOffset = 0.0;
    }

    public OffsetRotatedHeadshotBox(double headSize, double headYOffset, double headZOffset, float yawOffset, boolean rotatePitch, boolean rotateYaw, boolean useHeadYaw) {
        super(headSize, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
        this.useHeadYaw = useHeadYaw;
        this.headXOffset = 0.0;
    }

    public OffsetRotatedHeadshotBox(double headWidth, double headHeight, double headYOffset, float yawOffset, double headZOffset, boolean rotatePitch, boolean rotateYaw, boolean useHeadYaw) {
        super(headWidth, headHeight, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
        this.useHeadYaw = useHeadYaw;
        this.headXOffset = 0.0;
    }

    public OffsetRotatedHeadshotBox(double headWidth, double headHeight, double headYOffset, float yawOffset, double headZOffset, double headXOffset, boolean rotatePitch, boolean rotateYaw, boolean useHeadYaw) {
        super(headWidth, headHeight, headYOffset, headZOffset, rotatePitch, rotateYaw);
        this.yawOffset = yawOffset;
        this.useHeadYaw = useHeadYaw;
        this.headXOffset = headXOffset;
    }

    @Nullable
    public AABB getHeadshotBox(T entity) {
        AABB headBox = super.getHeadshotBox(entity);
        if (headBox != null) {
            if (this.useHeadYaw) {
                float pitch = this.rotatePitch ? entity.getXRot() : 0.0F;
                float baseYaw = this.rotateYaw ? entity.yBodyRot : 0.0F;
                float targetYaw = this.rotateYaw ? entity.yHeadRot + this.yawOffset : 0.0F;

                Vec3 base = Vec3.directionFromRotation(pitch, baseYaw).normalize();
                Vec3 target = Vec3.directionFromRotation(pitch, targetYaw).normalize();
                headBox = headBox.move(target.subtract(base).scale(this.headZOffset * (double) 0.0625F));

                if (this.headXOffset != 0.0) {
                    Vec3 right = Vec3.directionFromRotation(0.0F, targetYaw + 90.0F).normalize();
                    headBox = headBox.move(right.scale(this.headXOffset * 0.0625D));
                }
            } else {
                headBox = headBox.move(Vec3.directionFromRotation(this.rotatePitch ? entity.getXRot() : 0.0F, this.rotateYaw ? entity.yBodyRot + this.yawOffset : 0.0F).normalize().scale(this.headZOffset * (double) 0.0625F));
                if (this.headXOffset != 0.0) {
                    float yaw = this.rotateYaw ? entity.yBodyRot + this.yawOffset : 0.0F;
                    Vec3 right = Vec3.directionFromRotation(0.0F, yaw + 90.0F).normalize();
                    headBox = headBox.move(right.scale(this.headXOffset * 0.0625D));
                }
            }
            return headBox;
        } else {
            return null;
        }
    }
}
