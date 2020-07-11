package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Mixin(net.minecraft.client.gui.IngameGui.class)
public class MixinIngameGui {
    @Inject(method = "addChatMessage", at = @At("HEAD"))
    public void addMessage(ChatType chatTypeIn, ITextComponent message, CallbackInfo ci) {
        String timeStamp = DateTimeFormatter
                .ofPattern("[HH:mm:ss]")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        HeadlessAPI.chatMessages.add(timeStamp + " " + message.getString());
    }
    @Inject(method = "renderGameOverlay", at = @At("HEAD"), cancellable = true)
    public void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        ci.cancel();
    }
}
