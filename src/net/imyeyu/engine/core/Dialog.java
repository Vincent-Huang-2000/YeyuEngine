package net.imyeyu.engine.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.imyeyu.engine.bean.BezierPoint;
import net.imyeyu.engine.bean.Size;
import net.imyeyu.engine.utils.CubicBezier;
import net.imyeyu.px.bean.TipsLevel;
import net.imyeyu.px.extend.BgFill;
import net.imyeyu.px.extend.BorderX;

/**
 * 会话窗体
 * <p>
 * 本类仅作为会话弹窗的容器，覆盖整个页面，实际弹窗容器是 dialog 对象，不建议
 * 修改本类的属性，而是针对 dialog 自定义
 * <br>
 * PS: 可以设置本类暗色背景来突出弹窗容器
 * </p>
 * 
 * @author 夜雨
 * @createdAt 2021-01-22 08:37:21
 *
 * @param <T> 弹窗容器类型
 */
public abstract class Dialog<T extends Pane> extends View {

	/** 成功操作图标 */
	protected static final Image SUCCESS = TipsLevel.toImg(TipsLevel.SUCCESS);
	/** 警告操作图标 */
	protected static final Image WARNING = TipsLevel.toImg(TipsLevel.WARNING);
	/** 错误操作图标 */
	protected static final Image ERROR = TipsLevel.toImg(TipsLevel.ERROR);

	/** 会话窗体对象 */
	protected T dialog;
	
	private List<BezierPoint> bps = new CubicBezier(.08, .82, .17, 1).precision(12).build();
	
	protected OnClose onClose;

	@SuppressWarnings("unchecked")
	public Dialog() {
		try {
			// 实例化泛型
			Class<?> parent = this.getClass();
			ParameterizedType type = null;
			if (parent.getGenericSuperclass() instanceof ParameterizedType) {
				type = (ParameterizedType) parent.getGenericSuperclass();
			} else {
				// 向上查找
				for (Type t = null; parent != Object.class; t = parent.getGenericSuperclass()) {
					if (t instanceof ParameterizedType) {
						type = (ParameterizedType) t;
						break;
					} else {
						parent = parent.getSuperclass();
					}
				}
			}
			if (type != null) {
				Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
				dialog = clazz.newInstance();
				
				DropShadow shadow = new DropShadow();
				shadow.setRadius(0);
				shadow.setOffsetX(2);
				shadow.setOffsetY(2);
				shadow.setSpread(1);
				shadow.setColor(Color.valueOf("#000A"));
				

				dialog.setPadding(new Insets(8));
				dialog.setBackground(new BgFill("#DFECFA").build());
				dialog.setBorder(new BorderX("#CDDEF0").width(2).build());
				dialog.setEffect(shadow);
				dialog.setTranslateY(-60);
				
				getChildren().add(dialog);
			} else {
				throw new GeneralSecurityException("泛型布局异常");
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void setSize(Size size) {
		switch (size) {
			case S:    dialog.setMaxSize(380, 240); break;
			case M:    dialog.setMaxSize(480, 300); break;
			case L:    dialog.setMaxSize(520, 360); break;
			case XL:   dialog.setMaxSize(640, 410); break;
			case XXL:  dialog.setMaxSize(760, 520); break;
			case XXXL: dialog.setMaxSize(860, 570); break;
		}
	}
	
	protected final void open() {
		frame = 0;
		isOpening = true;
	}
	
	private int frame = 0;
	private double y = 0;
	private boolean isOpening = false, isClosing = false;
	protected void onUpdate(double time) {
		if (isOpening) {
			if (frame < bps.size()) {
				y = bps.get(frame).y;
				dialog.setScaleX(.4 + .6 * y);
				dialog.setScaleY(.4 + .6 * y);
				dialog.setOpacity(y);
				frame++;
			} else {
				isOpening = false;
			}
		}
		if (isClosing) {
			if (frame < bps.size()) {
				y = bps.get(frame).y;
				dialog.setScaleX(1 - .6 * y);
				dialog.setScaleY(1 - .6 * y);
				dialog.setOpacity(1 - y);
				frame++;
			} else {
				if (onClose != null) {
					onClose.handle();
				}
				isClosing = false;
			}
		}
	}
	
	protected final void close() {
		frame = 0;
		isClosing = true;
	}
	
	protected static interface OnClose {
		void handle();
	}
}