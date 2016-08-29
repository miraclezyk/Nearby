package app.yakun.nearby;

import java.util.List;

import app.yakun.nearby.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlaceAdapter extends ArrayAdapter<Place> {
	
	private int resourceId;

	public PlaceAdapter(Context context, int resource, List<Place> objects) {
		
		super(context, resource, objects);
		resourceId = resource;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Place place = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.placeName = (TextView) view.findViewById(R.id.name);
			viewHolder.placeAddress = (TextView) view.findViewById(R.id.address);
			viewHolder.status = (TextView) view.findViewById(R.id.status);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.placeName.setText(place.getName());
		viewHolder.placeAddress.setText(place.getAddress());
		if(place.getStatus()){
			viewHolder.status.setText("OPEN");
			viewHolder.status.setBackgroundColor(Color.GREEN);
			viewHolder.status.setTextColor(Color.WHITE);
		}else{
			viewHolder.status.setText("CLOSED");
			viewHolder.status.setBackgroundColor(Color.RED);
			viewHolder.status.setTextColor(Color.WHITE);
		}
		
		return view;
	}
	
	class ViewHolder{
		TextView placeName;
		TextView placeAddress;
		TextView status;
	}

}
