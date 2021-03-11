package de.maxhenkel.car.blocks;

import de.maxhenkel.car.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSplitTankTop extends BlockBase {

    public BlockSplitTankTop() {
        super(Properties.of(Material.METAL).strength(3F).sound(SoundType.STONE));
        setRegistryName(new ResourceLocation(Main.MODID, "split_tank_top"));
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return ModBlocks.SPLIT_TANK.use(worldIn.getBlockState(pos.below()), worldIn, pos.below(), player, handIn, hit);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    private static final VoxelShape SHAPE = Block.box(0D, -16D, 0D, 16D, 8D, 16D);

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onRemove(state, worldIn, pos, newState, isMoving);

            BlockState stateDown = worldIn.getBlockState(pos.below());
            if (stateDown != null && stateDown.getBlock().equals(ModBlocks.SPLIT_TANK)) {
                worldIn.destroyBlock(pos.below(), false);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        BlockState stateDown = world.getBlockState(pos.below());
        if (stateDown != null && stateDown.getBlock() != null && stateDown.getBlock().equals(ModBlocks.SPLIT_TANK) && !player.abilities.instabuild) {
            ModBlocks.SPLIT_TANK.playerDestroy(world, player, pos.below(), world.getBlockState(pos.below()), world.getBlockEntity(pos.below()), player.getMainHandItem());
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        BlockState stateDown = world.getBlockState(pos.below());
        if (stateDown.getBlock().equals(ModBlocks.SPLIT_TANK)) {
            return ModBlocks.SPLIT_TANK.getPickBlock(stateDown, target, world, pos.below(), player);
        }
        return super.getPickBlock(stateDown, target, world, pos, player);
    }

}
