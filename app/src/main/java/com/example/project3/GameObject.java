package com.example.project3;

import android.graphics.Canvas;

public interface GameObject {
    void draw(Canvas canvas);
    void update(long frameTime);
}
