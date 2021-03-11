package de.maxhenkel.car.blocks.tileentity;

import de.maxhenkel.car.Main;
import de.maxhenkel.corelib.energy.EnergyUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityDynamo extends TileEntityBase implements IEnergyStorage, ITickableTileEntity {

    private int storedEnergy;
    public final int maxStorage;
    public final int generation;

    public TileEntityDynamo() {
        super(Main.DYNAMO_TILE_ENTITY_TYPE);
        this.maxStorage = Main.SERVER_CONFIG.dynamoEnergyStorage.get();
        this.generation = Main.SERVER_CONFIG.dynamoEnergyGeneration.get();
        this.storedEnergy = 0;

    }

    @Override
    public void tick() {
        for (Direction side : Direction.values()) {
            IEnergyStorage storage = EnergyUtils.getEnergyStorageOffset(level, worldPosition, side);

            if (storage == null) {
                continue;
            }

            EnergyUtils.pushEnergy(this, storage, storedEnergy);
        }
    }

    public void addEnergy(int energy) {
        storedEnergy += energy;
        if (storedEnergy > maxStorage) {
            storedEnergy = maxStorage;
        }
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("stored_energy", storedEnergy);
        return super.save(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        storedEnergy = compound.getInt("stored_energy");
        super.load(blockState, compound);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int i = Math.min(maxExtract, storedEnergy);

        if (!simulate) {
            storedEnergy -= i;
            setChanged();
        }

        return i;
    }

    @Override
    public int getEnergyStored() {
        return storedEnergy;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxStorage;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public ITextComponent getTranslatedName() {
        return new TranslationTextComponent("block.car.dynamo");
    }

    @Override
    public IIntArray getFields() {
        return new IntArray(0);
    }

}
