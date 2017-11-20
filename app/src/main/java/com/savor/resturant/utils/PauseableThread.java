package com.savor.resturant.utils;

import android.util.Log;



public class PauseableThread extends Thread {
	private Object mPauseLock;
	private boolean mPauseFlag;

	public PauseableThread() {

		mPauseLock = new Object();
		mPauseFlag = false;
	}

	public void onPause() {
		synchronized (mPauseLock) {
			mPauseFlag = true;
		}
	}

	public void onResume() {
		synchronized (mPauseLock) {
			mPauseFlag = false;
			mPauseLock.notifyAll();
		}
	}

	public void pauseThread() {
		synchronized (mPauseLock) {
			if (mPauseFlag) {
				try {
					//LogUtils.d("PauseableThread", "mPauseLock.wait()前");
					mPauseLock.wait();
					//LogUtils.d("PauseableThread", "mPauseLock.wait()后");
				} catch (Exception e) {
					Log.v("thread", "fails");
				}
			}
		}
	}

}
