package net.imyeyu.engine.bean;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 默认过渡
 * 
 * @author 夜雨
 * @createdAt 2021-01-19 00:08:07
 *
 */
public enum Transition {

	OPACITY_TO_1("OPACITY_TO_1"),
	OPACITY_TO_0("OPACITY_TO_0");

	private String typeName;
	
	private Transition(String typeName) {
        this.typeName = typeName;
    }
	
	public static Transition fromTypeName(String typeName) {
        for (Transition item : Transition.values()) {
            if (item.getTypeName().equals(typeName)) {
                return item;
            }
        }
        return null;
    }
	
	public static List<KeyFrame> toKeyFrames(Node node, Transition typeName, long ms) {
		List<KeyFrame> l = new ArrayList<>();
		switch (typeName) {
			case OPACITY_TO_1:
				node.setOpacity(0);
				l.add(new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 0)));
				l.add(new KeyFrame(Duration.millis(ms), new KeyValue(node.opacityProperty(), 1)));
				break;
			case OPACITY_TO_0:
				node.setOpacity(1);
				l.add(new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 1)));
				l.add(new KeyFrame(Duration.millis(ms), new KeyValue(node.opacityProperty(), 0)));
				break;
		}
		return l;
	}
	
	public String getTypeName() {
        return this.typeName;
    }
}