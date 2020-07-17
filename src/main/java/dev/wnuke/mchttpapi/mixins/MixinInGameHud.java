package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "addChatMessage", at = @At("HEAD"))
    public void addChatMessage(MessageType type, Text text, UUID senderUuid, CallbackInfo ci) {
        String timeStamp = DateTimeFormatter
                .ofPattern("[HH:mm:ss]")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        HeadlessAPI.chatMessages.add(timeStamp + " " + text.getString());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (HeadlessAPI.disableRender) {
            ci.cancel();
        }
    }
}
