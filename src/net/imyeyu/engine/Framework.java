package net.imyeyu.engine;

import net.imyeyu.engine.core.EngineTimer;
import net.imyeyu.engine.core.YeyuEngine;

/**
 * 游戏框架
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 08:23:45
 *
 */
public class Framework {
	
	public static final String NAME = "Yeyu Game Engine";
	
	public static final String VERSION = "0.1.0 beta";
	
	/** 资源路径 */
	public static final String RES_PATH = "/net/imyeyu/engine/res/";

	/** 主引擎 */
	public static YeyuEngine engine;
	
	/** 主渲染循环 */
	public static EngineTimer timer;
}