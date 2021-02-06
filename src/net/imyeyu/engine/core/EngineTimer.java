package net.imyeyu.engine.core;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Properties;
import java.util.TimerTask;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.engine.Framework;

/**
 * 游戏主循环，帧更新
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 15:01:15
 *
 */
public class EngineTimer {

	private final Timer timer;
	private final FrameStats frameStats = new FrameStats();
	private final EngineListener engineListener; // 引擎监听
	private final SimpleLongProperty fpsProperty = new SimpleLongProperty(0);
	
	private boolean debug = false;

	// 当前帧，上一帧，累计帧差，当前帧差
	private double nowNanos, lastNanos, deltaNanos, betweenNanos;
	// 帧生成时间（纳秒）
	private double NPF;
	// 预设每秒帧数
	private int FPS;
	// 总帧数
	private long FPSL = 0;

	protected OnStart onStart;
	protected OnUpdate onUpdate;
	protected OnStop onStop;

	public EngineTimer() {
		this(60);
	}

	public EngineTimer(int FPS) {
		this.FPS = FPS;
		this.timer = new Timer();
		this.engineListener = new EngineListener();
		this.engineListener.start();
	}

	/**
	 * 启动帧更新
	 * 
	 */
	protected void start() {
		this.timer.start();
	}

	/**
	 * 停止帧更新
	 * 
	 */
	protected void stop() {
		this.timer.stop();
	}

	/**
	 * 帧渲染器（降权）
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-17 16:13:32
	 *
	 */
	private final class Timer extends AnimationTimer {

		private double lastNanos = 0;

		// 启动
		public void start() {
			if (onStart != null) {
				onStart.handle();
			}
			reset();
			super.start();
		}

		// 停止
		public void stop() {
			if (onStop != null) {
				onStop.handle();
			}
			super.stop();
			reset();
		}

		// 帧更新
		public void handle(long time) {
			nowNanos = time;
			if (0 < lastNanos) {
				// 累计帧差     当前帧差
				deltaNanos += betweenNanos = nowNanos - lastNanos;
			}
			// 累计帧差 大于 最小帧生成时间（足够渲染下一帧）
			if (NPF <= deltaNanos) {
				if (onUpdate != null) {
					onUpdate.handle(deltaNanos);
					if (debug) {
						frameStats.addFrame(betweenNanos);
					}
				}
				// 剩余帧差
				deltaNanos -= NPF;
			}
			lastNanos = nowNanos;
		}
	}

	/**
	 * 状态监听（FPS 和内存状态）
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-18 13:55:09
	 *
	 */
	private final class EngineListener extends Service<Void> {
		
		private long old = 0, secondFlag = 0;
		private final String staticInfo;
		
		public EngineListener() {
			// 系统信息
			Properties p = System.getProperties();
			StringBuilder sb = new StringBuilder();
			sb.append(Framework.NAME).append(' ').append(Framework.VERSION).append('\n');
			sb.append(p.getProperty("java.vm.name")).append(' ').append(p.getProperty("java.version"));
			staticInfo = sb.append('\n').toString();
		}

		protected Task<Void> createTask() {
			return new Task<Void>() {
				protected Void call() throws Exception {
					
					final StringBuilder sb = new StringBuilder();
					final MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
					
					new java.util.Timer().schedule(new TimerTask() {
						public void run() {
							if (debug) {
								if (secondFlag++ % 10 == 0) { // FPS
									fpsProperty.set(FPSL - old);
									old = FPSL;
								}
								sb.setLength(0);
								// 内存信息
								sb.append("\nMemory(MB): ");
								sb.append("\nInit: ").append(mb.getHeapMemoryUsage().getInit() / 1048576);
								sb.append("\nUsed: ").append(mb.getHeapMemoryUsage().getUsed() / 1048576);
								sb.append("\nCommitted: ").append(mb.getHeapMemoryUsage().getCommitted() / 1048576);
								sb.append("\nMax: ").append(mb.getHeapMemoryUsage().getMax() / 1048576);
								frameStats.setSystemInfo(staticInfo + sb.toString());
							}
						}
					}, 0, 100);
					return null;
				}
			};
		}
	}

