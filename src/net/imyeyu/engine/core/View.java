package net.imyeyu.engine.core;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;

/**
 * 页面
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 13:10:51
 *
 */
public abstract class View extends StackPane {
	
	// 页面过渡
	Timeline show, hide;
	
	/**
	 * 设置页面显示过渡
	 * 
	 * @param keyFrames 关键帧
	 */
	public void setShowKeyFrames(List<KeyFrame> keyFrames) {
		if (show == null) {
			show = new Timeline(60);
		}
		show.getKeyFrames().setAll(keyFrames);
	}

	/**
	 * 设置页面隐藏过渡
	 * 
	 * @param keyFrames 关键帧
	 */
	public void setHideKeyFrames(List<KeyFrame> keyFrames) {
		if (hide == null) {
			hide = new Timeline(60);
		}
		hide.getKeyFrames().setAll(keyFrames);
	}
	
	/**
	 * 引擎启动
	 * 
	 */
	public abstract void onLaunch();
	
	/**
	 * 引擎卸载
	 * 
	 */
	protected void onShutdown() {
		// 子类实现
	}

	/**
	 * 页面显示（如果有动画，将在动画执行前调用）
	 * 
	 */
	protected void onShow() {
		// 子类实现
	}
	
	/**
	 * 页面隐藏（如果有动画，将在动画执行后调用）
	 * 
	 */
	protected void onHide() {
		// 子类实现
	}
	
	/**
	 * 启动帧更新
	 * 
	 */
	protected void onStart() {
		// 子类实现
	}
	
	/**
	 * 帧更新
	 * 
	 * @param time
	 */
	protected void onUpdate(double time) {
		// 子类实现
	}
	
	/**
	 * 停止帧更新
	 * 
	 */
	protected void onStop() {
		// 子类实现
	}
}