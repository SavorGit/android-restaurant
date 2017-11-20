//package com.savor.resturant.activity;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.savor.resturant.R;
//import com.savor.resturant.adapter.PdfListAdapter;
//import com.savor.resturant.bean.PdfInfo;
//import com.savor.resturant.utils.ActivitiesManager;
//import com.savor.resturant.utils.ProjectionManager;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PdfListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
//
//    private ListView mPdfListView;
//    private PdfListAdapter mPdfListAdapter;
//    private LinearLayout mEmptyHintLayout;
//    private LinearLayout mHelpLayout;
//    private LinearLayout mBacklayout;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pdf_list);
//
//        getViews();
//        setViews();
//        setListeners();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        List<PdfInfo> pdfList = mSession.getPdfList();
//        List<PdfInfo> tempList = new ArrayList<>();
//        if(pdfList!=null) {
//            for (PdfInfo pdfInfo : pdfList) {
//                String path = pdfInfo.getPath();
//                File file = new File(path);
//                if (file.exists()) {
//                    tempList.add(pdfInfo);
//                }
//            }
//        }
//
//        if (tempList != null && tempList.size() > 0) {
//            mPdfListAdapter.setData(pdfList);
//            mEmptyHintLayout.setVisibility(View.GONE);
//        } else {
//            mEmptyHintLayout.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void getViews() {
//        mHelpLayout = (LinearLayout) findViewById(R.id.help_la);
//        mPdfListView = (ListView) findViewById(R.id.lv_files);
//        mEmptyHintLayout = (LinearLayout) findViewById(R.id.ll_empty_hint);
//        mBacklayout = (LinearLayout) findViewById(R.id.back);
//    }
//
//    @Override
//    public void setViews() {
//        mPdfListAdapter = new PdfListAdapter(this);
//        mPdfListView.setAdapter(mPdfListAdapter);
//    }
//
//    @Override
//    public void setListeners() {
//        mPdfListView.setOnItemClickListener(this);
//        mHelpLayout.setOnClickListener(this);
//        mBacklayout.setOnClickListener(this);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        PdfInfo info = (PdfInfo) parent.getItemAtPosition(position);
//        String path = info.getPath();
//        File file = new File(path);
//        if (!file.exists()) {
//            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
//        } else {
//            Intent intent = new Intent(this,PdfPreviewActivity.class);
//            Uri data = Uri.parse("file://"+path);
//            boolean lockedScrren = ProjectionManager.getInstance().isLockedScrren();
//            intent.putExtra("isLockedScrren",lockedScrren);
//            intent.setDataAndType(data,"application/pdf");
//            startActivity(intent);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(ActivitiesManager.getInstance().contains(HotspotMainActivity.class)) {
//            setResult(HotspotMainActivity.FROM_APP_BACK);
//        }else {
//            Intent intent = new Intent(this,HotspotMainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }
//        finish();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.help_la:
//                Intent intent = new Intent(this,FileProHelpActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.back:
//                finish();
//                break;
//        }
//    }
//}
