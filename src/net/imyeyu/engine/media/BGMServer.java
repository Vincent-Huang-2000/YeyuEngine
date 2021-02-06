package net.imyeyu.engine.media;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * 背景音乐服务。建议使用枚举泛型
 * 
 * @author 夜雨
 * @createdAt 2021-01-24 10:08:12
 *
 * @param <T> 标记类型
 * 
 */
public abstract class BGMServer<T> {
	
	/** 音效注册列表 */
	private Map<T, Media> list = new HashMap<>();
	/** 监听播放 */
	private ObjectProperty<T> bgmProperty = new SimpleObjectProperty<>();
	/** 播放器 */
	private MediaPlayer player;
	
	public BGMServer() {
		bgmProperty.addListener((obs, o, flag) -> {
			if (player != null) {
				player.dispose();
			}
			player = new MediaPlayer(list.get(flag));
			player.setOnEndOfMedia(() -> {
				player.seek(Duration.ZERO);
				player.play();
			});
		});
	}
	
	/**
	 * 添加到列表
	 * 
	 * @param t
	 * @param media
	 */
	public void add(T t, Media media) {
		list.put(t, media);
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
	public Map<T, Media> getList() {
		return list;
	}
	
	/**
	 * 播放器
	 * 
	 * @return
	 */
	public MediaPlayer getPlayer() {
		return player;
	}
	
	/**
	 * 设置音频
	 * 
	 * @param t
	 */
	public void set(T t) {
		bgmProperty.set(t);
	}
	
	/**
	 * 播放
	 * 
	 */
	public void play() {
		if (player != null) {
			player.play();
		}
	}
	
	/**
	 * 暂停
	 * 
	 */
	public void pause() {
		if (player != null) {
			player.pause();
		}
	}
	
	/**
	 * 销毁
	 * 
	 */
	public void shutdown() {
		if (player != null) {
			player.dispose();
			player = null;
		}
	}
}