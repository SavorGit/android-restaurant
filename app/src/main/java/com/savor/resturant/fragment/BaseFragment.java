package com.savor.resturant.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.common.api.utils.ShowProgressDialog;
import com.savor.resturant.R;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.core.Session;
import com.savor.resturant.interfaces.IBaseView;


/**
 * Fragment基类
 * 
 * @author bc
 * 
 */
public abstract class BaseFragment extends Fragment implements ApiRequestListener ,IBaseView{
	Session mSession;
//	protected PictureUtils bitmapUtils;
//	protected BitmapDisplayConfig config;
	
	protected FrameLayout backFL;
	protected ImageView backIV;
	protected TextView backTV;
	protected FrameLayout nextFL;
	protected ImageView nextIV;
	protected TextView nextTV;
	protected TextView titleTV;

	protected Activity mActivity;
	private ProgressDialog mProgressDialog;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtils.i(getFragmentName() + " onAttach()");
		mActivity = activity;
		mSession = Session.get(activity);
	}
	protected void initTitleView(){
//		View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//		backFL = (FrameLayout) rootView.findViewById(R.id.backFL);
//		backIV = (ImageView) rootView.findViewById(R.id.backIV);
//		backTV = (TextView) rootView.findViewById(R.id.backTV);
//		nextFL = (FrameLayout) rootView.findViewById(R.id.nextFL);
//		nextIV = (ImageView) rootView.findViewById(R.id.nextIV);
//		nextTV = (TextView) rootView.findViewById(R.id.nextTV);
//		titleTV = (TextView) rootView.findViewById(R.id.titleTV);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.i(getFragmentName() + " onCreate()");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		LogUtils.i(getFragmentName() + " onCreateView()");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LogUtils.i(getFragmentName() + " onViewCreated()");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtils.i(getFragmentName() + " onActivityCreated()");
	}

	@Override
	public void onStart() {
		super.onStart();
		LogUtils.i(getFragmentName() + " onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils.i(getFragmentName() + " onResume()");
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtils.i(getFragmentName() + " onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtils.i(getFragmentName() + " onStop()");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtils.i(getFragmentName() + " onDestroyView()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.i(getFragmentName() + " onDestroy()");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		LogUtils.i(getFragmentName() + " onDetach()");
	}

	protected void initBitmapUtils() {
//		bitmapUtils = PictureUtils.getInstance(getActivity());
//		config = new BitmapDisplayConfig();
//		config.setLoadingDrawable(getActivity().getResources().getDrawable(
//				R.drawable.ic_empty));
//		config.setLoadFailedDrawable(getActivity().getResources().getDrawable(
//				R.drawable.ic_empty));
	}
	
	/**
	 * fragment name
	 */
	public abstract String getFragmentName();

	
	@Override
	public void onError(AppApi.Action method, Object statusCode) {

		if(statusCode instanceof ResponseErrorMessage) {
			ResponseErrorMessage msg = (ResponseErrorMessage) statusCode;
			int code = msg.getCode();
			String message = msg.getMessage();
			showToast(message);
//			if(1006==code||12056==code) {
//				ActivityPagerUtils.launchLoginActivity(getActivity());
//
//				mSession.signOut();
//
//				Intent intent = new Intent(getActivity().getApplicationContext(), RegisterGCMSNSService.class);
//				intent.setAction("com.etago.life.pushgcm");
//				getActivity().getApplicationContext().startService(intent);
//			}
//			if(AppApi.HTTP_RESPONSE_NEED_LOGIN==code) {
//				ActivitiesManager.getInstance().popAllActivities();
////				mSession.clearUserInfo();
////				Intent intent = new Intent(getActivity(),LoginActivity.class);
////				intent.putExtra("op_type",AppApi.HTTP_RESPONSE_NEED_LOGIN );
////				startActivity(intent);
//			}
		}
	}

	@Override
	public void onSuccess(AppApi.Action method, Object obj) {

	}

	@Override
	public void onNetworkFailed(AppApi.Action method) {

	}

	protected void showErrorToast(Object obj, String defaultMsg) {
		if (obj instanceof ResponseErrorMessage) {
			ResponseErrorMessage errorMessage = (ResponseErrorMessage) obj;
			if (!TextUtils.isEmpty(errorMessage.getMessage())) {
				defaultMsg = errorMessage.getMessage();
			}
		}
		showToast(defaultMsg);
	}

	public void showToast(String msg) {
		ShowMessage.showToastSavor(getActivity(),msg);
	}

	@Override
	public void showLoadingLayout() {
		if(mProgressDialog == null)
			mProgressDialog = ShowProgressDialog.showProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, getString(R.string.loading_hint));
	}

	@Override
	public void hideLoadingLayout() {
		if(mProgressDialog!=null&&mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	public void getViews() {

	}

}
