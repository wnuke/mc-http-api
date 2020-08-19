package dev.wnuke.mchttpapi.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import dev.wnuke.mchttpapi.HeadlessAPI;
import dev.wnuke.mchttpapi.utils.RunBooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {
    private static final float TPS = 20.0F;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(TPS, 0L);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(boolean tick, CallbackInfo ci) {
        MinecraftClient.getInstance().skipGameRender = true;
        if (null != HeadlessAPI.compatLayer) {
            HeadlessAPI.compatLayer.respawn();
            ClientConnection connection = HeadlessAPI.compatLayer.connection;
            if (null != connection) {
                if (connection.isOpen()) {
                    connection.tick();
                } else {
                    connection.handleDisconnection();
                }
            }
        }
        if (tick) {
            int k = renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
            MinecraftClient.getInstance().runTasks(new RunBooleanSupplier());
            for (int j = 0; j < Math.min(10, k); ++j) {
                MinecraftClient.getInstance().tick();
            }
        }
    }

    @Inject(method = "getSession", at = @At("HEAD"), cancellable = true)
    public void getSession(CallbackInfoReturnable<? super Session> cir) {
        cir.setReturnValue(HeadlessAPI.compatLayer.session);
    }

    @Inject(method = "getSessionProperties", at = @At("HEAD"), cancellable = true)
    public void sessionProperties(CallbackInfoReturnable<? super PropertyMap> cir) {
        if (HeadlessAPI.compatLayer.sessionProperties.isEmpty()) {
            GameProfile gameProfile = MinecraftClient.getInstance().getSessionService().fillProfileProperties(HeadlessAPI.compatLayer.session.getProfile(), false);
            HeadlessAPI.compatLayer.sessionProperties.putAll(gameProfile.getProperties());
        }
        cir.setReturnValue(HeadlessAPI.compatLayer.sessionProperties);
    }

    @Inject(method = "isModded", at = @At("HEAD"), cancellable = true)
    public void setNotModded(CallbackInfoReturnable<? super Boolean> cir) {
        cir.setReturnValue(false);
    }
}
