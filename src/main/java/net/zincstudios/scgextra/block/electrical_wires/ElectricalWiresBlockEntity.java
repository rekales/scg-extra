package net.zincstudios.scgextra.block.electrical_wires;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.zincstudios.scgextra.block.ModBlockEntities;

public class ElectricalWiresBlockEntity extends BlockEntity{
    private final ContainerData data;
    private ElectricalWiresBlockEntity powersource = null;
    private BlockPos powersourcePos = null;
    private int lvl = 0;
    private final EnergyStorage energyStorage = new EnergyStorage(2000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                setChanged();
                sync();
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                setChanged();
                sync();
            }
            return extracted;
        }
        @Override
        public boolean canReceive() {
            if(this.getEnergyStored() < 2000){
                return true;
            }
            return false;
        };
    };
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
    public ElectricalWiresBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ELECTRICAL_WIRES.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored();
                    case 1 -> energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.putInt("lvl", this.lvl);
        if(this.getPowerSource()!=null){
            tag.putInt("PowerSourceX", this.getPowerSourcePos().getX());
            tag.putInt("PowerSourceY", this.getPowerSourcePos().getY());
            tag.putInt("PowerSourceZ", this.getPowerSourcePos().getZ());
        }
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("Energy"));
        this.lvl = tag.getInt("lvl");
        if(
            tag.contains("PowerSourceX") &&
            tag.contains("PowerSourceY") &&
            tag.contains("PowerSourceZ")
        ){
            this.setPowerSourcePos(new BlockPos(tag.getInt("PowerSourceX"), tag.getInt("PowerSourceY"), tag.getInt("PowerSourceZ")));
        }
    }
    private void sync() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, ElectricalWiresBlockEntity blockEntity) {
        boolean wasLit = state.getValue(ElectricalWiresBlock.LIT);
        boolean isLit = false;
        if (!level.isClientSide) {
            if(blockEntity.getPowerSource() == null && blockEntity.getPowerSourcePos()!=null){
                BlockEntity be = level.getBlockEntity(blockEntity.getPowerSourcePos());
                if(be != null && be instanceof ElectricalWiresBlockEntity ewbe){
                    blockEntity.setPowerSource(ewbe);
                }
            }
            if (blockEntity.hasEnoughEnergy(200)) {
                isLit = true;
            }

            if (wasLit != isLit) {
                level.setBlock(pos, state.setValue(ElectricalWiresBlock.LIT, isLit), 3);
            }
            for (Direction direction : Direction.values()) {
                BlockEntity adjacentEntity = level.getBlockEntity(pos.relative(direction));
                if (adjacentEntity != null) {
                    if(!(adjacentEntity instanceof ElectricalWiresBlockEntity ewbe)){
                        continue;
                    }
                    if(!(blockEntity.getPowerSource() == ewbe)){
                        ewbe.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                            if (handler.canReceive() && blockEntity.getLvl() < 15) {
                                int extracted = blockEntity.energyStorage.extractEnergy(200, true);
                                int accepted = handler.receiveEnergy(extracted, false);
                                blockEntity.energyStorage.extractEnergy(accepted, false);
                                if(ewbe.getPowerSource() == null){
                                    ewbe.setLvl(blockEntity.getLvl()+1);
                                }
                                if(ewbe.getLvl()!=blockEntity.getLvl()+1){
                                    ewbe.setLvl(blockEntity.getLvl()+1);
                                }
                                ewbe.setPowerSource(blockEntity);
                                ewbe.setPowerSourcePos(blockEntity.getBlockPos());
                                blockEntity.setChanged();
                                blockEntity.sync();
                            }
                        });
                    }
                }
            }
        }
    }
    private boolean hasEnoughEnergy(int amount) {
        return energyStorage.getEnergyStored() >= amount;
    }

    public void consumeEnergy(int amount) {
        energyStorage.extractEnergy(amount, false);
        setChanged();
        sync();
    }

    public void addEnergy(int amount) {
        energyStorage.receiveEnergy(amount, false);
        setChanged();
        sync();
    }
    public int getEnergy(){
        return energyStorage.getEnergyStored();
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energy.invalidate();
    }
    public void setPowerSource(ElectricalWiresBlockEntity ewbe){
        if(this.powersource!=ewbe){
            this.powersource = ewbe;
        }
    }
    public ElectricalWiresBlockEntity getPowerSource(){
        return this.powersource;
    }
    public void setPowerSourcePos(BlockPos pos){
        this.powersourcePos = pos;
    }
    public BlockPos getPowerSourcePos(){
        return this.powersourcePos;
    }
    public void setLvl(int l){
        this.lvl = l;
    }
    public int getLvl(){return this.lvl;}
}