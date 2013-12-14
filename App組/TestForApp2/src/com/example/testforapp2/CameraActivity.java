package com.example.testforapp2;

//import com.example.dragimagedemo.R;

//import com.example.dragimagedemo.ImageViewHelper;

import com.android.opgl.test.MyGLSurfaceView;
import com.android.opgl.test.Test03;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private Button buttonBack, buttonCapture;
	private FrameLayout frameLayout;
	private Camera camera;
	private CameraPreview preview;
	private Singleton singleton;
	
	private DisplayMetrics dm; //螢幕
	private Bitmap bitmap;//set Image
	//private ImageView imageView;
	private MyGLSurfaceView glSurface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera); // 一定要放在第一句 不然會出錯
		
		//在當前的activity中註冊廣播
		IntentFilter filter = new IntentFilter();  
		filter.addAction(MainActivity.BORADCAST_ACTION_EXIT);//爲BroadcastReceiver指定一個action，即要監聽的消息名字  
		registerReceiver(mBoradcastReceiver,filter); //動態註冊監聽  靜態的話 在AndroidManifest.xml中定義
		
		
		buttonBack = (Button)this.findViewById(R.id.camera_button_back);
		buttonCapture = (Button)this.findViewById(R.id.camera_button_capture);
		frameLayout = (FrameLayout)this.findViewById(R.id.camera_frameLayout);
		camera = getCameraInstance();
		preview = new CameraPreview(this, camera);
		dm = new DisplayMetrics();
		//imageView = (ImageView)this.findViewById(R.id.camera_imageView);
		
		frameLayout.addView(preview);
		
		buttonBack.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(CameraActivity.this, ChooseObjActivity.class);
				startActivity(intent);
			}});
		buttonCapture.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*拍照*/
			}});
		
		getWindowManager().getDefaultDisplay().getMetrics(dm); //取得螢幕資訊
		
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chrome);
		//imageView.setImageBitmap(bitmap);
		//new ImageViewHelper(dm,imageView,bitmap);
		Toast.makeText(this.getApplicationContext(), "手機螢幕 高度:"+dm.heightPixels+" 寬度:"+dm.widthPixels, 1000).show();
		
		
		
		/*objLoader 整合在app上****************************************/	
		//glSurface=new GLSurfaceView(this);
		glSurface=(MyGLSurfaceView)this.findViewById(R.id.myGLSurfaceView);		
		Test03 test03=new Test03(glSurface.getContext());
		glSurface.setZOrderOnTop(true);
		glSurface.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		//glSurface.getHolder().setFormat(PixelFormat.RGBA_8888);
		glSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		//glSurface.setAlpha(0);
		glSurface.setRenderer(test03);
		glSurface.setOnTouchListener(test03);
		//glSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		//addContentView(glSurface, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//setContentView(glSurface);
		
		/*objLoader 整合在app上*****************************************/
	}

	private BroadcastReceiver mBoradcastReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(MainActivity.BORADCAST_ACTION_EXIT)){//發來關閉action的廣播  
                finish();  
            }  
        }  
    }; 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	
	private Camera getCameraInstance() {
	    Camera camera = null;
	    try {
	        camera = Camera.open();
	    } catch (Exception e) {
	        // cannot get camera or does not exist
	    }
	    return camera;
	  }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this.getApplicationContext(), "CameraDestroy", 1000).show();
		unregisterReceiver(mBoradcastReceiver); //取消監聽
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
