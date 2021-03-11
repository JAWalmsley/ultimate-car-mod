package de.maxhenkel.car.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundLoopTileentity extends TickableSound {

    protected World world;
    protected BlockPos pos;

    public SoundLoopTileentity(SoundEvent event, SoundCategory category, TileEntity tileEntity) {
        super(event, category);
        this.world = tileEntity.getLevel();
        this.pos = tileEntity.getBlockPos();
        this.looping = true;
        this.delay = 0;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.volume = 0.15F;
        this.pitch = 1.0F;
    }

    @Override
    public void tick() {
        if (isStopped()) {
            return;
        }

        TileEntity tileEntity = getTileEntity();

        if (tileEntity == null || tileEntity.getLevel() == null) {
            stop();
        }

        if (tileEntity.getLevel().isClientSide) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null || !player.isAlive()) {
                stop();
                return;
            }
        }

        if (!(tileEntity instanceof ISoundLoopable)) {
            stop();
            return;
        }

        ISoundLoopable loop = (ISoundLoopable) tileEntity;

        if (!loop.shouldSoundBePlayed()) {
            stop();
            return;
        }
    }

    public TileEntity getTileEntity() {
        return world.getBlockEntity(pos);
    }

    public interface ISoundLoopable {
        boolean shouldSoundBePlayed();

        void play();
    }

}