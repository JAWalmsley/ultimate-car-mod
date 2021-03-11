package de.maxhenkel.car.blocks.tileentity;

import de.maxhenkel.car.Main;
import de.maxhenkel.car.net.MessageSyncTileEntity;
import de.maxhenkel.corelib.entity.EntityUtils;
import de.maxhenkel.corelib.net.NetUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public abstract class TileEntityBase extends TileEntity implements INameable {

    private ITextComponent name;
    private CompoundNBT compoundLast;

    public TileEntityBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void synchronize() {
        if (!level.isClientSide && level instanceof ServerWorld) {
            CompoundNBT last = save(new CompoundNBT());
            if (compoundLast == null || !compoundLast.equals(last)) {
                ServerWorld serverWorld = (ServerWorld) level;

                MessageSyncTileEntity msg = new MessageSyncTileEntity(worldPosition, last);
                EntityUtils.forEachPlayerAround(serverWorld, worldPosition, 128D, playerEntity -> NetUtils.sendTo(Main.SIMPLE_CHANNEL, playerEntity, msg));
                compoundLast = last;
            }
        }
    }

    public void synchronize(int ticks) {
        if (level.getGameTime() % ticks == 0) {
            synchronize();
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    public abstract ITextComponent getTranslatedName();

    public void setCustomName(ITextComponent name) {
        this.name = name;
    }

    @Override
    public ITextComponent getName() {
        return name != null ? name : getTranslatedName();
    }

    @Override
    @Nullable
    public ITextComponent getCustomName() {
        return name;
    }

    public abstract IIntArray getFields();

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (name != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(name));
        }
        return super.save(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        if (compound.contains("CustomName")) {
            name = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
        super.load(blockState, compound);
    }
}
