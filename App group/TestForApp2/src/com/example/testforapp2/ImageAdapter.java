package com.example.testforapp2;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	
	public Integer[] imgs = {R.drawable.img1 , R.drawable.img2 , R.drawable.img3 ,
			R.drawable.img4 , R.drawable.img5};
	
	public ImageAdapter(Context c){
		mContext = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
		//return imgs.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imgs[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageview = new ImageView(mContext);  
		imageview.setImageResource(imgs[position % imgs.length]);
        imageview.setLayoutParams(new Gallery.LayoutParams(300, 300));
        imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageview.setBackgroundColor(Color.alpha(1));
        return imageview;
	}

}