package hero.bane.herobot.mod.client.screen.ai.starfield;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public final class PixelBatch {
    private int[] coords = new int[4096];
    private int[] colors = new int[1024];
    private int count;

    private Matrix3x2f pose;
    private ScreenRectangle scissor;
    private ScreenRectangle bounds;
    private int minX, minY, maxX, maxY;

    public PixelBatch() {
    }

    public void begin(GuiGraphicsExtractor g, int left, int top, int right, int bottom) {
        count = 0;
        pose = new Matrix3x2f(g.pose());
        scissor = new ScreenRectangle(left, top, right - left, bottom - top).transformAxisAligned(pose);
        bounds = null;
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
    }

    void rect(int x0, int y0, int x1, int y1, int argb) {
        if ((argb >>> 24) == 0) return;
        if (count == colors.length) grow();
        int i = count * 4;
        coords[i] = x0;
        coords[i + 1] = y0;
        coords[i + 2] = x1;
        coords[i + 3] = y1;
        colors[count] = argb;
        count++;
        if (x0 < minX) minX = x0;
        if (y0 < minY) minY = y0;
        if (x1 > maxX) maxX = x1;
        if (y1 > maxY) maxY = y1;
    }

    public void pixel(int x, int y, int argb) {
        rect(x, y, x + 1, y + 1, argb);
    }

    public void submit(GuiGraphicsExtractor g) {
        if (count == 0) return;
        ScreenRectangle box = new ScreenRectangle(minX, minY, maxX - minX, maxY - minY).transformMaxBounds(pose);
        bounds = scissor == null ? box : scissor.intersection(box);
        if (bounds == null) {
            count = 0;
            return;
        }
        // 26.3: custom GUI render states use the new render pipeline; draw the batch as plain fills instead.
        boolean clip = scissor != null;
        if (clip) g.enableScissor(scissor.left(), scissor.top(), scissor.right(), scissor.bottom());
        for (int i = 0; i < count; i++) {
            int j = i * 4;
            g.fill(coords[j], coords[j + 1], coords[j + 2], coords[j + 3], colors[i]);
        }
        if (clip) g.disableScissor();
        count = 0;
    }

    private void grow() {
        int newCap = colors.length * 2;
        coords = java.util.Arrays.copyOf(coords, newCap * 4);
        colors = java.util.Arrays.copyOf(colors, newCap);
    }
}
