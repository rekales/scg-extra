package com.daragetsu.scgextra.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import top.ribs.scguns.block.MineUnitBlock;
import top.ribs.scguns.blockentity.MineUnitBlockEntity;
import top.ribs.scguns.init.ModBlocks;
import top.ribs.scguns.init.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DeployedMineEntity extends ThrowableProjectile {


    public DeployedMineEntity(EntityType<? extends DeployedMineEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (this.level().isClientSide) return;

        BlockPos pos = result.getBlockPos().relative(result.getDirection());
        BlockState stateToPlace = ModBlocks.MINE_UNIT.get().defaultBlockState().setValue(MineUnitBlock.PRIMED, true);

        if (this.level().getBlockState(pos).canBeReplaced()) {
            this.level().setBlock(pos, stateToPlace, 3);
            if (this.level().getBlockEntity(pos) instanceof MineUnitBlockEntity be) {
                be.setGrenade(
                        new ItemStack(ModItems.GRENADE.get()),
                        this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null
                );
                be.setPrimed(true);
            }
        }

        this.discard();
    }

    @Override
    protected void defineSynchedData() {
    }
}