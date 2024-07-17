package jsco.dev.ethanolgui.client;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import jsco.dev.ethanolgui.render.EthanolHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class EthanolGUI implements ClientModInitializer {
    public static MinecraftClient CLIENT;
    public static final Logger LOGGER = LoggerFactory.getLogger("EthanolGUI");
    public static DietrichEvents2 EVENT_BUS;
    public static EthanolHud ETHANOL_HUD;
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();;
    public static KeyBinding TOGGLE_KEY;
    public static boolean AUTHENTICATED = true;

    @Override
    public void onInitializeClient() {
        CLIENT = MinecraftClient.getInstance();
        EVENT_BUS = DietrichEvents2.global();
        ETHANOL_HUD = EthanolHud.getINSTANCE();
        ETHANOL_HUD.init();
        registerKeybind();
        Runtime.getRuntime().addShutdownHook(new Thread(ETHANOL_HUD.getScheduler()::shutdown));
    }

    private void registerKeybind() {
        TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "Ethanol-Hud"));
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (TOGGLE_KEY.isPressed()) {
                TOGGLE_KEY.setPressed(false);
                ETHANOL_HUD.toggle();
            }
        });
    }
}
