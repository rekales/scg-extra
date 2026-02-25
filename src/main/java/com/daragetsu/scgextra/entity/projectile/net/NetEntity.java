package com.daragetsu.scgextra.entity.projectile.net;

import java.util.List;

import com.daragetsu.scgextra.entity.ModEntities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class NetEntity extends AbstractArrow{
    public NetEntity(EntityType<? extends NetEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public NetEntity(LivingEntity pShooter, Level pLevel) {
        super(ModEntities.NET.get(), pShooter, pLevel);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if(entityHitResult.getEntity() instanceof Player p){
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        super.onHitEntity(entityHitResult);
    }

    //arrow hit range is too low, so added this
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        AABB box = new AABB(
            this.getX()-2,
            this.getY()-2,
            this.getZ()-2,
            this.getX()+2,
            this.getY()+2,
            this.getZ()+2
        );
        List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
        if(!players.isEmpty()){
            for(Player p : players){
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
            }
            this.remove(RemovalReason.DISCARDED);
        }else{
            super.onHitBlock(blockHitResult);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        //leaving at string item
        return new ItemStack(Items.STRING);
    }
}
