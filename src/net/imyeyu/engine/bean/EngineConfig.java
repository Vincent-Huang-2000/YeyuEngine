package net.imyeyu.engine.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.imyeyu.engine.Framework;

/**
 * 引擎配置
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 09:19:55
 *
 */
public class EngineConfig {

	// FPS 限制
	private int FPS, FPSMin;
	// 图标
	private Image icon;
	// 标题
	private String title;
	// CSS
	private List<String> CSS;
	// 尺寸
	private double width, height;
	// 光标
	private Cursor cursor;
	// 可改变尺寸
	private boolean canResize = true;
	// 显示调试信息
	private boolean isDebug = false;
	// 默认新页面在旧页面过渡前插入
	private boolean isInBeforeOut = true;
	// 场景底色
	private Paint bg;

	public EngineConfig() {
		FPS = 60;
		FPSMin = 12;
		title = "夜雨游戏引擎";
		CSS = new ArrayList<>();
		width = 850;
		height = 620;
		icon = new Image(Framework.RES_PATH + "icon.png");
		bg = Color.WHITE;
		cursor = Cursor.DEFAULT;
	}

	/**
	 * 每秒帧数。默认 60
	 * 
	 * @param FPS
	 */
	public void setFPS(int FPS) {
		if (FPS < 1) {
			throw new IllegalArgumentException("帧率不可小于 1 ");
		}
		this.FPS = FPS;
	}

	public int getFPS() {
		return FPS;
	}

	/**
	 * 失去焦点时每秒帧数。默认 12
	 * 
	 * @param FPSMin
	 */
	public void setFPSMin(int FPSMin) {
		if (FPSMin < 1) {
			throw new IllegalArgumentException("帧率不可小于 1 ");
		}
		this.FPSMin = FPSMin;
	}

	public int getFPSMin() {
		return FPSMin;
	}

	/**
	 * 窗体图标
	 * 
	 * @param icon
	 */
	public void setIcon(Image icon) {
		this.icon = icon;
	}
	public void setIcon(String url) {
		this.icon = new Image(url);
	}

	public Image getIcon() {
		return icon;
	}

	/**
	 * 窗体标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	/**
	 * 场景样式表
	 * 
	 * @param CSS
	 */
	public void setCSS(String... CSS) {
		this.CSS.addAll(Arrays.asList(CSS));
	}
	
	public List<String> getCSS() {
		return CSS;
	}

	/**
	 * 窗体宽度
	 * 
	 * @param width
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}

	/**
	 * 窗体高度
	 * 
	 * @param height
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	public double getHeight() {
		return height;
	}
	
	/**
	 * 窗体尺寸
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * 默认光标
	 * 
	 * @param img
	 */
	public void setCursor(Image img) {
		this.cursor = new ImageCursor(img);
	}
	
	public Cursor getCursor() {
		return cursor;
	}

	/**
	 * 是否允许缩放窗体
	 * 
	 * @param canResize
	 */
	public void setCanResize(boolean canResize) {
		this.canResize = canResize;
	}

	public boolean canResize() {
		return canResize;
	}
	
	/**
	 * 是否显示调试信息
	 * 
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}
	
	public boolean isDebug() {
		return isDebug;
	}
	
	/**
	 * 新页面是否在旧页面过渡前插入
	 * 
	 * @param isInBeforeOut 为 true 时旧页面将在新页面过渡前插入
	 */
	public void setInBeforeOut(boolean isInBeforeOut) {
		this.isInBeforeOut = isInBeforeOut;
	}
	
	public boolean isInBeforeOut() {
		return isInBeforeOut;
	}

	/**
	 * 场景底色
	 * 
	 * @param bg
	 */
	public void setBg(Paint bg) {
		this.bg = bg;
	}
	
	public Paint getBg() {
		return bg;
	}
}