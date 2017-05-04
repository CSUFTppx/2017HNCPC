package com.csuft.ppx.position;

//自定义圆类
public class Circular {
	private double R;//半径
	private double X;//圆心的x坐标
	private double Y;//圆心的Y坐标
	
	public Circular(double R,double X,double Y) {
		// TODO Auto-generated constructor stub
		this.R=R;
		this.X=X;
		this.Y=Y;
	}
	
	public double getX() {
		return X;
	}
	public double getY() {
		return Y;
	}
	public double getR() {
		return R;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "R:"+R+"   X:"+X+"   Y:"+Y;
	}
}
