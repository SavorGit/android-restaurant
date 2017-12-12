package com.savor.resturant.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.WifiUtil;

import java.util.HashMap;

/**
 * 幻灯片设置对话框
 * Created by luminita on 16/11/21.
 */
public class SlideSettingsDialog implements OnClickListener {
    public static final int QUALITY_HIGH = 0x1;
    public static final int QUALITY_LOW = 0x2;
    private final SlideManager.SlideType slideType;
    private Context context;
    private Dialog dialog;
    /**单张图片投屏时间*/
    private int singlePicTime = 5;
    /**是否循环播放*/
    private boolean isLooping = false;
    /**循环播放时间 单位分钟*/
    private int loopTime = 30;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private View view;
    private Button btn_pos;
    private Button btn_mid;
    private ImageView line1;
    private ImageView line2;
    private Button btn_neg;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView mLoopTimeTv;
    private LinearLayout mSeekBarLoopTimeLayout;
    private TextView mThreeSecTv;
    private TextView mFiveSecTv;
    private TextView mEightSecTv;
    private ImageView mLoopOffIv;
    private SeekBar mSeekBar;
    private LinearLayout mImageLoopTimeLayout;
    private RadioGroup mQualityRG;
    private TextView mQualityHint;

    public SlideSettingsDialog(Context context, SlideManager.SlideType slideType) {
        this.context = context;
        this.slideType = slideType;
    }


