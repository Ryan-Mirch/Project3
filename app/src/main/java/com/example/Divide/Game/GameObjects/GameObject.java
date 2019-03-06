package com.example.Divide.Game.GameObjects;

import android.graphics.Canvas;

public interface GameObject {
    void draw(Canvas canvas);
    void update(long frameTime);
}
