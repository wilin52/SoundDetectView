package com.wilin.yx.sounddetectview;

public class Point {
  public float x;
  public float y;

  public Point() {
	  
  }

  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }
  
  public void copy(Point point){
	  this.x = point.x;
	  this.y = point.y;
  }

}
