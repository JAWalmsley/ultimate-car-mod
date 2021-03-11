package de.maxhenkel.car.blocks;

import de.maxhenkel.car.Main;
import de.maxhenkel.car.ModItemGroups;
import de.maxhenkel.car.blocks.tileentity.TileEntityCarWorkshop;
import de.maxhenkel.corelib.block.IItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCarWorkshopOutter extends BlockBase implements IItemBlock {

    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 8);

    public BlockCarWorkshopOutter() {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3F).sound(SoundType.METAL));
        setRegistryName(new ResourceLocation(Main.MODID, "car_workshop_outter"));

        this.registerDefaultState(stateDefinition.any().setValue(POSITION, 0));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_CAR)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        BlockPos tePos = findCenter(worldIn, pos);

        if (tePos == null) {
            return ActionResultType.FAIL;
        }
        return ModBlocks.CAR_WORKSHOP.use(worldIn.getBlockState(tePos), worldIn, tePos, player, handIn, hit);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        validate(worldIn, pos);
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
        validate(worldIn, pos);
    }

    private void validate(World worldIn, BlockPos pos) {
        BlockPos tePos = findCenter(worldIn, pos);

        if (tePos == null) {
            return;
        }

        TileEntity te = worldIn.getBlockEntity(tePos);

        if (!(te instanceof TileEntityCarWorkshop)) {
            return;
        }

        TileEntityCarWorkshop workshop = (TileEntityCarWorkshop) te;

        workshop.checkValidity();
    }

    private static BlockPos findCenter(World world, BlockPos pos) {
        if (isCenter(world, pos.offset(0, 0, 1))) {
            return pos.offset(0, 0, 1);
        }
        if (isCenter(world, pos.offset(1, 0, 0))) {
            return pos.offset(1, 0, 0);
        }
        if (isCenter(world, pos.offset(1, 0, 1))) {
            return pos.offset(1, 0, 1);
        }
        if (isCenter(world, pos.offset(0, 0, -1))) {
            return pos.offset(0, 0, -1);
        }
        if (isCenter(world, pos.offset(-1, 0, 0))) {
            return pos.offset(-1, 0, 0);
        }
        if (isCenter(world, pos.offset(-1, 0, -1))) {
            return pos.offset(-1, 0, -1);
        }
        if (isCenter(world, pos.offset(-1, 0, 1))) {
            return pos.offset(-1, 0, 1);
        }
        if (isCenter(world, pos.offset(1, 0, -1))) {
            return pos.offset(1, 0, -1);
        }
        return null;
    }

    private static boolean isCenter(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().equals(ModBlocks.CAR_WORKSHOP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(POSITION, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POSITION);
    }


    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
