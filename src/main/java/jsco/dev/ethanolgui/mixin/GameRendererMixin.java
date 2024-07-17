package jsco.dev.ethanolgui.mixin;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.render.EthanolHud;
import jsco.dev.ethanolgui.render.blur.Blur;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyVariable(method = "renderBlur", at = @At("STORE"), ordinal = 1)
    private float blur$modifyRadius(float radius) {
        if (EthanolGUI.ETHANOL_HUD.canRender())
            Blur.updateProgress(Blur.screenHasBlur);
        return radius * Blur.progress;
    }
}
