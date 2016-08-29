package app.yakun.nearby;

public interface HttpCallbackListener {

	void onFinish(String response);
	
	void onError(Exception e);
	
}
