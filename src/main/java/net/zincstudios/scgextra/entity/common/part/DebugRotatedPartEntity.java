package net.zincstudios.scgextra.entity.common.part;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * For instant feedback with code hot swapping. Not meant for released
 * @param <T>
 */
@Deprecated
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DebugRotatedPartEntity <T extends LivingEntity> extends PartEntity<T> {

    private EntityDimensions size;

    public DebugRotatedPartEntity(T parent) {
        super(parent);
    }

    public void update(Vec3 offset, float width, float height) {
        this.size = EntityDimensions.fixed(width, height);
        this.refreshDimensions();


        this.setOldPosAndRot();
        this.setPos(this.getParent().position().add(offset.yRot(-this.getParent().yBodyRot * Mth.DEG_TO_RAD)));
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }


//    PartEntity<?>[] partEntities = this.getParts();
//        ((DebugRotatedPartEntity<?>)partEntities[0]).update(new Vec3(1.5, 1.4, -0.5), 20/16f, 0.5f);
//        ((DebugRotatedPartEntity<?>)partEntities[1]).update(new Vec3(-1.5, 1.4, -0.5), 20/16f, 0.5f);
}
