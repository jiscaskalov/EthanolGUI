package jsco.dev.ethanolgui.client.action.auth;

import jsco.dev.ethanolgui.render.elements.ActionElement;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AuthActionsList {
    @Getter private static final List<ActionElement> list = new ArrayList<>();

    public static void fillList(int width, int height, Color color) {
        list.add(new ActionElement(width, height, 4, "Try Authenticate", "Tries to authenticate \nwith the server.", color, 2));
    }
}
