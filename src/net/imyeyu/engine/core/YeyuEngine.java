package net.imyeyu.engine.core;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.imyeyu.engine.Framework;
import net.imyeyu.engine.bean.EngineConfig;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.extend.BorderX;

/**
 * 游戏引擎核心
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 08:26:46
 *
 */
public final class YeyuEngine {

	// 窗体组件
	private Pane debugBox;
	private Label debugInfo;
	private final Stage stage;
	private final Scene scene;
	private final StackPane root;
	// 页面
	private final Map<String, View> views;
	private final ObjectProperty<View> currentView;
	// 页面过渡
	private boolean isInBeforeOUT = false;
	private boolean isInBeforeOUT4Config = false;
	private Timeline tlOUT, tlIN;
	// 会话
	private final Map<String, Dialog<?>> dialogs;
	private final ObjectProperty<Dialog<?>> currentDialog;
	// 游戏主循环
	private int fpsMax = 60, fpsMin = 12;
	private final EngineTimer timer;
	
	// 引擎相关
	private EngineConfig config;
	protected OnInit onInit;
	protected OnLaunch onLaunch;
	protected OnShutdown onShutdown;
	protected OnToggleView onToggleView;
	
	public YeyuEngine(Stage stage) {
		this.stage = stage;
		
		// 调试参数
		debugInfo = new Label();
		debugInfo.setWrapText(true);
		debugInfo.setTextFill(PixelFX.PINK);
		debugInfo.setLayoutY(2);
		debugInfo.setLayoutX(6);
		
		debugBox = new Pane(debugInfo);
		debugBox.setMouseTransparent(true);
		
		// 根布局
		root = new StackPane();
		root.setBackground(Background.EMPTY);
		root.setBorder(new BorderX(PixelFX.LIGHT_GRAY).top().build());
		// 页面
		views = new HashMap<>();
		currentView = new SimpleObjectProperty<>();
		currentView.addListener((obs, viewOld, viewNew) -> {
			if (viewNew != null) {
				// 切换事件
				if (onToggleView != null) {
					onToggleView.handle();
				}
				// 切换页面
				if (viewOld != null && root.getChildren().contains(viewOld)) {
					viewOut(viewOld, viewNew);
				}
				if (viewOld == null) {
					viewIn(viewNew);
				}
				if (debugBox != null) {
					debugBox.toFront();
				}
			}
		});
		// 会话
		dialogs = new HashMap<>();
		currentDialog = new SimpleObjectProperty<>();
		currentDialog.addListener((obs, dialogOld, dialogNew) -> {
			if (dialogNew != null) {
				root.getChildren().add(dialogNew);
				dialogNew.open();
				
				dialogNew.onShow();
				if (debugBox != null) {
					debugBox.toFront();
				}
			}
		});
		// 关闭事件
		stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> {
			if (onShutdown != null && !onShutdown.handle()) {
				event.consume();
			}
		});
		// 帧更新
		timer = new EngineTimer();
		
		scene = new Scene(root);
		stage.setScene(scene);
		
