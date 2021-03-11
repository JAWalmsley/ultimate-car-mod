package de.maxhenkel.car.blocks.tileentity;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

import de.maxhenkel.car.Main;
import de.maxhenkel.corelib.fluid.FluidUtils;
import de.maxhenkel.tools.BlockPosList;
import de.maxhenkel.car.blocks.BlockFluidExtractor;
import de.maxhenkel.car.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityFluidExtractor extends TileEntityBase implements ITickableTileEntity {

    private IFluidHandler extractHandler;

    private final int drainSpeed;

    private ItemStack filter;

    public TileEntityFluidExtractor() {
        super(Main.FLUID_EXTRACTOR_TILE_ENTITY_TYPE);
        this.drainSpeed = Main.SERVER_CONFIG.fluidExtractorDrainSpeed.get();
        filter = null;
    }

    @Nullable
    public Fluid getFilterFluid() {
        if (filter == null) {
            return null;
        }

        FluidStack stack = FluidUtil.getFluidContained(filter).orElse(null);

        if (stack == null || stack.getAmount() <= 0) {
            return null;
        }

        return stack.getFluid();
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            return;
        }
        updateExtractHandler();

        if (extractHandler == null) {
            return;
        }

        FluidStack drainSimulated;
        if (getFilterFluid() == null) {
            drainSimulated = extractHandler.drain(drainSpeed, IFluidHandler.FluidAction.SIMULATE);
        } else {
            drainSimulated = extractHandler.drain(new FluidStack(getFilterFluid(), drainSpeed), IFluidHandler.FluidAction.SIMULATE);
        }

        if (drainSimulated.getAmount() <= 0) {
            return;
        }

        List<IFluidHandler> handlers = new LinkedList<>();

        getConnectedHandlers(handlers, new BlockPosList(), worldPosition);

        List<IFluidHandler> fillHandlers = new LinkedList<>();

        for (IFluidHandler handler : handlers) {
            int amount = handler.fill(drainSimulated, IFluidHandler.FluidAction.SIMULATE);
            if (amount > 0) {
                fillHandlers.add(handler);
            }
        }

        if (fillHandlers.isEmpty()) {
            return;
        }

        for (IFluidHandler handler : fillHandlers) {
            if (getFilterFluid() == null) {
                FluidUtils.tryFluidTransfer(handler, extractHandler, drainSpeed, true);
            } else {
                FluidUtils.tryFluidTransfer(handler, extractHandler, drainSpeed, true, getFilterFluid());
            }
        }
    }

    public void updateExtractHandler() {
        BlockState state = level.getBlockState(worldPosition);

        if (!state.getBlock().equals(ModBlocks.FLUID_EXTRACTOR)) {
            extractHandler = null;
            return;
        }

        Direction side = state.getValue(BlockFluidExtractor.FACING);

        extractHandler = FluidUtils.getFluidHandlerOffset(level, worldPosition, side);
    }

    public void getConnectedHandlers(List<IFluidHandler> handlers, BlockPosList positions, BlockPos pos) {
        for (Direction side : Direction.values()) {
            BlockPos p = pos.relative(side);

            if (positions.contains(p)) {
                continue;
            }

            BlockState state = level.getBlockState(p);

            if (state.getBlock().equals(ModBlocks.FLUID_PIPE) || state.getBlock().equals(ModBlocks.FLUID_EXTRACTOR)) {
                positions.add(p);
                getConnectedHandlers(handlers, positions, p);
                continue;
            }

            IFluidHandler handler = FluidUtils.getFluidHandlerOffset(level, pos, side);

            if (handler == null || handler.equals(extractHandler)) {
                continue;
            }

            handlers.add(handler);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {

        if (filter != null) {
            CompoundNBT tag = new CompoundNBT();
            filter.save(tag);
            compound.put("filter", tag);
        }

        return super.save(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        if (compound.contains("filter")) {
            CompoundNBT tag = compound.getCompound("filter");
            filter = ItemStack.of(tag);
        } else {
            filter = null;
        }
        super.load(blockState, compound);
    }

    @Override
    public ITextComponent getTranslatedName() {
        return new TranslationTextComponent("block.car.fluid_extractor");
    }

    public ItemStack getFilter() {
        if (filter == null) {
            return null;
        }
        return filter.copy();
    }

    public void setFilter(ItemStack filter) {
        if (filter == null) {
            this.filter = null;
            setChanged();
            synchronize();
            return;
        }
        this.filter = filter.copy();
        setChanged();
        synchronize();
    }

    @Override
    public IIntArray getFields() {
        return new IntArray(0);
    }

}
