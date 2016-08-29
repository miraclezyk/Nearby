package app.yakun.nearby;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;

public class HttpUtil {

	private static final String API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
	private static final String API_KEY = "AIzaSyBhKWB8tbsW6ebN0wijrrmjpa0fXZ6ER70";
	// private static final String LOCATION = "34.0606780,-118.1436890";
	// private static final String RADIUS = "3000";
	// private static final String RANKBY = "distance";
	private static final String MAP_API = "https://maps.googleapis.com/maps/api/directions/json";

	public static void sendHttpRequest(final String place,
			final String location, final int radius,
			final HttpCallbackListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection connection = null;

				try {
					String uri = Uri
							.parse(API)
							.buildUpon()
							.appendQueryParameter("key", API_KEY)
							.appendQueryParameter("location", location)
							.appendQueryParameter("radius",
									String.valueOf(radius * 1500))
							.appendQueryParameter("type", place).build()
							.toString();
					URL url = new URL(uri);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}

			}

		}).start();

	}

	public static String sendMapRequest(final String origin, final String destination) {

		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		try {
			String uri = Uri.parse(MAP_API).buildUpon()
					.appendQueryParameter("origin", origin)
					.appendQueryParameter("destination", destination).build()
					.toString();
			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));	
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return response.toString();

	}

}
