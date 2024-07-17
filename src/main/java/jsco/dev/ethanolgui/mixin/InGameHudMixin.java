package jsco.dev.ethanolgui.mixin;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.event.HudRenderListener;
import jsco.dev.ethanolgui.render.blur.Blur;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "HEAD"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EthanolGUI.EVENT_BUS.call(HudRenderListener.HudRenderEvent.ID, new HudRenderListener.HudRenderEvent(context, context.getScaledWindowWidth(), context.getScaledWindowHeight(), tickCounter.getTickDelta(true)));
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (EthanolGUI.ETHANOL_HUD.isEnabled()) ci.cancel();
    }
}
