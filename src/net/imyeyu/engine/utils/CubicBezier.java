package net.imyeyu.engine.utils;

import java.util.ArrayList;
import java.util.List;

import net.imyeyu.engine.bean.BezierPoint;

/**
 * 生成二次贝塞尔曲线点（CSS 3 格式） <br>
 * 示例:
 * 
 * <pre>
 * // 共 120 帧先快后慢向右平移 300 像素（时间决定于渲染 FPS）
 * 
 * // 成员变量
 * long frame = 0;
 * 
 * Region region = new Region();
 * region.setPrefSize(100, 100);
 * region.setBackgound(....);
 * 
 * List&lt;BezierPoint&gt; bps = new CubicBezier(.23, 1, .32, 1).precision(120).build();
 * AnimationTimer timer = new AnimationTimer() {
 *     public void handle(long now) {
 *         if (frame < bps.size()) {
 *             region.setTranslateX(bps.get(frame).y * 300);
 *             frame++;
 *         } else {
 *             timer.stop();
 *         }
 *     }
 * };
 * timer.start();
 * </pre>
 * 
 * @author 夜雨
 * @createdAt 2021-01-19 10:54:48
 *
 */
public class CubicBezier {

	private int boost = 1;
	private double precision = 0.01;
	private BezierPoint cps[] = new BezierPoint[4];

	public CubicBezier(double f0, double f1, double f2, double f3) {
		cps[0] = new BezierPoint(0, 0);
		cps[1] = new BezierPoint(f0, f1);
		cps[2] = new BezierPoint(f2, f3);
		cps[3] = new BezierPoint(1, 1);
	}

	/**
	 * 精度（默认 100，越大数量越多，精度越高）
	 * 
	 * @param precision
	 * @return
	 */
	public CubicBezier precision(double precision) {
		if (precision < 4) {
			throw new IllegalArgumentException("精度不可小于 4");
		}
		this.precision = 1d / precision;
		return this;
	}

	/**
	 * 放大（默认 1，不可小于 1）
	 * 
	 * @param boost
	 * @return
	 */
	public CubicBezier boost(int boost) {
		if (boost < 1) {
			throw new IllegalArgumentException("放大倍数不可小于 1");
		}
		this.boost = boost;
		return this;
	}

	/**
	 * 构造二次贝塞尔点
	 * 
	 * @return
	 */
	public List<BezierPoint> build() {
		List<BezierPoint> bps = new ArrayList<>();

		int n = cps.length - 1;
		int j, k;
		BezierPoint[] p;
		for (float i = 0; i <= 1; i += precision) {
			p = new BezierPoint[n + 1];
			for (j = 0; j <= n; j++) {
				p[j] = new BezierPoint(cps[j].x, cps[j].y);
			}
			for (k = 1; k <= n; k++) {
				for (j = 0; j <= n - k; j++) {
					p[j].x = (1 - i) * p[j].x + i * p[j + 1].x;
					p[j].y = (1 - i) * p[j].y + i * p[j + 1].y;
				}
			}
			p[0].boost(boost);
			bps.add(p[0]);
		}
		return bps;
	}
}