package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.MCHTTPAPI;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinInGameHud {
    private static final Pattern COMPILE = Pattern.compile("§([a-f]|k|l|m|n|o|r|[0-9])");

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String timeStamp = DateTimeFormatter
                .ofPattern("[HH:mm:ss]")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        MCHTTPAPI.INSTANCE.getChatMessages().add(timeStamp + " " + COMPILE.matcher(packet.getMessage().asString()).replaceAll(""));
    }
}
