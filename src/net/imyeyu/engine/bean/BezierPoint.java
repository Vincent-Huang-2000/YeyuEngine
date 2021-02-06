package net.imyeyu.engine.bean;

/**
 * 坐标点
 * 
 * @author 夜雨
 * @createdAt 2021-01-19 10:50:05
 *
 */
public class BezierPoint {

	public double x;
	public double y;
	
	public BezierPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void boost(int v) {
		this.x *= v;
		this.y *= v;
	}
}