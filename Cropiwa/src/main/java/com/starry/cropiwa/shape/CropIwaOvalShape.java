package com.starry.cropiwa.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.starry.cropiwa.config.CropIwaOverlayConfig;

/**
 * Created by yarolegovich on 04.02.2017.
 * https://github.com/yarolegovich
 */
public class CropIwaOvalShape extends CropIwaShape {

    private Path clipPath;
    /**
     * 是否裁剪成圆形图片，默认裁剪成方形图片
     */
    private boolean clipOval;

    public CropIwaOvalShape(CropIwaOverlayConfig config) {
        super(config);
        clipPath = new Path();
    }

    public CropIwaOvalShape(CropIwaOverlayConfig config, boolean clipOval) {
        super(config);
        clipPath = new Path();
        this.clipOval = clipOval;
    }

    @Override
    protected void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint) {
        canvas.drawOval(cropBounds, clearPaint);
    }


    @Override
    protected void drawBorders(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawOval(cropBounds, paint);
        if (overlayConfig.isDynamicCrop()) {
            canvas.drawRect(cropBounds, paint);
        }
    }

    @Override
    protected void drawGrid(Canvas canvas, RectF cropBounds, Paint paint) {
        clipPath.rewind();
        clipPath.addOval(cropBounds, Path.Direction.CW);

        //    @Deprecated
        //    Use the flagless version of save(), saveLayer(RectF, Paint) or saveLayerAlpha(RectF, int).
        //    For saveLayer() calls the clip was always restored for Hardware accelerated canvases and
        //    as of API level 26 that is the default behavior for all canvas types.
        //    使用save（）、saveLayer（RectF，Paint）或saveLayerAlpha（RectF，int）的无标志版本。
        //    对于saveLayer（）调用，对于硬件加速的画布，始终会恢复剪辑，并且从API级别26开始，这是所有画布类型的默认行为。
        //    public static final int CLIP_SAVE_FLAG = 2;
        canvas.save();
        canvas.clipPath(clipPath);
        super.drawGrid(canvas, cropBounds, paint);
        canvas.restore();
    }

    @Override
    public CropIwaShapeMask getMask() {
        return new OvalShapeMask(clipOval);
    }

    private static class OvalShapeMask implements CropIwaShapeMask {

        private boolean clipOval;

        public OvalShapeMask(boolean clipOval) {
            this.clipOval = clipOval;
        }

        @Override
        public Bitmap applyMaskTo(Bitmap croppedRegion) {
            if (!clipOval) return croppedRegion;

            croppedRegion.setHasAlpha(true);

            Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            RectF ovalRect = new RectF(0, 0, croppedRegion.getWidth(), croppedRegion.getHeight());
            Path maskShape = new Path();
            //This is similar to ImageRect\Oval
            maskShape.addRect(ovalRect, Path.Direction.CW);
            maskShape.addOval(ovalRect, Path.Direction.CCW);

            Canvas canvas = new Canvas(croppedRegion);
            canvas.drawPath(maskShape, maskPaint);
            return croppedRegion;
        }
    }
}

