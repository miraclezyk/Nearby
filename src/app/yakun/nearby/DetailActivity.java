package app.yakun.nearby;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.yakun.nearby.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DetailActivity extends Activity {
	
	private List<Place> placeList = new ArrayList<Place>();
	
	private ProgressDialog progressDialog;

	public static void startActivity(Context context, String result, String origin){
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("result", result);
		intent.putExtra("origin", origin);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_layout);
		
		initPlaces();
		
		if(placeList.size() == 0){
			Toast.makeText(this, "No places found...", Toast.LENGTH_SHORT).show();
		}
		PlaceAdapter adapter = new PlaceAdapter(this, R.layout.place_item, placeList);
		ListView listView = (ListView) findViewById(R.id.list_place);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				progressDialog = new ProgressDialog(DetailActivity.this);
		        progressDialog.setMessage("Loading, Please wait...");
		        progressDialog.setCancelable(false);
		        progressDialog.show();
				MapActivity.startActivity(DetailActivity.this, getIntent().getStringExtra("origin"), placeList.get(arg2).getLatLon());
			}
		});
		
		ActivityCollector.addActivity(this);
		
	}
	
	private void initPlaces(){
		
		String text = getIntent().getStringExtra("result");
		
		try {
			JSONArray jsonArray = new JSONObject(text).getJSONArray("results");
			for(int i=0; i<jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if(jsonObject.isNull("opening_hours"))
					continue;
				Boolean isOpen = jsonObject.getJSONObject("opening_hours").getBoolean("open_now");
				JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
				placeList.add(new Place(jsonObject.getString("name"), jsonObject.getString("vicinity"), location.getString("lat")+","+location.getString("lng"), isOpen));
			}
		} catch (JSONException e) {
			Log.d("JSON", e.toString());
		}
		
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
		ActivityCollector.removeActivity(this);
		
	}

}
