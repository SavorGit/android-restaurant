package com.savor.resturant.adapter.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.savor.resturant.R;
import com.savor.resturant.activity.ContactCustomerListActivity;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.core.Session;
import com.savor.resturant.utils.GlideCircleTransform;
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

    private final ContactCustomerListActivity.OperationType operationType;
    private final Session session;
    //    private final CharacterParser characterParser;
    private List<ContactFormat> mLists;

    private Context mContext;
    private boolean isMultiSelectMode;
    private OnAddBtnClickListener onAddBtnClickListener;
    private OnCheckStateChangeListener onCheckStateChangeListener;
    private OnItemClickListener onItemClickListener;


    public MyContactAdapter(Context ct, List<ContactFormat> mListsD, ContactCustomerListActivity.OperationType operationType) {
        this.mLists = mListsD;
        mContext = ct;
        this.addAll(mLists);
        this.operationType = operationType;
        this.session = Session.get(mContext);
//        characterParser = CharacterParser.getInstance();
    }

    public void setData(List<ContactFormat> mListsD) {
        this.mLists = mListsD;
        this.addAll(mLists);
    }

    public List<ContactFormat> getData() {
        return mLists;
    }

    public void setSelectMode(boolean isMultiSelectMode) {
        this.isMultiSelectMode = isMultiSelectMode;
        notifyDataSetChanged();
    }

    @Override
    public MyContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyContactAdapter.ContactViewHolder holder, final int position) {
        final SwipeItemLayout swipeRoot = holder.mRoot;
        swipeRoot.setSwipeAble(false);
        TextView textView = holder.mName;
        ContactFormat item = getItem(position);
        boolean selected = item.isSelected();

        String mobile = item.getMobile();
        String name = item.getName();
        String face_url = item.getFace_url();

        textView.setText(getItem(position).getName());

        holder.mAdd.setVisibility(isMultiSelectMode?View.GONE:View.VISIBLE);

        if(TextUtils.isEmpty(mobile)) {
            holder.mNum.setVisibility(View.GONE);
        }else {
            holder.mNum.setVisibility(View.VISIBLE);
            holder.mNum.setText(mobile);
        }

        holder.checkBox.setVisibility(isMultiSelectMode?View.VISIBLE:View.GONE);

        holder.mAdd.setVisibility((ContactCustomerListActivity.OperationType.CONSTACT_LIST_FIRST ==operationType||ContactCustomerListActivity.OperationType.CONSTACT_LIST_NOTFIST ==operationType)?View.VISIBLE:View.GONE);

        holder.mAdd.setTag(position);


        holder.checkBox.setTag(position);


        // 判断是否已添加
        List<ContactFormat> customer_list =session.getCustomerList();
        if(customer_list!=null&&customer_list.size()>0) {
            if(customer_list.contains(item)) {
                item.setAdded(true);
            }
        }

        boolean added = item.isAdded();
        if(isMultiSelectMode) {
            holder.checkBox.setChecked(selected||added);
        }else {
            item.setSelected(false);
            holder.checkBox.setChecked(added);
        }
        if(added) {
            holder.mAdd.setBackground(null);
            holder.mAdd.setTextColor(mContext.getResources().getColor(R.color.app_gray));
            holder.checkBox.setEnabled(false);
            holder.mAdd.setText("已添加");
            holder.mAdd.setOnClickListener(null);
        }else {
            holder.mAdd.setBackgroundResource(R.drawable.edit_text_bg);
            holder.mAdd.setText("添加");
            holder.mAdd.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.checkBox.setEnabled(true);
            holder.mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    ContactFormat addItem = getItem(pos);
                    if(onAddBtnClickListener!=null) {
                        onAddBtnClickListener.onAddBtnClick(pos,addItem);
                    }
                }
            });
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                ContactFormat changeItem = getItem(pos);
                changeItem.setSelected(isChecked);
                if(onCheckStateChangeListener!=null) {
                    onCheckStateChangeListener.onCheckStateChange(isChecked,changeItem);
                }
            }
        });

        swipeRoot.setTag(position);
        swipeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null) {
                    int pos = (int) swipeRoot.getTag();
                    onItemClickListener.onItemClick(pos,getItem(pos));
                }
            }
        });

        if(!TextUtils.isEmpty(face_url)) {
            holder.iv_header.setVisibility(View.VISIBLE);
            holder.tv_label.setVisibility(View.GONE);
            Glide.with(mContext).load(face_url).transform(new GlideCircleTransform(mContext)).into(holder.iv_header);
        }else {
            holder.iv_header.setVisibility(View.GONE);
            holder.tv_label.setVisibility(View.VISIBLE);

            if(!TextUtils.isEmpty(name)&&name.length()>0) {
                holder.tv_label.setText(String.valueOf(name.charAt(0)));
            }
        }

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
            String firstKeyWord = String.valueOf(mLists.get(i).getKey().charAt(0));
            if(firstKeyWord.equals("#")&&firstKeyWord.equals(String.valueOf(section))) {
                return i;
            }
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
        public TextView mAdd;
        public TextView mNum;
        public TextView tv_label;
        public SwipeItemLayout mRoot;
        public TextView mDelete;
        public CheckBox checkBox;
        public ImageView iv_header;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_contact_title);
            tv_label = (TextView) itemView.findViewById(R.id.tv_label);
            mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
            mDelete = (TextView) itemView.findViewById(R.id.item_contact_delete);
            mNum = (TextView) itemView.findViewById(R.id.tv_num);
            mAdd = (TextView) itemView.findViewById(R.id.tv_add);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_select);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
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

    public void setOnAddBtnClickListener(OnAddBtnClickListener onAddBtnClickListener) {
        this.onAddBtnClickListener = onAddBtnClickListener;
    }

    public void setOnCheckStateChangeListener(OnCheckStateChangeListener onCheckStateChangeListener) {
        this.onCheckStateChangeListener = onCheckStateChangeListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnAddBtnClickListener {
        void onAddBtnClick(int position,ContactFormat contactFormat);
    }

    public interface OnCheckStateChangeListener {
        void onCheckStateChange(boolean isChecked,ContactFormat contactFormat);
    }

    public interface OnItemClickListener {
        void onItemClick(int position,ContactFormat contactFormat);
    }
}
