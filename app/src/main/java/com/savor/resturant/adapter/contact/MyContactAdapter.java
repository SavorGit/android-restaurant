package com.savor.resturant.adapter.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.widget.contact.SwipeItemLayout;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiang on 12/3/15.
 * 根据当前权限进行判断相关的滑动逻辑
 */
public class MyContactAdapter extends ContactBaseAdapter<ContactFormat, MyContactAdapter.ContactViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

//    private final CharacterParser characterParser;
    private List<ContactFormat> mLists;

    private Context mContext;


    public MyContactAdapter(Context ct, List<ContactFormat> mListsD) {
        this.mLists = mListsD;
        mContext = ct;
        this.addAll(mLists);
//        characterParser = CharacterParser.getInstance();
    }

    public void setData(List<ContactFormat> mListsD) {
        this.mLists = mListsD;
        this.addAll(mLists);
    }

    @Override
    public MyContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyContactAdapter.ContactViewHolder holder, final int position) {
        SwipeItemLayout swipeRoot = holder.mRoot;
        swipeRoot.setSwipeAble(false);
        TextView textView = holder.mName;
        ContactFormat item = getItem(position);
        String mobile = item.getMobile();
        String phone = "";
        if(!TextUtils.isEmpty(mobile))
            phone = mobile;
        textView.setText(getItem(position).getName()+"  "+phone);

    }

    @Override
    public long getHeaderId(int position) {
        return PinyinHelper.toHanyuPinyinStringArray(getItem(position).getName().charAt(0))[0].toLowerCase().charAt(0);
//        return characterParser.getSelling(getItem(position).getDisplayName()).toLowerCase().charAt(0);

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        ;
        String showValue = String.valueOf(PinyinHelper.toHanyuPinyinStringArray(getItem(position).getName().charAt(0))[0].toLowerCase().charAt(0));
//        if ("$".equals(showValue)) {
//            textView.setText("群主");
//        } else if ("%".equals(showValue)) {
//            textView.setText("系统管理员");
//
//        } else {
            textView.setText(showValue);
//        }

    }


    public int getPositionForSection(char section) {
        for (int i = 0; i < getItemCount(); i++) {

            String sortStr;
            String firstWord = String.valueOf(mLists.get(i).getName().charAt(0));
            if(isNumeric(firstWord)||isLetter(firstWord)) {
                sortStr = firstWord;
            }else {
                sortStr =  PinyinHelper.toHanyuPinyinStringArray(mLists.get(i).getName().charAt(0))[0];
            }

            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public SwipeItemLayout mRoot;
        public TextView mDelete;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_contact_title);
            mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
            mDelete = (TextView) itemView.findViewById(R.id.item_contact_delete);


        }


    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /*判断字符串中是否仅包含字母数字和汉字
     *各种字符的unicode编码的范围：
    * 汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
    * 数字：[0x30,0x39]（或十进制[48, 57]）
    *小写字母：[0x61,0x7a]（或十进制[97, 122]）
    * 大写字母：[0x41,0x5a]（或十进制[65, 90]）
*/
    public static boolean isLetter(String str) {
        String regex = "^[a-zA-Z]+$";
        return str.matches(regex);
    }
}
