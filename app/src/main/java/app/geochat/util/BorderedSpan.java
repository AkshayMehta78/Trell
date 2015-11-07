package app.geochat.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

/**
 * Created by akshaymehta on 06/11/15.
 */

public class BorderedSpan extends ReplacementSpan {
    final Paint mPaintBorder, mPaintBackground;
    int mWidth;
    Resources r;
    int mTextColor;

    public BorderedSpan(Context context) {
        mPaintBorder = new Paint();
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setAntiAlias(true);

        mPaintBackground = new Paint();
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setAntiAlias(true);

        r = context.getResources();

        mPaintBorder.setColor(Color.RED);
        mPaintBackground.setColor(Color.WHITE);
        mTextColor = Color.BLACK;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //return text with relative to the Paint
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.drawRect(x, top, x + mWidth, bottom, mPaintBackground);
        canvas.drawRect(x, top, x + mWidth, bottom, mPaintBorder);
        paint.setColor(mTextColor); //use the default text paint to preserve font size/style
        canvas.drawText(text, start, end, x, y, paint);
    }
}