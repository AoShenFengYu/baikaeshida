package com.android.opgl.test;


import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class Run extends Activity
{
	private GLSurfaceView glSurface;
	//private GLSurfaceView glSurface2;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		glSurface=new GLSurfaceView(this);
		//glSurface=(GLSurfaceView)this.findViewById(R.id.a);
		Test03 test03=new Test03(glSurface.getContext());
		glSurface.setRenderer(test03);
		glSurface.setOnTouchListener(test03);
		addContentView(glSurface, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(glSurface);
		setContentView(R.layout.main);
	}
}