    public SlideSettingsDialog builder() {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_slide_settings, null);
        mQualityRG = (RadioGroup) view.findViewById(R.id.rg_video_quality);
        lLayout_bg = (LinearLayout) view.findViewById(R.id.layout_bg);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        mImageLoopTimeLayout = (LinearLayout) view.findViewById(R.id.ll_image_time);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        btn_mid = (Button) view.findViewById(R.id.btn_mid);
        btn_mid.setVisibility(View.GONE);
        line1 = (ImageView) view.findViewById(R.id.line1);
        line1.setVisibility(View.GONE);
        line2 = (ImageView) view.findViewById(R.id.line2);
        line2.setVisibility(View.GONE);
        mLoopTimeTv = (TextView) view.findViewById(R.id.tv_loop_time);
        mSeekBarLoopTimeLayout = (LinearLayout) view.findViewById(R.id.ll_seekbar_time);
        mLoopOffIv = (ImageView) view.findViewById(R.id.iv_loop_off);
        mThreeSecTv = (TextView) view.findViewById(R.id.single_time_3s);
        mFiveSecTv = (TextView) view.findViewById(R.id.single_time_5s);
        mEightSecTv = (TextView) view.findViewById(R.id.single_time_8s);
        mQualityHint = (TextView) view.findViewById(R.id.tv_qulity_hint);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (
                DensityUtil.dip2px(context,335)), FrameLayout.LayoutParams.WRAP_CONTENT));

        String wifiName = WifiUtil.getWifiName(context);
        txt_title.setText("正在投屏的包间-"+wifiName);

        mLoopTimeTv.setVisibility(isLooping?View.VISIBLE:View.GONE);
        mSeekBarLoopTimeLayout.setVisibility(isLooping?View.VISIBLE:View.GONE);

        mThreeSecTv.setOnClickListener(this);
        mFiveSecTv.setOnClickListener(this);
        mEightSecTv.setOnClickListener(this);

        mLoopOffIv.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress>=1) {
                    mLoopTimeTv.setText(progress+"分钟");
                }else {
                    mSeekBar.setProgress(1);
                }
                loopTime = mSeekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        switch (slideType) {
            case IMAGE:

                break;
            case VIDEO:
                mImageLoopTimeLayout.setVisibility(View.GONE);
                break;
        }
        return this;
    }

    public boolean isShowing() {
        if(dialog!=null&&dialog.isShowing())
            return true;
        return false;
    }

    public void dismiss() {
        if(dialog!=null) {
            dialog.dismiss();
        }
    }

    public SlideSettingsDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public SlideSettingsDialog setPositiveButton(String text, final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);

            }
        });
        return this;
    }

    public SlideSettingsDialog setNegativeButton(String text, final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    /**
     * 重置单张投屏时间按钮状态
     */
    private void resetSingleTimeBtnStatus() {
        mThreeSecTv.setTextColor(context.getResources().getColor(R.color.color_red_light));
        mFiveSecTv.setTextColor(context.getResources().getColor(R.color.color_red_light));
        mEightSecTv.setTextColor(context.getResources().getColor(R.color.color_red_light));

        mThreeSecTv.setBackgroundResource(R.drawable.bg_hollow_corner_red);
        mFiveSecTv.setBackgroundResource(R.drawable.bg_hollow_corner_red);
        mEightSecTv.setBackgroundResource(R.drawable.bg_hollow_corner_red);
    }

    /**
     * 单张投屏时间选择3s
     */
    private void selectThreeSecBtn() {
        resetSingleTimeBtnStatus();
        mThreeSecTv.setBackgroundResource(R.drawable.bg_solid_corner_red);
        mThreeSecTv.setTextColor(context.getResources().getColor(R.color.white));
        singlePicTime = 3;
    }

    /**
     * 单张投屏时间选择5s
     */
    private void selectFiveSecBtn() {
        resetSingleTimeBtnStatus();
        mFiveSecTv.setBackgroundResource(R.drawable.bg_solid_corner_red);
        mFiveSecTv.setTextColor(context.getResources().getColor(R.color.white));
        singlePicTime = 5;
    }

    /**
     * 单张投屏时间选择8s
     */
    private void selectEightSecBtn() {
        resetSingleTimeBtnStatus();
        mEightSecTv.setBackgroundResource(R.drawable.bg_solid_corner_red);
        mEightSecTv.setTextColor(context.getResources().getColor(R.color.white));
        singlePicTime = 8;
    }

    private void setLayout() {
        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            line1.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return true;
            }
        });

        mQualityRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_high_quality:
                        mQualityHint.setText("(高质量投屏，速度较慢)");
                        break;
                    case R.id.rb_low_quality:
                        mQualityHint.setText("(投屏质量一般，速度较快)");
                        break;
                }
            }
        });
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_time_3s:
                selectThreeSecBtn();
                break;
            case R.id.single_time_5s:
                selectFiveSecBtn();
                break;
            case R.id.single_time_8s:
                selectEightSecBtn();
                break;
            case R.id.iv_loop_off:
                loopoff();
                break;
        }
    }

    /**
     * 关闭循环播放
     */
    private void loopoff() {
        if(isLooping) {
            isLooping = false;
            mSeekBar.setProgress(30);
            mLoopOffIv.setImageResource(R.drawable.loop_off);
            mLoopTimeTv.setVisibility(View.GONE);
            mSeekBarLoopTimeLayout.setVisibility(View.GONE);
        }else {
            isLooping = true;
            mLoopOffIv.setImageResource(R.drawable.loop_on);
            mLoopTimeTv.setVisibility(View.VISIBLE);
            mSeekBarLoopTimeLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取单张投票设置的时间
     */
    public int getSingleTime() {
        return singlePicTime;
    }

    /**
     * 是否循环播放
     */
    public boolean isLooping() {
        return isLooping;
    }

    /**
     * 获取循环时间
     */
    public int getLoopTime() {
        return loopTime;
    }

    /**
     * 获取当前配置投屏质量，默认是高清
     * @return
     */
    public int getQuality() {
        int checkedRadioButtonId = mQualityRG.getCheckedRadioButtonId();
        if(R.id.rb_high_quality == checkedRadioButtonId) {
            return QUALITY_HIGH;
        }else if(R.id.rb_low_quality == checkedRadioButtonId) {
            return QUALITY_LOW;
        }
        return QUALITY_HIGH;
    }
}
