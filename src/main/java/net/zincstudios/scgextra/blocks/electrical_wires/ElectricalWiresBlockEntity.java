package net.zincstudios.scgextra.blocks.electrical_wires;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.blocks.ModBlockEntities;
import top.ribs.scguns.init.ModBlocks;

public class ElectricalWiresBlockEntity extends BlockEntity{
    public ElectricalWiresBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ELECTRICAL_WIRES.get(), pos, blockState);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, ElectricalWiresBlockEntity blockEntity) {
        if(!level.isClientSide()){
            if(level.players().size()>0){
                BlockEntity en = getPowerSource(level, pos, state, blockEntity);
                if(en!=null){
                    if(level.players().size()>0){
                        level.players().get(0).sendSystemMessage(Component.literal("FOUND SOURCE, ENERGY: "+getEnergy(en)));
                    }
                }else{
                    if(level.players().size()>0){
                        level.players().get(0).sendSystemMessage(Component.literal("NO SOURCE"));
                    }
                }
            }
        }
    }
    //just for testing, TODO: make a better system
    @Nullable
    private static BlockEntity getPowerSource(Level level, BlockPos pos, BlockState state, ElectricalWiresBlockEntity blockEntity){
        BlockPos eastPos = pos.east();
        BlockState eastState = level.getBlockState(eastPos);
        BlockPos westPos = pos.west();
        BlockState westState = level.getBlockState(westPos);
        BlockPos northPos = pos.north();
        BlockState northState = level.getBlockState(northPos);
        BlockPos southPos = pos.south();
        BlockState southState = level.getBlockState(southPos);
        BlockEntity be = null;
        if(eastState.is(ModBlocks.POLAR_GENERATOR.get())){
            if(getEnergy(level.getBlockEntity(eastPos))>0){
                be = level.getBlockEntity(eastPos);
            }
        }
        if(westState.is(ModBlocks.POLAR_GENERATOR.get())){
            if(getEnergy(level.getBlockEntity(westPos))>0){
                be = level.getBlockEntity(westPos);
            }
        }
        if(northState.is(ModBlocks.POLAR_GENERATOR.get())){
            if(getEnergy(level.getBlockEntity(northPos))>0){
                be = level.getBlockEntity(northPos);
            }
        }
        if(southState.is(ModBlocks.POLAR_GENERATOR.get())){
            if(getEnergy(level.getBlockEntity(southPos))>0){
                be = level.getBlockEntity(southPos);
            }
        }
        return be;
    }
    private static int getEnergy(BlockEntity be){
        CompoundTag tag = be.saveWithoutMetadata();
        return tag.getInt("Energy");
    }
}