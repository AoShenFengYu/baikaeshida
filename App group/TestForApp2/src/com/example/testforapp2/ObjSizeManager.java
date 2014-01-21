package com.example.testforapp2;

import android.util.Log;



public class ObjSizeManager {


	private float focusLength;  //�J�Z
	private int foot;  //�}�B��
	private float u, v;   //���Z ���Z �Y�p����
	private double magnification;
	private double fix = 0.01*3.14;
	private HeyRenderer heyRenderer;
	
	public ObjSizeManager(HeyRenderer heyRenderer, float focalLength, int foot){
		this.heyRenderer = heyRenderer;
		this.focusLength = focalLength;
		this.foot = foot;
	}

	public void setFocalLength(float focalLength){
		this.focusLength = focalLength;
	}
	
	public void setFoot(int foot){
		this.foot = foot;
	}
	
	public double getMagnification(){
		u = foot * 500;//50cm
		
		if(u==0)return 0;
		
		Log.e("Log","foot:"+foot);
		Log.e("Log","u:"+u);
		v = 1/(1/focusLength - 1/u);
		magnification = u/v*fix;
		
		return magnification;
	}

	public void changeObjSize(double magnification) {
		heyRenderer.changeObjSize(magnification);
	}
	public void initSize(){
		heyRenderer.initSize();
	}
}
