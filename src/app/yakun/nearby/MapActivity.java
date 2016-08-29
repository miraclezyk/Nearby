package app.yakun.nearby;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;


public class MapActivity extends Activity {
	
	private static LatLng ORIGIN;
	
	private static LatLng DESTINATION;
	
	private GoogleMap googleMap;
	
	private ProgressDialog progressDialog;
	
	public static void startActivity(Context context, String origin, String destination){
		Intent intent = new Intent(context, MapActivity.class);
		intent.putExtra("origin", origin);
		intent.putExtra("destination", destination);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_layout);
		
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		new getRoute().execute();
		
		ActivityCollector.addActivity(this);
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		ActivityCollector.removeActivity(this);
		
	}



	private class getRoute extends AsyncTask<Void, Void, String>{	    
	    
	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	        progressDialog = new ProgressDialog(MapActivity.this);
	        progressDialog.setMessage("Loading, Please wait...");
	        progressDialog.setCancelable(false);
	        progressDialog.show();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	String[] locationO = getIntent().getStringExtra("origin").split(",");
			String[] locationD = getIntent().getStringExtra("destination").split(",");
			ORIGIN = new LatLng(Double.parseDouble(locationO[0]),Double.parseDouble(locationO[1]));
			DESTINATION = new LatLng(Double.parseDouble(locationD[0]),Double.parseDouble(locationD[1]));
	        return HttpUtil.sendMapRequest(getIntent().getStringExtra("origin"), getIntent().getStringExtra("destination"));
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        progressDialog.hide();        
	        if(result!=null){
	            drawPath(result);
	        }
	    }
	}
	
	public void drawPath(String  result) {

	    try {
	           final JSONObject json = new JSONObject(result);
	           JSONArray routeArray = json.getJSONArray("routes");
	           JSONObject routes = routeArray.getJSONObject(0);
	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	           String encodedString = overviewPolylines.getString("points");
	           List<LatLng> list = decodePoly(encodedString);
	           googleMap.addPolyline(new PolylineOptions()
	                                    .addAll(list)
	                                    .width(12)
	                                    .color(Color.parseColor("#05b1fb"))
	                                    .geodesic(true)
	                    );
	           googleMap.addMarker(new MarkerOptions().position(ORIGIN).title("Your location"));
	           googleMap.addMarker(new MarkerOptions().position(DESTINATION).title("Destination"));
	           googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ORIGIN, 20));
	           googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
	    } 
	    catch (JSONException e) {
	    	e.printStackTrace();
	    }
	}
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}
	
}
