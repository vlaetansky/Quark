package vazkii.quark.api.config;

public interface IConfigObject<T> extends IConfigElement {

	public T getCurrentObj();
	public void setCurrentObj(T obj);
	
}
