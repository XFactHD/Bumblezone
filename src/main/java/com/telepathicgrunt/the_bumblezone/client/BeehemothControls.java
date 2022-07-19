package com.telepathicgrunt.the_bumblezone.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.mobs.BeehemothEntity;
import com.telepathicgrunt.the_bumblezone.packets.BeehemothControlsPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class BeehemothControls {
    public static final KeyMapping KEY_BIND_BEEHEMOTH_DOWN = new KeyMapping(
    "key." + Bumblezone.MODID + ".beehemoth_down",
            BeehemothKeyContext.BEEHEMOTH_KEY_CONTEXT,
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_SPACE),
            "key.categories." + Bumblezone.MODID
    );

    public static final KeyMapping KEY_BIND_BEEHEMOTH_UP = new KeyMapping(
    "key." + Bumblezone.MODID + ".beehemoth_up",
            BeehemothKeyContext.BEEHEMOTH_KEY_CONTEXT,
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_SPACE),
            "key.categories." + Bumblezone.MODID
    );

    static boolean prevUp = false;
    static boolean prevDown = false;

    public static void keyInput(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        Entity vehicle = Minecraft.getInstance().player.getVehicle();
        if (vehicle == null) {
            prevUp = false;
            prevDown = false;
            return;
        }

        boolean upKeyAction = KEY_BIND_BEEHEMOTH_UP.isDown();
        boolean downKeyAction = KEY_BIND_BEEHEMOTH_DOWN.isDown();

        if (vehicle instanceof BeehemothEntity beehemothEntity && (prevUp != upKeyAction || prevDown != downKeyAction)) {
            BeehemothControlsPacket.sendToServer(
                prevUp != upKeyAction ? (upKeyAction ? 1 : 0) : 2,
                prevDown != downKeyAction ? (downKeyAction ? 1 : 0) : 2
            );

            if (prevUp != upKeyAction) {
                beehemothEntity.movingStraightUp = upKeyAction;
            }
            else {
                beehemothEntity.movingStraightDown = downKeyAction;
            }
        }

        prevUp = upKeyAction;
        prevDown = downKeyAction;
    }

    private enum BeehemothKeyContext implements IKeyConflictContext {
        BEEHEMOTH_KEY_CONTEXT {
            @Override
            public boolean isActive() {
                return Minecraft.getInstance().player != null &&
                        Minecraft.getInstance().player.getVehicle() instanceof BeehemothEntity;
            }

            @Override
            public boolean conflicts(IKeyConflictContext other) {
                return this == other;
            }
        }
    }
}