	/**
	 * 帧生成效率计算
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-25 15:19:52
	 *
	 */
	private final class FrameStats {

		private double ms = 0;
		private String systemInfo;
		private final ReadOnlyStringWrapper text;

		public FrameStats() {
			text = new ReadOnlyStringWrapper(this, "text", "Average frame interval(ms): N/A\nFrame: 0\nFPS: 0");
		}

		/**
		 * 渲染一帧
		 * 
		 * @param nowNanos 当前纳秒
		 */
		public void addFrame(double nowNanos) {
			ms = (ms * FPSL + nowNanos * 1E-6) / (FPSL + 1);
			FPSL++;
			text.set(systemInfo + toString());
		}
		
		public void setSystemInfo(String info) {
			this.systemInfo = info;
		}

		public ReadOnlyStringProperty textProperty() {
			return text.getReadOnlyProperty();
		}

		public String toString() {
			return String.format("\n\nAverage frame interval(ms): %.3f\nFrame: %,d\nFPS: %,d", ms, FPSL, fpsProperty.get());
		}
	}
	
	/**
	 * 调试模式
	 * 
	 * @return
	 */
	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * 重置参数
	 * 
	 */
	public void reset() {
		nowNanos = lastNanos = deltaNanos = FPSL = 0;
	}

	/**
	 * 获取当前帧（纳秒）
	 * 
	 * @return
	 */
	public double getNowNanos() {
		return nowNanos;
	}

	/**
	 * 获取上一帧（纳秒）
	 * 
	 * @return
	 */
	public double getLastNanos() {
		return lastNanos;
	}

	/**
	 * 获取帧差（纳秒）
	 * 
	 * @return
	 */
	public double getDeltaNanos() {
		return deltaNanos;
	}

	/**
	 * 获取当前帧（毫秒）
	 * 
	 * @return
	 */
	public double getNowMillis() {
		return nowNanos * 1E-6;
	}

	/**
	 * 获取上一帧（毫秒）
	 * 
	 * @return
	 */
	public double getLastMillis() {
		return lastNanos * 1E-6;
	}

	/**
	 * 获取帧差（毫秒）
	 * 
	 * @return
	 */
	public double getDeltaMillis() {
		return deltaNanos * 1E-6;
	}

	/**
	 * 获取当前帧（秒）
	 * 
	 * @return
	 */
	public double getNowSecond() {
		return nowNanos * 1E-9;
	}

	/**
	 * 获取上一帧（秒）
	 * 
	 * @return
	 */
	public double getLastSecond() {
		return lastNanos * 1E-9;
	}

	/**
	 * 获取帧差（秒）
	 * 
	 * @return
	 */
	public double getDeltaSecond() {
		return deltaNanos * 1E-9;
	}

	/**
	 * 帧率监听
	 * 
	 * @return
	 */
	public ReadOnlyLongProperty fpsProperty() {
		return fpsProperty;
	}

	/**
	 * Debug 文本监听
	 * 
	 * @return
	 */
	public ReadOnlyStringProperty textProperty() {
		return frameStats.textProperty();
	}

	/**
	 * 获取帧率
	 * 
	 * @return
	 */
	public int getFPS() {
		return FPS;
	}

	/**
	 * 设置帧率
	 * 
	 * @param FPS
	 */
	public void setFPS(int FPS) {
		if (FPS < 1) {
			throw new IllegalArgumentException("帧率不可小于 1 ");
		}
		this.FPS = FPS;
		this.NPF = 1E9 / this.FPS;
	}

	/**
	 * 启动事件
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-17 15:23:28
	 *
	 */
	protected static interface OnStart {
		void handle();
	}

	/**
	 * 停止事件
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-17 15:23:35
	 *
	 */
	protected static interface OnStop {
		void handle();
	}

	/**
	 * 帧更新
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-17 15:17:29
	 *
	 */
	protected static interface OnUpdate {
		void handle(double time);
	}
}