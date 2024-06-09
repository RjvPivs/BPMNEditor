package com.bpmn.editor.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.ByteArrayOutputStream;

public class TextGenerator {
    /**
     * Генератор текста для акторов.
     * @param s Генерируемый текст.
     * @return Картинка с текстом.
     */
    public static byte[] generate(String s) {
        float textSize = 30;
        String text = s;
        android.text.TextPaint tp = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        tp.setColor(Color.rgb(17, 45, 62));
        tp.setTextSize(textSize);
        Rect bounds = new Rect();
        tp.getTextBounds(text, 0, text.length(), bounds);
        StaticLayout sl = new StaticLayout(text, tp, bounds.width() + 5,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);

        Bitmap bmp = Bitmap.createBitmap(bounds.width() + 5, bounds.height() + 5,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        sl.draw(canvas);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        return image;
    }
}
