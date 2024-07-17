package jsco.dev.ethanolgui.render;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.client.Input;
import jsco.dev.ethanolgui.client.Pair;
import jsco.dev.ethanolgui.event.MouseButtonListener;
import jsco.dev.ethanolgui.event.MouseScrollListener;
import jsco.dev.ethanolgui.event.HudRenderListener;
import jsco.dev.ethanolgui.mixin.MouseAccessor;
import jsco.dev.ethanolgui.render.blur.Blur;
import jsco.dev.ethanolgui.render.blur.RainbowColor;
import jsco.dev.ethanolgui.render.elements.BaseElement;
import jsco.dev.ethanolgui.render.elements.HudElement;
import jsco.dev.ethanolgui.render.elements.TickableElement;
import jsco.dev.ethanolgui.render.lines.LineRenderer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import static jsco.dev.ethanolgui.client.EthanolGUI.CLIENT;
import static jsco.dev.ethanolgui.client.EthanolGUI.EVENT_BUS;
import static jsco.dev.ethanolgui.render.SharedColors.BACKGROUND;

@Getter public class EthanolHud implements HudRenderListener, MouseButtonListener, MouseScrollListener {
    private boolean enabled = false;
    private boolean render = false;
    @Getter private int alpha = 0;
    private int startFade = 0;
    private int endFade = 0;
    private double animProgress = 0;
    @Setter private double frameTime = 0;

    private final List<HudElement> children = new ArrayList<>();
    private final Map<HudElement, Pair<Integer, Integer>> cachedChildren = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final LineRenderer lineRenderer = new LineRenderer();

    private HudElement selected;
    private HudElement lastAdded;
    private final boolean renderChildrenFirst = false;

    @Getter private static final EthanolHud INSTANCE = new EthanolHud();

    public EthanolHud() {
        EVENT_BUS.subscribe(HudRenderEvent.ID, this);
        EVENT_BUS.subscribe(MouseButtonEvent.ID, this);
        EVENT_BUS.subscribe(MouseScrollEvent.ID, this);
        addCustomElements();
    }

    public void init() {
        selected = null;
        lastAdded = null;

        Blur.start();
        System.out.println("Initialized Ethanol Hud!");
    }

    private void update() {
        double delta = render ? 0.02 : -0.02;
        animProgress = MathHelper.clamp(animProgress + delta, 0, 1);

        recursiveTick(getChildren());

        lineRenderer.tickPhysics();
        RainbowColor.render();
    }

    private void recursiveTick(List<HudElement> elements) {
        for (HudElement element : elements) {
            if (element instanceof TickableElement t) {
                if (element.canRender()) {
                    t.onTick();
                }
            }

            recursiveTick(element.getChildren());
        }
    }

    private void addCustomElements() {
        addElement(new BaseElement(600, 200, 450, 250, 5, BACKGROUND, Color.WHITE, 6, 4));
//        addElement(new PlayerContextElement(100, 100, 200, 200, 5, BACKGROUND, 6));
    }

    private void addElement(HudElement element) {
        cachedChildren.put(element, Pair.of(element.getX(), element.getY()));
        addChild(element);
    }

    @Override
    public void onHudRender(HudRenderEvent event) {
        if (!EthanolGUI.AUTHENTICATED) {
            return;
        }

        if (!canRender()) return;

        if (!renderChildrenFirst)
            renderElements(event);

        renderChildren(event);

        if (renderChildrenFirst)
            renderElements(event);
    }

    private void renderElements(HudRenderEvent event) {
        float animProgress = (float) Animations.easeOut(this.animProgress);
        alpha = Animations.numberFade(animProgress, startFade, endFade);
        render = alpha != 0;
        Blur.setGradientAlpha(alpha);
        CLIENT.gameRenderer.renderBlur(event.getTickDelta());
        CLIENT.getFramebuffer().beginWrite(false);
        Blur.renderRotatedGradient(event.getDrawContext(), event.getScreenWidth(), event.getScreenHeight());
//        lineRenderer.render(event.getDrawContext().getMatrices());
    }

    private void renderChildren(HudRenderEvent event) {
        for (HudElement child : children) {
            child.render(event.getDrawContext());
        }
    }

    @Override
    public void onMouse(MouseButtonEvent event) {
        if (!enabled) return;
        double mouseX = CLIENT.mouse.getX();
        double mouseY = CLIENT.mouse.getY();
        if(event.action == Input.KeyAction.Press) {
            for (HudElement each : getChildren()) {
                if (each.isHovered(mouseX, mouseY)) {
                    each.lastClickOffset = Pair.of(mouseX-(each.x), mouseY-(each.y));
                    each.click(mouseX, mouseY, event.button);
                    this.selected = each;
                    break;
                }
            }
        } else if (event.action == Input.KeyAction.Release) {
            for (HudElement each : getChildren()) {
                if (each.isHovered(mouseX, mouseY)) {
                    each.lastClickOffset = Pair.of(mouseX-(each.x), mouseY-(each.y));
                    each.release(mouseX, mouseY, event.button);
                    this.selected = each;
                    break;
                }
            }
        }
    }

    @Override
    public void onScroll(MouseScrollEvent e) {
        if (!enabled) return;
        double mouseX = CLIENT.mouse.getX();
        double mouseY = CLIENT.mouse.getY();
        double delta = e.vertical;
        for(HudElement each : getChildren()) {
            if (each.isHovered(mouseX, mouseY)) {
                each.onScroll(mouseX,mouseY,delta);
            }
        }
        for (HudElement each : this.getChildren()) {
            if (each.isHovered(mouseX, mouseY)) {
                each.onScroll(mouseX,mouseY,delta);
            }
        }
    }

    public void addChild(HudElement child) {
        if (child != null) {
            children.add(child);
            lastAdded = child;
        }
    }

    private void loadChildren() {
        for (HudElement child : cachedChildren.keySet()) {
            if (!children.contains(child)) addChild(child);
        }
    }

    public void clearChildren() {
        children.clear();
    }

    public boolean canRender() {
        return render && canUpdate() && checkScreen();
    }

    private boolean canUpdate() {
        return CLIENT != null && CLIENT.world != null && CLIENT.player != null;
    }

    private boolean checkScreen() {
        return CLIENT.currentScreen == null || CLIENT.currentScreen instanceof HandledScreen || CLIENT.currentScreen instanceof ChatScreen;
    }

    private void lockCursor() {
        ((MouseAccessor) CLIENT.mouse).setCursorLocked(true);
        InputUtil.setCursorParameters(CLIENT.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, CLIENT.mouse.getX(), CLIENT.mouse.getY());
    }

    private void unlockCursor() {
        ((MouseAccessor) CLIENT.mouse).setCursorLocked(false);
        InputUtil.setCursorParameters(CLIENT.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, CLIENT.mouse.getX(), CLIENT.mouse.getY());
    }

    public void toggle() {
        if (enabled) {
            startFade = 45;
            endFade = 0;
            enabled = false;
            clearChildren();
            lineRenderer.cache.clear();
            lockCursor();

            scheduler.close();
        } else {
            render = true;
            startFade = 1;
            endFade = 45;
            enabled = true;
            loadChildren();
            lineRenderer.init(50, 0.15f);
            unlockCursor();

            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(this::update, 0, 10, TimeUnit.MILLISECONDS);
        }
    }
}