package com.example.besafe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.widget.ImageView;

public class BitmapOperation {
    public static BitmapDrawable scaleImage(Context context, Bitmap bitmap, ImageView image){
        int sourceWidth = bitmap.getWidth();
        int sourceHeight = bitmap.getHeight();

        int newWidth = image.getWidth();
        int newHeight = image.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

//                    final int color = 0xff424242;
//                    final Paint paint = new Paint();
//                    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//                    final RectF rectF = new RectF(rect);
//
//                    paint.setAntiAlias(true);
//                    canvas.drawARGB(0, 0, 0, 0);
//                    paint.setColor(color);
////                    canvas.drawRoundRect(rectF, 40, 30, paint);
//                    canvas.drawRoundRect(rectF, 0, 0, paint);
//                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//                    canvas.drawBitmap(bitmap, rect, rect, paint);
        Paint p = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        p.setColorFilter(filter);

//                    Shader shaderA = new LinearGradient(0, 0, newWidth, newHeight, 0xFF7F7F7F, 0x00000000, Shader.TileMode.CLAMP);
//                    Shader shaderB = new BitmapShader(output, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//                    p.setShader(new ComposeShader(shaderA, shaderB, PorterDuff.Mode.SRC_IN));

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
        Canvas canvas2 = new Canvas(dest);
        canvas2.drawBitmap(bitmap, null, targetRect, p);
        BitmapDrawable ob = new BitmapDrawable(context.getResources(), dest);
        return ob;
    }
}
