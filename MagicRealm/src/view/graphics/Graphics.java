package view.graphics;


public interface Graphics {
	
	void start();
	
	void stop();
	
	void addDrawable(Drawable dr);
	
	void addAllDrawables(Iterable<? extends Drawable> dr);

}
