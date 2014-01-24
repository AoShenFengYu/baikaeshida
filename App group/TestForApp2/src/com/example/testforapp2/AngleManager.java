package com.example.testforapp2;

import android.hardware.SensorEvent;

/*
 * �ϥΤ�k
 * new������
 * �bSensorEventListener����
 * onSensorChanged(SensorEvent event)�ح�
 * set value[] and set orientation
 * �M��I�scomputeAngle() ���|�^�Ǩ���*/
public class AngleManager {
	
	private float[] value;
	private int orientation; //�����V
	private float gOfZeroPointOneAngle; //���רC�W��0.1 ��  �N��W�ɦh�֪����O�[�t��
	private float gOfAngle[]; //���O�[�t�� 0 ~ 9.8 ����  �@�@����901�Ӽƭ� ����O�N������
	private float angle; //����
	
	public AngleManager(){
		
		gOfZeroPointOneAngle = (float) (9.8/900);	
		gOfAngle = new float[901];
		gOfAngle[0] = 0;
		
		for(int i=1; i<901; i++)
			gOfAngle[i] = gOfAngle[i-1] + gOfZeroPointOneAngle;
	}
	
	public float computeAngle(){
		
		if(orientation == 0)
			computeAngleOfOrientation0();
		else if(orientation == 1)
			computeAngleOfOrientation1();
		else if(orientation == 2)
			computeAngleOfOrientation2();
		else 
			angle = 0;
		return angle;
		
	}
	
	private void computeAngleOfOrientation0(){
		
		int i=0;
		while(i<900){	
			if( gOfAngle[i] <= value[1] && value[1] < gOfAngle[i+1]){ //�̫�|�� 89.9 ~ 90.0��
				angle = (float) (i*(0.1));
				break;
			}
			else
				i++;
		}
		//�P�_�s���I
		if(value[2] < 0)
			angle = 90;
		else if(value[1] < 0)
			angle = 0;
	}
	
	private void computeAngleOfOrientation1(){
		
		int i=0;
		while(i<900){	
			if( gOfAngle[i] <= value[0] && value[0] < gOfAngle[i+1]){ //�̫�|�� 89.9 ~ 90.0��
				angle = (float) (i*(0.1));
				break;
			}
			else
				i++;
		}
		
		if(value[2] < 0)
			angle = 90;
		else if(value[0] < 0)
			angle = 0;
	}
	
	private void computeAngleOfOrientation2(){

		int i=0;
		while(i<900){	
			if( gOfAngle[i] <= Math.abs(value[0]) && Math.abs(value[0]) < gOfAngle[i+1]){ //�̫�|�� 89.9 ~ 90.0��
				angle = (float) (i*(0.1));
				break;
			}
			else
				i++;
		}
		
		if(value[2] < 0)
			angle = 90;
		else if(value[0] > 0)
			angle = 0;
	}
	
	public void setValue(float[] value){
		this.value = new float[3];
		this.value[0] = value[0];
		this.value[1] = value[1];
		this.value[2] = value[2];
	}
	
	public void setOrientation(int orientation){
		this.orientation = orientation;
	}
}
