package de.maxhenkel.car.net;

import de.maxhenkel.car.Main;
import de.maxhenkel.car.entity.car.base.EntityCarBatteryBase;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessageStarting implements Message<MessageStarting> {

    private boolean start;
    private boolean playSound;
    private UUID uuid;

    public MessageStarting() {

    }

    public MessageStarting(boolean start, boolean playSound, PlayerEntity player) {
        this.start = start;
        this.playSound = playSound;
        this.uuid = player.getUUID();
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        if (!context.getSender().getUUID().equals(uuid)) {
            Main.LOGGER.error("The UUID of the sender was not equal to the packet UUID");
            return;
        }

        Entity riding = context.getSender().getVehicle();

        if (!(riding instanceof EntityCarBatteryBase)) {
            return;
        }

        EntityCarBatteryBase car = (EntityCarBatteryBase) riding;
        if (context.getSender().equals(car.getDriver())) {
            car.setStarting(start, playSound);
        }
    }

    @Override
    public MessageStarting fromBytes(PacketBuffer buf) {
        this.start = buf.readBoolean();
        this.playSound = buf.readBoolean();

        this.uuid = buf.readUUID();

        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(start);
        buf.writeBoolean(playSound);

        buf.writeUUID(uuid);
    }

}
