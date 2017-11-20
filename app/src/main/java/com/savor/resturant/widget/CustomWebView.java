package com.savor.resturant.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.savor.resturant.R;

import java.util.HashMap;

/**
 * 自定义webview加载网页
 * Created by wmm on 16/11/21.
 */
public class CustomWebView extends FrameLayout implements MyWebView.OnScrollBottomListener {
    private MyWebView mWebView;
    private ProgressBar mProgressBar;

    public CustomWebView(Context context) {
        this(context, null);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_webview, this, true);
        mWebView = (MyWebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(VISIBLE);
        mWebView.setWebViewClient(new MyClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.setOnScrollBottomListener(this);
//        mWebView.setLayerType(View.La, null);
//        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        mWebView.setVerticalScrollBarEnabled(true);
//        mWebView.setHorizontalScrollBarEnabled(false);
//        webSettings.setSupportZoom(false);
//        webSettings.setBlockNetworkImage(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webSettings.setPluginState(WebSettings.PluginState.ON);
//        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    /**
     * 滚动监听
     */
    public MyWebView.OnScrollBottomListener mListener;

    public void setOnScrollBottomListener(MyWebView.OnScrollBottomListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onScrollBottom() {
        if(mListener!=null) {
            mListener.onScrollBottom();
        }
    }

    public void resumeTimers() {
        mWebView.resumeTimers();
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if(newProgress >= 80&&mProgressBar.getVisibility() == VISIBLE)
                mProgressBar.setVisibility(GONE);
        }
    }
    private class MyClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            mProgressBar.setVisibility(VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            mWebView.getSettings().setBlockNetworkImage(false);
//            mProgressBar.setVisibility(GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

        }
        //        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
////            view.loadUrl(url);
//            return true;
//        }

//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            view.stopLoading();
//            mProgressBar.setVisibility(GONE);
//            super.onReceivedError(view, errorCode, description, failingUrl);
//        }
    };


    public void loadUrl(String url, HashMap<String, String> headMap) {
        if (!TextUtils.isEmpty(url)) {
            if (headMap != null) {
                mWebView.loadUrl(url, headMap);
            } else {
                mWebView.loadUrl(url);
            }
        }
    }

    public void onDestroy() {
        if(mWebView!=null) {
            mWebView.pauseTimers();
            mWebView.clearCache(true);
            mWebView.destroyDrawingCache();
            mWebView.destroy();
            mWebView = null;
        }
    }

}
