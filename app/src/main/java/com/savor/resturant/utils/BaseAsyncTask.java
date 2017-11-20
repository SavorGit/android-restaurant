package com.savor.resturant.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.BaseProResponse;
import com.savor.resturant.interfaces.OnBaseListenner;


public abstract class BaseAsyncTask<T1, T2, T3 extends BaseProResponse> extends
		AsyncTask<T1, T2, T3> {
	protected String TAG = getClass().getSimpleName();
	// protected Handler actionHandler = null;
	protected Context context;
	protected SavorApplication app = null;
	protected OnBaseListenner listener;

	// protected abstract void shareHandler(Context context, Handler mHandler);
	public BaseAsyncTask(Context context, OnBaseListenner listener) {
		// this.actionHandler = actionHandler;
		this.listener = listener;
		this.context = context;
		app = (SavorApplication) context.getApplicationContext();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// actionHandler.sendEmptyMessage(ConstantsWhat.SHOW_DIALOG);
	}

	@Override
	protected void onPostExecute(T3 result) {
		super.onPostExecute(result);
		// switchEmptyMessage(ConstantsWhat.KILL_DIALOG);
		if (result == null) {
			// listener.resultNull();
			return;
		}
		// 心跳倒计时归零

//		LogUtils.d(TAG, result.getClass().getSimpleName() + "\nresult = "
//				+ result);
		if (result.getResult() == 0) {
			doSuccess(result);
		} else {
			doFailed(result);
		}
	}

	protected abstract void doSuccess(T3 result);

	protected abstract void doFailed(T3 result);

	@Override
	protected void onProgressUpdate(T2... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected T3 doInBackground(T1... params) {
		return null;
	}

	// protected void switchMessage(int what, String msg) {
	// Message message = Message.obtain();
	// message.what = what;
	// message.obj = msg;
	// actionHandler.sendMessage(message);
	// }

	// protected void switchEmptyMessage(int what) {
	// actionHandler.sendEmptyMessage(what);
	// }

	/**
	 * @param action
	 *            {@link ConstantsWhat.ActionCode}
	 * @param result
	 *            {@link T extends BaseProResponse}
	 */
	// protected void switchActionMessage(int action, Object result) {
	// Message message = Message.obtain();
	// message.what = action;
	// message.obj = result;
	// actionHandler.sendMessage(message);
	// }

}
