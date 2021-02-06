package net.imyeyu.engine.core;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * FX 游戏程序
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 08:36:13
 *
 */
public abstract class GameFX extends Application {
	
	private YeyuEngine engine;

	public final void start(Stage stage) throws Exception {
		try {
			Class.forName("net.imyeyu.px.PixelFX");
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("找不到依赖，夜雨游戏引擎需要 PixelFX.jar");
		}
		
		engine = new YeyuEngine(stage);
		engine.onInit = this::onInit;
		engine.onLaunch = this::onLaunch;
		engine.onShutdown = this::onShutdown;
		engine.launch();
	}
	
	/**
	 * 初始化事件
	 * <br>已有: 窗体、场景、根布局
	 * <br>未有: 页面、帧更新
	 * 
	 */
	public void onInit() {
		// 子类实现
	}
	
	/**
	 * 程序启动事件，子类必须实现
	 * <br>已有: 窗体、场景、根布局、页面、帧更新
	 * 
	 */
	public abstract void onLaunch();
	
	/**
	 * 默认关闭事件，返回 false 阻止窗体关闭
	 * 
	 * @return
	 */
	public boolean onShutdown() {
		// 子类实现
		return true;
	}
}