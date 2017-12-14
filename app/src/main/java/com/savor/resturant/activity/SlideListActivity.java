package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.adapter.SlideListAdapter;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.utils.IntentUtil;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.ToastUtil;
import com.savor.resturant.widget.CommonDialog;
import com.savor.resturant.widget.InputAlertDialog;

import java.util.ArrayList;

import static com.savor.resturant.utils.IntentUtil.KEY_SLIDE;
import static com.savor.resturant.utils.IntentUtil.KEY_TYPE;
import static com.savor.resturant.utils.IntentUtil.MEDIA_TYPE;

/**
 * 幻灯片集列表界面
 * 每次进入页面oncreate onrestart刷新当前页面状态 被删除的图片会被去除
 */
public class SlideListActivity extends BaseActivity implements View.OnClickListener,
        SlideListAdapter.OnItemLongClickListener,
        SlideListAdapter.OnItemSlideClickListener {

    private ImageView back;
    private TextView title;
    private ImageView add;
    private LinearLayout ll_create;
    private Button create;
    private ArrayList<SlideSetInfo> slideList = new ArrayList<SlideSetInfo>();
    private PullToRefreshListView mPullTorefreshListView;
    private ListView mListView;
    private SlideListActivity mContext;
    private SlideListAdapter mAdapter;
    private View mFooterView;
    private Handler mHandler = new Handler();
    private SlideManager.SlideType slideType;
    private TextView mHintTv;
    private TextView mFootHintTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        mContext = SlideListActivity.this;

        handleIntent();
        getViews();
        setViews();
        setListeners();
    }

    private void handleIntent() {
        slideType = (SlideManager.SlideType) getIntent().getSerializableExtra("type");
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStart(this, getString(R.string.slide_to_screen_list));
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.btn_create:
                //createDialog();
                RecordUtils.onEvent(this, getString(R.string.slide_to_screen_creat));
                createSlideLantern();
                break;
        }
    }

    /**
     * 关联控件
     */
    @Override
    public void getViews() {
        mHintTv = (TextView) findViewById(R.id.tv_hint);
        back = (ImageView) findViewById(R.id.iv_left);
        title = (TextView) findViewById(R.id.tv_center);
        add = (ImageView) findViewById(R.id.iv_right);
        ll_create = (LinearLayout) findViewById(R.id.ll_create_first);
        create = (Button) findViewById(R.id.btn_create);
        mPullTorefreshListView = (PullToRefreshListView) findViewById(R.id.main_list);
        mListView = mPullTorefreshListView.getRefreshableView();
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.slide_footer_layout, null);
        mFootHintTv = (TextView) mFooterView.findViewById(R.id.tv_load_hint);
    }

    /**
     * 初始化控件
     */
    @Override
    public void setViews() {
        switch (slideType) {
            case IMAGE:
                mFootHintTv.setText("最多可以创建50个幻灯片");
                title.setText("图片与幻灯片");
                break;
            case VIDEO:
                title.setText("视频列表");
                mFootHintTv.setText("最多可以创建50组视频列表");
                break;
        }
        add.setImageResource(R.drawable.tianjia_w);
        mPullTorefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mAdapter = new SlideListAdapter(mContext,this);
        mPullTorefreshListView.setAdapter(mAdapter);
        initSlide();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initSlide();
        final Intent intent = getIntent();
        if(intent!=null) {
            int type = intent.getIntExtra(KEY_TYPE, 0);
            if(type == IntentUtil.TYPE_SLIDE_BY_LIST) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SlideSetInfo info = (SlideSetInfo) intent.getSerializableExtra(KEY_SLIDE);
                        Intent intent = new Intent(SlideListActivity.this, SlideDetailActivity.class);
                        intent.putExtra(KEY_TYPE, IntentUtil.TYPE_SLIDE_BY_LIST);
                        intent.putExtra(KEY_SLIDE, info);
                        intent.putExtra("type",slideType);
                        startActivity(intent);
//                        IntentUtil.openActivity(mContext, SlideDetailActivity.class, IntentUtil.TYPE_SLIDE_BY_LIST, info);
                        setIntent(null);
                    }
                },100);

            }
        }
    }

    /**
     * 设置监听事件
     */
    @Override
    public void setListeners() {
        back.setOnClickListener(this);
        add.setOnClickListener(this);
        create.setOnClickListener(this);;
        mAdapter.setOnItemLongClickListener(this);
    }

    /**
     * 扫描手机幻灯片文件中幻灯片列表信息
     */
    private void initSlide() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showToastSavor(this, getString(R.string.null_storage));
            return;
        }
        //获取幻灯片列表
       // SlideManager.getInstance().readFile();
        slideList = SlideManager.getInstance(slideType).getData();
        if (slideList == null || slideList.size() == 0) {
            ll_create.setVisibility(View.VISIBLE);
            mPullTorefreshListView.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            switch (slideType) {
                case IMAGE:
                    mHintTv.setText("去创建您的第一个幻灯片吧");
                    break;
                case VIDEO:
                    mHintTv.setText("去创建您的第一个视频列表吧~");
                    break;
            }
        } else {
            mPullTorefreshListView.getRefreshableView().removeFooterView(mFooterView);
            ll_create.setVisibility(View.GONE);
            mPullTorefreshListView.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            mAdapter.setData(slideList, true);
            mPullTorefreshListView.getRefreshableView().addFooterView(mFooterView,null,false);
        }
    }

    /**
     * 新建幻灯片
     *
     *
     */
    private void createSlideLantern() {
        // 新建幻灯片信息
        SlideSetInfo slideSetInfo = new SlideSetInfo();
        slideSetInfo.isNewCreate = true;
       // slideSetInfo.groupName = filename;
        slideSetInfo.updateTime = System.currentTimeMillis();
        //判断幻灯片是否已存在
        int size = SlideManager.getInstance(slideType).getSize();
//       boolean isExist = SlideManager.getInstance().containGroup(slideSetInfo);
        if (size >= 50) {
            ShowMessage.showToast(SlideListActivity.this, getString(R.string.create_more));
            return;
        }
        //第一次创建，直接进入相册列表页，选择图片
//        IntentUtil.openActivity(SlideListActivity.this, PhotoActivity.class, IntentUtil.TYPE_SLIDE_BY_LIST, slideSetInfo);
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(KEY_TYPE, IntentUtil.TYPE_SLIDE_BY_LIST);
        intent.putExtra(KEY_SLIDE, slideSetInfo);
        intent.putExtra(MEDIA_TYPE,slideType);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(final SlideSetInfo info) {
        String hint = "";
        switch (slideType) {
            case VIDEO:
                hint = "是否删除该视频列表？";
                break;
            case IMAGE:
                hint = "是否删除幻灯片？";
                break;
        }
        new CommonDialog(this, hint, new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                //删除幻灯片，存储数据
                SlideManager.getInstance(slideType).removeGroup(info);
                SlideManager.getInstance(slideType).saveSlide();
                initSlide();
                ShowMessage.showToast(SlideListActivity.this,"删除成功");
            }
        }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
            }
        },"删除").show();
//        mAdapter.remove(info);
    }

    @Override
    public void onItemClick(SlideSetInfo info) {

        Intent intent = new Intent(this,SlideDetailActivity.class);
        intent.putExtra(KEY_TYPE, IntentUtil.TYPE_SLIDE_BY_LIST);
        intent.putExtra(KEY_SLIDE, info);
        intent.putExtra("type",slideType);
        startActivity(intent);
//        IntentUtil.openActivity(mContext, SlideDetailActivity.class, IntentUtil.TYPE_SLIDE_BY_LIST, info);
    }
}
