package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.client.Pair;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

import static jsco.dev.ethanolgui.client.EthanolGUI.CLIENT;

public abstract class HudElement {
    @Getter @Setter private HudElement parent;
    @Getter private final List<HudElement> children;

    protected HudElement selected = null;

    @Getter @Setter public int x, y, width, height, cachedWidth, cachedHeight;

    @Getter @Setter public boolean rendering, renderDependentOnParent;
    public Pair<Double, Double> lastClickOffset;
    @Setter public boolean canDrag = false;
    public boolean altKey, sftKey, crlKey, leftClick;

    protected boolean renderChildrenFirst = false;

    protected HudElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.parent = null;
        this.children = new ArrayList<>();
        this.rendering = true;
        this.renderDependentOnParent = false;
        this.canDrag = false;
    }
    protected HudElement(int x, int y, int width, int height, boolean renderChildrenFirst) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.parent = null;
        this.children = new ArrayList<>();
        this.rendering = true;
        this.renderDependentOnParent = false;
        this.renderChildrenFirst = renderChildrenFirst;
        this.canDrag = false;
    }

    protected abstract void onRender(DrawContext context);
    protected abstract void onClick(double mouseX, double mouseY, int button);
    public abstract void onRelease(double mouseX, double mouseY, int button);
    public abstract void onScroll(double mouseX, double mouseY, double delta);

    public void click(double mouseX, double mouseY, int button) {
        if(button == 0)
            leftClick = true;
        this.onClick(mouseX, mouseY, button);

        for (int i = 0; i <= children.size() - 1; i++) {
            HudElement child = children.get(i);
            child.click(mouseX, mouseY, button);
            if (child.isHovered(mouseX, mouseY)) {
                this.selected = child;
                child.lastClickOffset = Pair.of(mouseX-(child.x), mouseY-(child.y));
                break;
            }
        }
    }

    public void release(double mouseX, double mouseY, int button) {
        if(button == 0)
            leftClick = false;
        this.onRelease(mouseX, mouseY, button);

        if (selected != null) {
            selected.onRelease(mouseX, mouseY, button);
            selected = null;
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        double xMin = Math.min(x, width);
        double xMax = Math.max(x, width);
        double yMin = Math.min(y, height);
        double yMax = Math.max(y, height);

        return canRender() && mouseX >= xMin && mouseX < xMax && mouseY >= yMin && mouseY < yMax;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return isHovered((int)mouseX, (int)mouseY);
    }

    public void setAllRendering(boolean b) {
        this.rendering = b;
        for(HudElement each : children) {
            each.setRendering(b);
        }
    }

    public boolean canRender() {
        if (rendering) {
            if (renderDependentOnParent && hasParent()) {
                return parent.canRender();
            }
            return true;
        }
        return false;
    }

    public void render(DrawContext context) {
        EthanolRenderer.unscaledProjection();

        if (canRender()) {
            width = x+cachedWidth;
            height = y+cachedHeight;

            if (leftClick && canDrag) {
                Pair<Double, Double> cursor = Pair.of(CLIENT.mouse.getX(), CLIENT.mouse.getY());
                dragWithCursor(cursor, true);
                width = x+cachedWidth;
                height = y+cachedHeight;
            }

            if(renderChildrenFirst) {
                for (HudElement child : children) {
                    child.render(context);
                }
                this.onRender(context);
            } else {
                this.onRender(context);
                for (HudElement child : children) {
                    child.render(context);
                }
            }
            postRender(context);
        }

        EthanolRenderer.scaledProjection();
    }

    public void postRender(DrawContext drawContext) {

    }

    public boolean hasParent() {
        return parent != null;
    }

    public void clearChildren() {
        children.clear();
    }

    public HudElement addChild(HudElement child) {
        if (child != null && child != this) {
            children.add(child);
            child.setParent(this);
        }
        return child;
    }

    public HudElement removeChild(HudElement child) {
        if (child != null && child != this) {
            children.remove(child);
        }
        return child;
    }

    public void move(int delX, int delY) {
        x += delX;
        y += delY;

        for (HudElement child : children) {
            child.move(delX, delY);
        }
    }

    public void moveChildren(int delX, int delY) {
        for (HudElement child : children) {
            child.move(delX, delY);
        }
    }

    public void moveChildren(double delX, double delY) {
        for (HudElement child : children) {
            child.move((int)delX, (int)delY);
        }
    }

    public void move(double delX, double delY) {
        x = (int)(x + delX);
        y = (int)(y + delY);

        moveChildren(delX, delY);
    }

    public void moveTo(int x, int y) {
        int delX = x - this.x;
        int delY = y - this.y;

        this.x = x;
        this.y = y;
        for (HudElement child : children) {
            child.move(delX, delY);

        }
    }

    public void moveTo(double x, double y) {
        double delX = x - this.x;
        double delY = y - this.y;
        this.x = (int)x;
        this.y = (int)y;

        for (HudElement child : children) {
            child.move(delX, delY);
        }
    }

    public void moveChildrenTo(int x, int y) {
        int delX = x - this.x;
        int delY = y - this.y;

        this.x = x;
        this.y = y;
        for (HudElement child : children) {
            child.move(delX, delY);

        }
    }

    public void moveChildrenTo(double x, double y) {
        double delX = x - this.x;
        double delY = y - this.y;
        this.x = (int)x;
        this.y = (int)y;

        for (HudElement child : children) {
            child.move(delX, delY);
        }
    }

    public void centerIn(int frameWidth, int frameHeight){
        float guiScale = CLIENT.options.getGuiScale().getValue();

        if(guiScale == 0)
            guiScale = 4;
        frameWidth*= (int) guiScale;
        frameHeight*= (int) guiScale;


        moveTo((frameWidth / 2 - width / 2), (frameHeight / 2 - height / 2));
    }

    public void drag(Pair<Double, Double> predrag, Pair<Double, Double> postdrag) {
        drag(predrag, postdrag, false);
    }

    public void drag(Pair<Double, Double> predrag, Pair<Double, Double> postdrag, boolean force) {
        if (!force && !canDrag) return;

        double delX = (postdrag.left - predrag.left);
        double delY = (postdrag.right - predrag.right);

        move(delX,delY);
    }

    public void dragWithCursor(Pair<Double, Double> cursor, boolean force) {
        if (!force && !canDrag) return;
        double delX = cursor.left - (((double)x+lastClickOffset.left));
        double delY = cursor.right - (((double)y+lastClickOffset.right));

        move((int)delX, (int)delY);
    }
}
