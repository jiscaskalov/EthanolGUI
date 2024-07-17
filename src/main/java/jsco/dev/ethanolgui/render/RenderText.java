package jsco.dev.ethanolgui.render;

import jsco.dev.ethanolgui.client.EthanolGUI;
import lombok.Getter;
import me.x150.renderer.font.FontRenderer;

import java.awt.*;

public class RenderText {
    @Getter private static FontRenderer smallRenderer;
    @Getter private static FontRenderer largeRenderer;
    @Getter private static Font font;

    public static void initFonts() {
        EthanolGUI.LOGGER.info("Initializing Fonts...");
        font = Font.decode("Inter");
        smallRenderer = createRenderer(font, 8);
        largeRenderer = createRenderer(font, 10);
    }

    public static FontRenderer createRenderer(Font font, int size) {
        return new FontRenderer(new Font[]{font}, size, 5, 2, "123");
    }

    public static int numLinesWrappedText(String text, FontRenderer renderer, int width, float scale) {
        int index = 0;
        int numLines = 1;
        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == ' ') {
                float lineWidth = renderer.getStringWidth(text.substring(index, i)) * scale;

                if(lineWidth + 10 > width * scale) {
                    numLines++;
                    index = i + 1;
                }
            }

            if(i == text.length() - 1 && index!= i) {
                numLines++;
            }
        }

        return numLines;
    }
}