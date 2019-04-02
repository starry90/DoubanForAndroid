package com.starry.cropiwa.image;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author yarolegovich
 * 25.02.2017.
 */
public class CropArea {

    public static CropArea create(RectF coordinateSystem, RectF imageRect, RectF cropRect) {
        return new CropArea(
                moveRectToCoordinateSystem(coordinateSystem, imageRect),
                moveRectToCoordinateSystem(coordinateSystem, cropRect));
    }

    private static Rect moveRectToCoordinateSystem(RectF system, RectF rect) {
        float originX = system.left, originY = system.top;
        return new Rect(
                Math.round(rect.left - originX), Math.round(rect.top - originY),
                Math.round(rect.right - originX), Math.round(rect.bottom - originY));
    }

    private final Rect imageRect;
    private final Rect cropRect;

    public CropArea(Rect imageRect, Rect cropRect) {
        this.imageRect = imageRect;
        this.cropRect = cropRect;
    }

    public Bitmap applyCropTo(Bitmap bitmap) {
        int imageWidth = imageRect.width();
        int imageHeight = imageRect.height();

        int cropWidth = cropRect.width();
        int cropHeight = cropRect.height();

        //throw new IllegalArgumentException("x must be >= 0");
        int left = cropRect.left > 0 ? cropRect.left : 0;
        //throw new IllegalArgumentException("y must be >= 0");
        int top = cropRect.top > 0 ? cropRect.top : 0;

        int x = findRealCoordinate(bitmap.getWidth(), left, imageWidth);
        int y = findRealCoordinate(bitmap.getHeight(), top, imageHeight);
        int width = findRealCoordinate(bitmap.getWidth(), cropWidth, imageWidth);
        int height = findRealCoordinate(bitmap.getHeight(), cropHeight, imageHeight);

        //用户把图片拖动到裁剪区域外时点击裁剪会产生以下错误
        // throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        // throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        if (cropRect.left < 0 || cropRect.top < 0
                || x + width > bitmap.getWidth() || y + height > bitmap.getHeight()) {
            // 此时默认截取屏幕正中间的内容
            x = bitmap.getWidth() / 4;
            width = bitmap.getWidth() / 2;
            y = bitmap.getHeight() / 4;
            height = bitmap.getHeight() / 2;
            //比较XY轴大小，按小的一方裁剪成正方形
            if (x > y) {
                x = bitmap.getWidth() / 2 - height / 2;
                width = height;
            } else {
                y = bitmap.getHeight() / 2 - width / 2;
                height = width;
            }
        }

        Bitmap immutableCropped = Bitmap.createBitmap(bitmap, x, y, width, height);
        return immutableCropped.copy(immutableCropped.getConfig(), true);
    }


    private int findRealCoordinate(int imageRealSize, int cropCoordinate, float cropImageSize) {
        return Math.round((imageRealSize * cropCoordinate) / cropImageSize);
    }

}
