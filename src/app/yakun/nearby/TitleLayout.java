package app.yakun.nearby;

import app.yakun.nearby.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class TitleLayout extends LinearLayout implements OnClickListener{
	
	private Button back;
	private Button close;

	public TitleLayout(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.title, this);
		back = (Button) findViewById(R.id.title_back);
		close = (Button) findViewById(R.id.title_close);
		back.setOnClickListener(this);
		close.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.title_back:
			((Activity)getContext()).finish();
			break;
		case R.id.title_close:
			ActivityCollector.finishAll();
			break;
		default:
			break;
		}
		
	}

}
