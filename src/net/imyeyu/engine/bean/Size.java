package net.imyeyu.engine.bean;

/**
 * 尺寸枚举，只有名称，实际执行由调用决定
 * 
 * @author 夜雨
 * @createdAt 2021-01-10 17:19:20
 *
 */
public enum Size {

	S("S"),
	M("M"),
	L("L"),
	XL("XL"),
	XXL("XXL"),
	XXXL("XXXL");
	
	private String typeName;
	
	private Size(String typeName) {
        this.typeName = typeName;
    }
	
	public static Size fromTypeName(String typeName) {
        for (Size type : Size.values()) {
            if (type.getTypeName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }
	
	public String getTypeName() {
        return this.typeName;
    }
}
