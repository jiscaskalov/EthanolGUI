package jsco.dev.ethanolgui.mixin;

import me.x150.renderer.render.Renderer2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Renderer2d.class)
public interface Renderer2dAccessor {
    @Invoker("_populateRC")
    static void invoke$populateRC(float x, float y, float rad, int index) {
        throw new AssertionError();
    }

    @Accessor("roundedCache")
    static float[][] getRoundedCache() {
        throw new AssertionError();
    }
}
