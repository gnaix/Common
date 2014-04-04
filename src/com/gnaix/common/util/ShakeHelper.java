package com.gnaix.common.util;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

public class ShakeHelper implements SensorEventListener {
	private static final String TAG = "ShakeHelper";

	private static final int SHAKE_TIMEOUT = 500; // ��Чҡ�����ļ��ʱ��
	private static final int TIME_THRESHOLD = 100; // �жϵ���С���ʱ��
	private static final int FORCE_THRESHOLD = 2000; // �����Чspeed
	private static final int SHAKE_COUNT = 3; // ����������Чҡ���Ĵ���
	private static final int SHAKE_DURATION = 1000; // ����ҡ���¼����ټ��ʱ��Ϊ1��
	
	private Context mContext;
	private SensorManager mSensorMgr;
	private OnShakeListener mShakeListener;

	private long mLastTime; // ��һ�μ�¼���ٶ���Ϣ��ʱ��
	private long mLastForce; // ��һ����Чҡ�������ʱ��
	private long mLastShake; // ��һ��ҡ��������ʱ��
	private int mShakeCount = 0; // ��������ҡ�����ۻ����
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f; // x,y,z ��ļ��ٶ�
    private long[] pattern = new long[]{200, 150, 200, 100};
    private Vibrator vib;
    private boolean mVibratory;

	public ShakeHelper(Context context) {
		this.mContext = context;
		mSensorMgr = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	}

	public interface OnShakeListener {
		public void onShake();
	}
	
	public void setVibratory(boolean vibratory){
	    mVibratory = vibratory;
	    if(vib == null && mVibratory){
	        vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE); 
	    }
	}

	public void setOnShakeListener(OnShakeListener shakeListener) {
		this.mShakeListener = shakeListener;
	}
	
	public void start(){
        if (mSensorMgr != null) {
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        }
	}

	public void stop() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

    @Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		
		long now = System.currentTimeMillis();
		
		// ������Чҡ���������ʱ�䣬���ô���
		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}
		
		// �����жϵ���С���ʱ��Ϊ100����
		if ((now - mLastTime) > TIME_THRESHOLD) {
			long diff = now - mLastTime;
            float speed = Math.abs(event.values[0] + event.values[1] 
					+ event.values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
			
			if (speed > FORCE_THRESHOLD) {
				// ����������������������ϴεĴ���ʱ��֮��ļ��Ҫ����1��
				if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
					mShakeCount = 0;
					mLastShake = now;
					if (mShakeListener != null) {
					    if(mVibratory && vib != null){
					        vib.vibrate(pattern,-1);
					    }
						mShakeListener.onShake();
					}
				}
				mLastForce = now;
			}
			
			mLastTime = now;
			mLastX = event.values[0];
			mLastY = event.values[1];
			mLastZ = event.values[2];
		}
	}
}