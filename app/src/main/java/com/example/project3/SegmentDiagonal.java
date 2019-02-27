package com.example.project3;

import android.graphics.Path;

public class SegmentDiagonal implements SegmentInterface{
    private int upperX;
    private int upperY;
    private int lowerX;
    private int lowerY;
    private Path shape;

    public SegmentDiagonal(int upperX, int upperY, int lowerX, int lowerY) {
        this.upperX = upperX;
        this.upperY = upperY;
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        createShape();
    }

    public Path getPath(){
        return shape;
    }

    private void stretch(int distance){
        lowerY -= distance;
    }

    private void move(int distance){
        upperY -= distance;
        lowerY -= distance;
    }

    private void createShape(){
        shape = new Path();
        shape.moveTo(upperX,upperY);
        shape.lineTo(lowerX, lowerY);
    }

    public void update(){
        stretch(GameLogic.getSpeed());
        move(GameLogic.getSpeed());
        createShape();
    }
}

