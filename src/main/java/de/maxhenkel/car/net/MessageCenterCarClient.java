package de.maxhenkel.car.net;

import de.maxhenkel.car.entity.car.base.EntityCarBase;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessageCenterCarClient implements Message<MessageCenterCarClient> {

    private UUID uuid;

    public MessageCenterCarClient() {

    }

    public MessageCenterCarClient(PlayerEntity player) {
        this.uuid = player.getUUID();
    }

    public MessageCenterCarClient(UUID uuid) {
        this.uuid = uuid;
    }

    @OnlyIn(Dist.CLIENT)
    public void centerClient() {
        PlayerEntity player = Minecraft.getInstance().player;
        PlayerEntity ridingPlayer = player.level.getPlayerByUUID(uuid);
        Entity riding = ridingPlayer.getVehicle();

        if (!(riding instanceof EntityCarBase)) {
            return;
        }

        EntityCarBase car = (EntityCarBase) riding;
        if (ridingPlayer.equals(car.getDriver())) {
            car.centerCar();
        }
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {
        centerClient();
    }

    @Override
    public MessageCenterCarClient fromBytes(PacketBuffer buf) {
        this.uuid = buf.readUUID();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUUID(uuid);
    }

}
