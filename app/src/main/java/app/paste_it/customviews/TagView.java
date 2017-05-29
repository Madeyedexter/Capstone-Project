package app.paste_it.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by 834619 on 5/16/2017.
 */

public class TagView extends android.support.v7.widget.AppCompatTextView {

    private Paint mTagPaint;

    private int backgroundColor;

    public TagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        mTagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTagPaint.setColor(backgroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
