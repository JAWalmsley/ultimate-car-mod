package de.maxhenkel.car.events;

import de.maxhenkel.car.Main;
import de.maxhenkel.car.entity.car.base.EntityCarBatteryBase;
import de.maxhenkel.car.net.MessageCenterCar;
import de.maxhenkel.car.net.MessageStarting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

@OnlyIn(Dist.CLIENT)
public class KeyEvents {

    private boolean wasStartPressed;
    private boolean wasGuiPressed;
    private boolean wasHornPressed;
    private boolean wasCenterPressed;

    public KeyEvents() {

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();

        PlayerEntity player = minecraft.player;

        if (player == null) {
            return;
        }

        Entity riding = player.getVehicle();

        if (!(riding instanceof EntityCarBatteryBase)) {
            return;
        }

        EntityCarBatteryBase car = (EntityCarBatteryBase) riding;

        if (player.equals(car.getDriver())) {
            car.updateControls(Main.FORWARD_KEY.isDown(), Main.BACK_KEY.isDown(), Main.LEFT_KEY.isDown(), Main.RIGHT_KEY.isDown(), player);

            if (Main.START_KEY.isDown()) {
                if (!wasStartPressed) {
                    Main.SIMPLE_CHANNEL.sendToServer(new MessageStarting(true, false, player));
                    wasStartPressed = true;
                }
            } else {
                if (wasStartPressed) {
                    Main.SIMPLE_CHANNEL.sendToServer(new MessageStarting(false, true, player));
                }
                wasStartPressed = false;
            }

            if (Main.HORN_KEY.isDown()) {
                if (!wasHornPressed) {
                    car.onHornPressed(player);
                    wasHornPressed = true;
                }
            } else {
                wasHornPressed = false;
            }

            if (Main.CENTER_KEY.isDown()) {
                if (!wasCenterPressed) {
                    Main.SIMPLE_CHANNEL.sendToServer(new MessageCenterCar(player));
                    player.displayClientMessage(new TranslationTextComponent("message.center_car"), true);
                    wasCenterPressed = true;
                }
            } else {
                wasCenterPressed = false;
            }
        }

        if (Main.CAR_GUI_KEY.isDown()) {
            if (!wasGuiPressed) {
                car.openCarGUI(player);
                wasGuiPressed = true;
            }
        } else {
            wasGuiPressed = false;
        }

    }

}
