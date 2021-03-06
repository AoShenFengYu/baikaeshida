package com.heyyeyaaeyaaaeyaeyaa.furniture_simulation_app.camera.glsurface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLException;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.heyyeyaaeyaaaeyaeyaa.furniture_simulation_app.select_obj_file.ObjDataFileHelper;
import com.heyyeyaaeyaaaeyaeyaa.furnituresimulationapp.R;
import com.test.obj.ObjData;

public class HeyRenderer implements Renderer, OnTouchListener
{
	private Shape[] shapes;
	private Context context;
	
	private int shapeCount = 0;
	private int[] testTexture = {R.raw.simba1,R.raw.simba2};
	private String[] dataFileName;
	private float objInitScale;
	
	/* Rotation values */
	private float xrot;					//X Rotation
	private float yrot;					//Y Rotation
	private float zrot;					//Z Rotation
	private float xmove=0;
	private float ymove=0;
	private float zmove;
	private float currentSize;
	private int lock = 0;
	private int height;
	private int width;
	private Bitmap bitmap;
	private final int ROTATION_VERTICAL = 0;
	private final int ROTATION_UPSIDE_DOWN = 3;
	
	/* 
	 * The initial light values for ambient and diffuse
	 * as well as the light position ( NEW ) 
	 */
	private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};//0.5f, 0.5f, 0.5f, 1.0f
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};//1.0f, 1.0f, 1.0f, 1.0f
	private float[] lightPosition = {0.0f, -100.0f, 0.0f, 1.0f};//0.0f, 0.0f, 2.0f, 1.0f
		
	/* The buffers for our light values ( NEW ) */
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightPositionBuffer;
	
	/* Variables and factor for the input handler */
	private float oldX;
    private float oldY;
    float oldDist=0,newDist=0;
	private final float TOUCH_SCALE = 0.2f;			//Proved to be good for normal rotation
	private boolean requestFlag = false;
	private boolean responseFlag = false;
	private float initSize = -10.0f;
	
	public HeyRenderer(Context context, int height, int width)
	{
		this.context=context;
		this.height = height;
		this.width = width;
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
		
		ObjDataFileHelper odfh = new ObjDataFileHelper();
		
		shapeCount = odfh.getObjDataFileCount();
		dataFileName = odfh.getObjDataFileNameList();
		objInitScale = odfh.getObjInitScale();
		
		shapes = new Shape[shapeCount];
		for(int i=0;i<shapeCount;i++){
			ObjDataLoader loadObjData=new ObjDataLoader(context,dataFileName[i]);
			ObjData objData=loadObjData.getObjData();
			shapes[i]=new Shape(objData.getVertices(),objData.getTextures(),objData.getNormals(),objData.getIndices());
		}
		
	}
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		initSize();
		//And there'll be light!
		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_LIGHTING);
		
		for(int i = 0; i < shapes.length; i++){
			shapes[i].loadTexture(gl, context,testTexture[i]);
		}
		
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		//gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}
	
	public void onDrawFrame(GL10 gl)
	{
		//Clear Screen And Depth Buffer
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();				//Reset The Current Modelview Matrix
		//Drawing
		gl.glTranslatef(0.0f+xmove, 0.0f-ymove, currentSize);	//Move down 1.0 Unit And Into The Screen 7.0
		gl.glScalef(objInitScale-zmove, objInitScale-zmove, objInitScale -zmove);
		//Rotate around the axis based on the rotation matrix (rotation, x, y, z)
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);	//X
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);	//Y
		gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);	//Z

		for(int i=0;i<shapeCount;i++){
			shapes[i].draw(gl);
		}
		if(requestFlag)
		{
			bitmap = createBitmapFromGLSurface(0,0,width,height,gl);
			requestFlag = false;
			responseFlag = true;
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		if(height == 0) //Prevent A Divide By Zero By
		{ 						
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix

	}
	float tempMovex = 0;
    float tempMovey = 0;
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		float x = event.getX(0);
        float y = event.getY(0);
        int pointerCount = event.getPointerCount();
        if(pointerCount==2)
        	newDist = spacing(event);
        switch(event.getAction() ) 
        {
        	case MotionEvent.ACTION_DOWN:
        		tempMovex = event.getX(0);
        		tempMovey = event.getY(0);
        		break;
        	case MotionEvent.ACTION_MOVE:
        		float dx = x - oldX;
    	        float dy = y - oldY;
        		switch(pointerCount)
        		{
        			case 1:
        				if(lock<1){
        					lock = 0;
        					xrot += dy * TOUCH_SCALE;
        					yrot += dx * TOUCH_SCALE;
        					Log.e("rotation",xrot+"");
        				}
        				break;
        			case 2:    
        				if(lock<2){
        					lock = 1;
        					zmove -=(newDist-oldDist)*0.0001;
        					zmove = Math.min(0.02f, zmove);
        				}
        				break;
        			case 3:
        				if(lock<3){
        					lock = 2;
        					xmove += dx * TOUCH_SCALE * 0.016;
        					ymove += dy * TOUCH_SCALE * 0.016;
        				}
        				break;
        		}
        		break;
        	case MotionEvent.ACTION_UP:
        		lock = 0;
        		tempMovex = event.getX(0) - tempMovex;
        		tempMovey = event.getY(0) - tempMovey;
        		Log.i("tempMovex", "tempMovex====="+tempMovex);
        		Log.i("tempMovey", "tempMovey====="+tempMovey);
        		break;
        }
        if(event.getPointerCount()==2)
        	oldDist = spacing(event);
        oldX = x;
        oldY = y;
		return true;
	}
	private float spacing(MotionEvent event) { 
		float x = event.getX(0) - event.getX(1); 
		float y = event.getY(0) - event.getY(1); 
		return (float) Math.sqrt(x * x + y * y); 
	}
	public Bitmap getBitmap(){
		return bitmap;
	}
	public void snedBitmapResquest(){
		bitmap = null;
		requestFlag = true;
	}
	public boolean getResponseFlag(){
		return responseFlag;
	}
	public void setResponseFlag(boolean responseFlag){
		this.responseFlag = responseFlag;
	}
	private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) throws OutOfMemoryError {
	    int bitmapBuffer[] = new int[w * h];
	    int bitmapSource[] = new int[w * h];
	    IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
	    intBuffer.position(0);

	    try {
	    	//gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
	        gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
	        int offset1, offset2;
	        for (int i = 0; i < h; i++) {
	            offset1 = i * w;
	            offset2 = (h - i - 1) * w;
	            for (int j = 0; j < w; j++) {
	                int texturePixel = bitmapBuffer[offset1 + j];
	                int blue = (texturePixel >> 16) & 0xff;
	                int red = (texturePixel << 16) & 0x00ff0000;
	                int pixel = (texturePixel & 0xff00ff00) | red | blue;
	                bitmapSource[offset2 + j] = pixel;
	            }
	        }
	    } catch (GLException e) {
	        return null;
	    }
	    
	    return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
	}
	public void changeObjSize(double magnification){
		
		initSize();
		if(magnification==0){
			zmove = 0;
		}
		else{
			currentSize = (float) (initSize*magnification);
			//currentSize = Math.max(currentSize, -100.0f);
		}
	}
	public void initSize(){
		currentSize = initSize;
	}
	public void rotateObj(float angleX, float angleY, float angleZ, int rotationState) {
		if(rotationState == ROTATION_VERTICAL){
			xrot = (float) (angleX);
			yrot = (float) (-angleY);
			zrot = (float) (angleZ);
		}
		else if(rotationState == ROTATION_UPSIDE_DOWN){
			xrot = (float) (-angleX);
			yrot = (float) (-angleY);
			zrot = (float) (angleZ);
		}
	}
	public void moveObj(float y, float x, int rotationState) {
		// TODO Auto-generated method stub

		this.ymove = y;
		this.xmove = x;
		
	}
}
