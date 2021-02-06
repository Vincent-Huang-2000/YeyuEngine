package net.imyeyu.engine.media;

import java.applet.AudioClip;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * 音效服务。建议使用枚举泛型
 * 
 * @author 夜雨
 * @createdAt 2021-01-18 23:59:19
 *
 * @param <T> 标记类型
 */
public abstract class SoundServer<T> extends Service<Boolean> {
	
	/** 音效列表 */
	private Map<T, AudioClip> list = new HashMap<>();
	/** 监听播放 */
	private ObjectProperty<T> soundProperty = new SimpleObjectProperty<>();

	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			protected Boolean call() throws Exception {
				soundProperty.addListener((obs, o, type) -> {
					if (type != null) {
						list.get(type).stop();
						list.get(type).play();
						soundProperty.set(null);
					}
				});
				return null;
			}
		};
	}
	
	/**
	 * 播放
	 * 
	 * @param t
	 */
	public void set(T t) {
		soundProperty.set(t);
	}
	
	/**
	 * 添加到列表
	 * 
	 * @param t
	 * @param ac
	 */
	public void add(T t, AudioClip ac) {
		list.put(t, ac);
	}
	
	/**
	 * 从列表移除（非必要）
	 * 
	 * @param t
	 */
	public void remove(T t) {
		list.remove(t);
	}
	
	/**
	 * 获取列表
	 * 
	 * @return
	 */
	public Map<T, AudioClip> getList() {
		return list;
	}
}