		// 初始化引擎框架
		Framework.engine = this;
		Framework.timer = timer;
	}
	
	/**
	 * 启动引擎
	 * 
	 */
	protected void launch() {
		// 初始化事件
		if (onInit != null) {
			onInit.handle();
		}
		// 页面事件
		for (Map.Entry<String, View> view : views.entrySet()) {
			view.getValue().onLaunch();
		}
		// 会话事件
		for (Map.Entry<String, Dialog<?>> dialog : dialogs.entrySet()) {
			dialog.getValue().onLaunch();
		}
		// 启动帧更新事件
		timer.onStart = () -> {
			// 页面
			for (Map.Entry<String, View> view : views.entrySet()) {
				view.getValue().onStart();
			}
			// 会话
			for (Map.Entry<String, Dialog<?>> dialog : dialogs.entrySet()) {
				dialog.getValue().onStart();
			}
		};
		// 帧更新
		timer.onUpdate = (time) -> {
			if (getCurrentView() != null) {
				// 当前页面帧更新
				getCurrentView().onUpdate(time);
			}
			if (getCurrentDialog() != null) {
				// 会话帧更新
				getCurrentDialog().onUpdate(time);
			}
		};
		// 停止帧更新
		timer.onStop = () -> {
			// 页面
			for (Map.Entry<String, View> view : views.entrySet()) {
				view.getValue().onStop();
			}
			// 会话
			for (Map.Entry<String, Dialog<?>> dialog : dialogs.entrySet()) {
				dialog.getValue().onStop();
			}
		};
		// 失去焦点削弱帧更新
		stage.focusedProperty().addListener((obs, o, isFocused) -> {
			if (isFocused) {
				timer.setFPS(fpsMax);
			} else {
				timer.setFPS(fpsMin);
			}
		});
		timer.start();
		// 启动事件
		if (onLaunch != null) {
			onLaunch.handle();
		}
		stage.show();
		stage.requestFocus();
	}
	
	/**
	 * 关闭引擎
	 * 
	 */
	public void shutdown() {
		if (onShutdown != null && onShutdown.handle()) {
			timer.stop();
			stage.close();
		}
	}
	
	/**
	 * 配置引擎
	 * 
	 * @param config
	 */
	public void setConfig(EngineConfig config) {
		this.config = config;
		this.isInBeforeOUT4Config = config.isInBeforeOut();
		
		fpsMax = config.getFPS();
		fpsMin = config.getFPSMin();
		
		setDebug(config.isDebug());
		
		scene.setFill(config.getBg());
		scene.setCursor(config.getCursor());
		if (!config.getCSS().isEmpty()) {
			scene.getStylesheets().setAll(config.getCSS());
		}
		
		stage.setTitle(config.getTitle());
		stage.setWidth(config.getWidth());
		stage.setHeight(config.getHeight());
		stage.setResizable(config.canResize());
		stage.getIcons().add(config.getIcon());
	}
	
	/**
	 * 是否显示调试信息
	 * 
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		timer.setDebug(isDebug);
		if (isDebug) {
			if (!debugInfo.textProperty().isBound()) {
				debugInfo.textProperty().bind(timer.textProperty());
			}
			if (!root.getChildren().contains(debugBox)) {
				root.getChildren().add(debugBox);
			}
		} else {
			debugInfo.textProperty().unbind();
			if (root.getChildren().contains(debugBox)) {
				root.getChildren().remove(debugBox);
			}
		}
	}
	
	/**
	 * 注册页面
	 * 
	 * @param name 页面标记
	 * @param view 页面
	 */
	public void regView(String name, View view) {
		views.put(name, view);
	}
	
	/**
	 * 移除注册页面，非必要不调用
	 * 
	 * @param name 页面标记
	 */
	public void unregView(String name) {
		if (views.get(name) != null) {
			if (views.get(name) == getCurrentView()) {
				throw new SecurityException("当前页面正在显示，无法移除");
			}
			views.remove(name);
		}
	}
	
	/**
	 * 获取指定页面
	 * 
	 * @param name 页面标记
	 * @return
	 */
	public View getView(String name) {
		return views.get(name);
	}
	
	/**
	 * 获取当前页面
	 * 
	 * @return 当前页面
	 */
	public View getCurrentView() {
		return currentView.get();
	}
	
	/**
	 * 页面切换监听
	 * 
	 * @return
	 */
	public ReadOnlyObjectProperty<View> viewProperty() {
		return currentView;
	}
	
	/**
	 * 切换页面
	 * 
	 * @param name
	 */
	public void gotoView(String name) {
		gotoView(name, isInBeforeOUT4Config);
	}
	
	/**
	 * 页面切换
	 * 
	 * @param name        页面标记
	 * @param inBeforeOut 新页面是否在旧页面过渡前插入
	 */
	public void gotoView(String name, boolean inBeforeOut) {
		View target = views.get(name);
		if (target != null) {
			this.tlIN = target.show;
			this.tlOUT = getCurrentView() == null ? null : getCurrentView().hide;
			this.isInBeforeOUT = inBeforeOut;
			currentView.set(target);
		} else {
			throw new NullPointerException("页面未注册");
		}
	}
	
	/**
	 * 页面进入
	 * 
	 * @param view
	 */
	private void viewIn(View view) {
		if (view != null) {
			if (isInBeforeOUT) {
				root.getChildren().add(0, view);
			} else {
				root.getChildren().add(view);
			}
			if (debugBox != null) {
				debugBox.toFront();
			}
			view.onShow();
			if (tlIN != null) {
				tlIN.play();
			}
		}
	}
	
	/**
	 * 页面退出
	 * 
	 * @param viewOld 旧页面
	 * @param viewNew 新页面
	 */
	private void viewOut(View viewOld, View viewNew) {
		if (tlOUT != null) {
			if (isInBeforeOUT) { // 新页面在旧页面过渡前插入
				viewIn(viewNew);
			}
			tlOUT.setOnFinished(event -> {
				viewOld.onHide();
				root.getChildren().remove(viewOld);
				if (!isInBeforeOUT) {
					viewIn(viewNew);
				}
			});
			tlOUT.play();
		} else {
			root.getChildren().remove(viewOld);
			viewIn(viewNew);
		}
	}
	
	/**
	 * 注册会话
	 * 
	 * @param name   会话名称
	 * @param dialog 
	 */
	public void regDialog(String name, Dialog<?> dialog) {
		dialogs.put(name, dialog);
	}
	
	/**
	 * 移除注册会话，非必要不调用
	 * 
	 * @param type 会话名称
	 */
	public void unregDialog(String type) {
		if (dialogs.get(type) != null) {
			if (dialogs.get(type) == currentDialog.get()) {
				throw new SecurityException("当前会话正在显示，无法移除");
			}
			dialogs.remove(type);
		}
	}
	
	/**
	 * 获取指定会话
	 * 
	 * @param name 会话名称
	 * @return
	 */
	public Dialog<?> getDialog(String name) {
		return dialogs.get(name);
	}
	
	/**
	 * 获取当前会话
	 * 
	 * @return
	 */
	public Dialog<?> getCurrentDialog() {
		return currentDialog.get();
	}
	
	/**
	 * 会话监听
	 * 
	 * @return
	 */
	public ReadOnlyObjectProperty<Dialog<?>> dialogProperty() {
		return currentDialog;
	}
	
	/**
	 * 显示会话
	 * 
	 * @param name 会话名称
	 */
	public void dialogShow(String name) {
		if (dialogs.get(name) != null) {
			Dialog<?> dialog = currentDialog.get();
			if (dialog != null) { // 已存在会话
				dialog.onClose = () -> {
					root.getChildren().remove(dialog);
					dialog.onHide();
					currentDialog.set(dialogs.get(name));
				};
				dialog.close();
			} else {
				currentDialog.set(dialogs.get(name));
			}
		} else {
			throw new NullPointerException(name.toString() + "会话未注册");
		}
	}
	
	/**
	 * 关闭会话
	 * 
	 */
	public void dialogClose() {
		Dialog<?> dialog = currentDialog.get();
		if (dialog != null) {
			dialog.onClose = () -> {
				root.getChildren().remove(dialog);
				dialog.onHide();
				currentDialog.set(null);
			};
			dialog.close();
		} else {
		}
	}
	
	/**
	 * 初始化事件
	 * <br>已有: 窗体、场景、根布局
	 * <br>未有: 页面、帧更新
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-19 01:02:34
	 *
	 */
	protected static interface OnInit {
		void handle();
	}
	
	/**
	 * 启动事件
	 * <br>已有: 窗体、场景、根布局、页面、帧更新
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-17 09:06:12
	 *
	 */
	protected static interface OnLaunch {
		void handle();
	}
	
	/**
	 * 页面切换事件
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-18 19:47:30
	 *
	 */
	protected static interface OnToggleView {
		void handle();
	}
	
	/**
	 * 关闭事件
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-17 09:06:03
	 *
	 */
	protected static interface OnShutdown {
		boolean handle();
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public StackPane getRoot() {
		return root;
	}
	
	public EngineConfig getConfig() {
		return config;
	}
}