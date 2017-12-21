package com.savor.resturant.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.savor.resturant.bean.ContactFormat;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hezd on 2017/12/20.
 */

public class ContactUtil {
    private static ContactUtil instance = null;
    private ContactUtil(){}

    public static ContactUtil getInstance() {
        if(instance == null) {
            synchronized (ContactUtil.class) {
                if(instance == null) {
                    instance = new ContactUtil();
                }
            }
        }

        return instance;
    }

    public List<ContactFormat> getAllContact(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        List<ContactFormat> contactFormatList = new ArrayList<>();
        while(cursor.moveToNext()) {
            ContactFormat contactFormat = new ContactFormat();

            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int contactIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(contactIndex);

            String name = cursor.getString(nameIndex);
            if(!TextUtils.isEmpty(name)) {
                name = name.trim().replaceAll(" ","");
            }
            contactFormat.setName(name);

            String workAddress = "" ;


            Cursor address = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId,
                    null, null);

            if(address.moveToNext()) {
                workAddress = address.getString(address.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredPostal.DATA));
                if(!TextUtils.isEmpty(workAddress)) {
                    contactFormat.setBirthplace(workAddress);
                }
            }
            address.close();

            Cursor phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + "=" + contactId, null, null);

            int count = 0;
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(count==0) {
                    contactFormat.setMobile(phoneNumber);
                }else if(count==1) {
                    contactFormat.setMobile1(phoneNumber);
                }else {
                    break;
                }
                count++;
            }

            phoneCursor.close();

            StringBuilder sb = new StringBuilder();

            if(!TextUtils.isEmpty(name)) {
                name = name.trim().replaceAll(" ","");
                if(!isNumeric(name)&&!isLetter(name)) {
                    for(int i = 0;i<name.length();i++) {
                        String str= removeDigital(String.valueOf(PinyinHelper.toHanyuPinyinStringArray(name.charAt(i))[0]));
                        sb.append(str);
                    }
                }else {
                    sb.append(name);
                }
            }
            String mobile = contactFormat.getMobile();
            contactFormat.setKey(name+"#"+sb.toString().toLowerCase()+"#"+workAddress+(TextUtils.isEmpty(mobile)?"":mobile));

//            Cursor birthCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
//                    null, ContactsContract.Data.CONTACT_ID +"="+contactId, null, ContactsContract.Data.RAW_CONTACT_ID);
//            if (birthCursor.moveToNext()) {
//                String mimetype = birthCursor.getString(birthCursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
//                if (ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出时间类型
//                    int eventType = birthCursor.getInt(birthCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE)); // 生日
//                    if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {
//                        String birthday = birthCursor.getString(cursor
//                                .getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
//                        contactFormat.setBirthday(birthday);
//                    }
//                }
//            }
//            birthCursor.close();

            contactFormatList.add(contactFormat);
        }
        cursor.close();

        return contactFormatList;
    }

    private void addContact(ContentResolver contentResolver, List<ContactFormat> contactFormatList, int contactId, String name) {
        ContactFormat contactFormat = new ContactFormat();
        contactFormat.setName(name);

        String workAddress ;


        Cursor address = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId,
                null, null);
        if(address.moveToNext()) {
            workAddress = address.getString(address.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.DATA));
            if(!TextUtils.isEmpty(workAddress)) {
                contactFormat.setBirthplace(workAddress);
            }
        }
        address.close();

        Cursor phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                        + "=" + contactId, null, null);
        int count = 0;
        while (phoneCursor.moveToNext()) {
            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(count==0) {
                contactFormat.setMobile(phoneNumber);
            }else if(count==1) {
                contactFormat.setMobile1(phoneNumber);
            }else {
                break;
            }
            count++;
        }
        phoneCursor.close();

        contactFormatList.add(contactFormat);
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 剔除数字
     * @param value
     */
    public String removeDigital(String value){

        Pattern p = Pattern.compile("[\\d]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
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
