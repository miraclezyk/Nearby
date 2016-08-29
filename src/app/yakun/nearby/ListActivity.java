package app.yakun.nearby;

import java.util.Arrays;
import java.util.List;

import app.yakun.nearby.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ListActivity extends Activity {
	
	public static final int SUCCESS = 1;
	public static final int INTERNET_ERROR = 2;
	
	private String result;
	
	private int radius;
	
	private ArrayAdapter<String> adapter;
	
	private ProgressDialog progressDialog;
	
	private RadioGroup radioGroup;
	
	private RadioButton radioButton;
	
	private Message message;
	
	private LocationManager locationManager;
	
	private String provider;
	
	private String currentLocation="34.0606780,-118.1436890";
	
	private List<String> places;
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			
			currentLocation = location.getLatitude()+","+location.getLongitude();
			
		}
	};
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case SUCCESS:
				DetailActivity.startActivity(ListActivity.this, result, currentLocation);
				break;
			case INTERNET_ERROR:
				progressDialog.dismiss();
				Toast.makeText(ListActivity.this, "Please check the network connection.", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_layout);
		
		places = Arrays.asList("ATM","Bank","Bar","Car_Repair","Church","Gas_Station","Hospital","Library","Park","Post_office","Restaurant","Store");
		
		adapter = new ArrayAdapter<String>(this, R.layout.choose_item, places);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				message = new Message();
				
				progressDialog = new ProgressDialog(ListActivity.this);
				progressDialog.setTitle("Have a good day!");
				progressDialog.setMessage("Loading...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				
				getDistance();
				
				HttpUtil.sendHttpRequest(places.get(arg2).toLowerCase(), currentLocation, radius, new HttpCallbackListener() {
					
					@Override
					public void onFinish(String response) {
						
						result = response;
						message.what = SUCCESS;
						handler.sendMessage(message);
						
					}
					
					@Override
					public void onError(Exception e) {
						
						message.what = INTERNET_ERROR;
						handler.sendMessage(message);
						
					}
				});
			}
		});
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providerList = locationManager.getProviders(true);
		if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
			provider = LocationManager.NETWORK_PROVIDER;
		}else if(providerList.contains(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.GPS_PROVIDER;
		}else{
			Toast.makeText(this, "No location provider to use, location will be set as default.", Toast.LENGTH_SHORT).show();
		}
		locationManager.requestLocationUpdates(provider, 1000L, 500.0f, locationListener);
		currentLocation = locationManager.getLastKnownLocation(provider).getLatitude()+","+locationManager.getLastKnownLocation(provider).getLongitude();
		
		ActivityCollector.addActivity(this);
		
	}
	
	
	
	@Override
	protected void onStop() {
		
		super.onStop();
		if(progressDialog != null){
			progressDialog.dismiss();	
		}
		
	}



	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		if(locationManager != null){
			locationManager.removeUpdates(locationListener);
		}
		ActivityCollector.removeActivity(this);
		
	}
	
	private void getDistance(){
		
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
		radius = radioButton.getText().charAt(0)-48;
		
	}
	
}
