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
        back = (ImageView) findViewById(R.id.iv_left);
        title = (TextView) findViewById(R.id.tv_center);
        add = (ImageView) findViewById(R.id.iv_right);
        ll_create = (LinearLayout) findViewById(R.id.ll_create_first);
        create = (Button) findViewById(R.id.btn_create);
        mPullTorefreshListView = (PullToRefreshListView) findViewById(R.id.main_list);
        mListView = mPullTorefreshListView.getRefreshableView();
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.slide_footer_layout, null);

    }

    /**
     * 初始化控件
     */
    @Override
    public void setViews() {
        switch (slideType) {
            case IMAGE:
                title.setText("幻灯片");
                break;
            case VIDEO:
                title.setText("视频");
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
                        IntentUtil.openActivity(mContext, SlideDetailActivity.class, IntentUtil.TYPE_SLIDE_BY_LIST, info);
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
     * 创建幻灯片弹窗
     */
    private void createDialog() {
        final InputAlertDialog dialog = new InputAlertDialog(this);
        dialog.builder()
                .setTitle(getString(R.string.create_slide_lantern))
                .setInputHint(getString(R.string.input_slide_lantern))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputText = dialog.getInputText();
                        //创建幻灯片
                       // createSlideLantern(inputText);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .show();


    }

    /**
     * 侧滑删除幻灯片集
     * @param slideInfo
     */
    private void deleteSlide(final SlideSetInfo slideInfo) {
        new InputAlertDialog(this)
                .builder()
                .setTitle(getString(R.string.confirm_delete_lantern_slide, slideInfo.groupName))
                .setMsg(getString(R.string.confirm_delete_lanternslide_msg))
                .setCancelable(false)
                .setPositiveButton("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除幻灯片，存储数据
                        SlideManager.getInstance(slideType).removeGroup(slideInfo);
                        SlideManager.getInstance(slideType).saveSlide();
                        initSlide();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .show();
    }

    /**
     * 新建幻灯片
     *
     *
     */
    private void createSlideLantern() {
        // 创建文件夹，记录文件名
//        if (TextUtils.isEmpty(filename)) {
//            ToastUtil.showToastSavor(SlideListActivity.this, getString(R.string.filename_not_null));
//            return;
//        }
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
        new CommonDialog(this, "是否删除幻灯片？", new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                //删除幻灯片，存储数据
                SlideManager.getInstance(slideType).removeGroup(info);
                SlideManager.getInstance(slideType).saveSlide();
                initSlide();
            }
        }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
            }
        },"删除").show();
//        mAdapter.remove(info);
    }

    @Override
    public void ItemClick(SlideSetInfo info) {


        IntentUtil.openActivity(mContext, SlideDetailActivity.class, IntentUtil.TYPE_SLIDE_BY_LIST, info);
    }
}
