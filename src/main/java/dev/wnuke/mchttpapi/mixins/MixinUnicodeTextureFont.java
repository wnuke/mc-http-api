package dev.wnuke.mchttpapi.mixins;

import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnicodeTextureFont.class)
public class MixinUnicodeTextureFont {
    @Inject(method = "getGlyphImage", at = @At("HEAD"), cancellable = true)
    private void getGlyphImage(Identifier glyphId, CallbackInfoReturnable<NativeImage> cir) {
        cir.setReturnValue(null);
        cir.cancel();
    }
    @Inject(method = "getGlyph", at = @At("HEAD"), cancellable = true)
    private void getGlyph(int i, CallbackInfoReturnable<RenderableGlyph> cir) {
        cir.setReturnValue(null);
        cir.cancel();
    }
}
