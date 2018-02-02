//package com.savor.resturant.service;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
//
//import com.common.api.utils.LogUtils;
//import com.savor.resturant.bean.RoomInfo;
//import com.savor.resturant.core.Session;
//import com.savor.resturant.utils.ConstantValues;
//
//import java.util.List;
//
///**
// * 餐厅服务列表投屏状态维护
// * @author hezd
// */
//public class ProjectionService extends IntentService {
//    /**延迟执行推荐菜投屏*/
//    private static final String ACTION_RECOMMEND_START = "com.savor.resturant.service.action.recommend";
//    /**当播放完毕（推荐菜播放完毕）更新包间列表状态*/
//    private static final String ACTION_STOP_DELAYED = "com.savor.resturant.service.action.stopdelayed";
//
//    /**包间信息*/
//    private static final String EXTRA_PARAM_ROOM = "com.savor.resturant.service.extra.room";
//    private static final String EXTRA_PARAM2 = "com.savor.resturant.service.extra.PARAM2";
//    /**延迟5分钟播放推荐菜*/
//    private static final int MSG_RECOMMEND = 0x1;
//    /**播放完毕*/
//    private static final int MSG_COMPLETE = 0x2;
//
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            Intent intent;
//            Session session;
//            List<RoomInfo> roomList;
//            switch (msg.what) {
//                case MSG_RECOMMEND:
//                    RoomInfo room = (RoomInfo) msg.obj;
//                    session = Session.get(ProjectionService.this);
//                    roomList = session.getRoomList();
//                    if(roomList!=null&&roomList.contains(room)) {
//                        int i = roomList.indexOf(room);
//                        RoomInfo roomInfo = roomList.get(i);
//                        roomInfo.setWelPlay(false);
//                        roomInfo.setRecommendPlay(true);
//                    }
//
//                    intent = new Intent(ConstantValues.ACTION_RECOMMEND_PLAY_DELAYED_5MIN);
//                    sendBroadcast(intent);
//
//                    Message message = Message.obtain();
//                    message.what = MSG_COMPLETE;
//                    message.obj = room;
//                    mHandler.sendMessageDelayed(message,10*1000);
//                    break;
//                case MSG_COMPLETE:
//                    RoomInfo roomInfo = (RoomInfo) msg.obj;
//                    session = Session.get(ProjectionService.this);
//                    roomList = session.getRoomList();
//                    if(roomList!=null&&roomList.contains(roomInfo)) {
//                        int i = roomList.indexOf(roomInfo);
//                        RoomInfo currentRoom = roomList.get(i);
//                        currentRoom.setWelPlay(false);
//                        currentRoom.setRecommendPlay(false);
//
//                         intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
//                        sendBroadcast(intent);
//                    }
//                    break;
//            }
//        }
//    };
//
//    public ProjectionService() {
//        super("ProjectionService");
//    }
//
//    /**
//     *  延迟执行推荐菜播放状态
//     * @see IntentService
//     */
//    public static void startActionRecommend(Context context, RoomInfo roomInfo) {
//        Intent intent = new Intent(context, ProjectionService.class);
//        intent.setAction(ACTION_RECOMMEND_START);
//        intent.putExtra(EXTRA_PARAM_ROOM, roomInfo);
//        context.startService(intent);
//    }
//
//    /**
//     * Starts this service to perform action Baz with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, ProjectionService.class);
//        intent.setAction(ACTION_STOP_DELAYED);
//        intent.putExtra(EXTRA_PARAM_ROOM, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        LogUtils.d("savor:projection onHandleIntent");
//
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_RECOMMEND_START.equals(action)) {
//                final RoomInfo roomInfo = (RoomInfo) intent.getSerializableExtra(EXTRA_PARAM_ROOM);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionRecommend(roomInfo);
//            } else if (ACTION_STOP_DELAYED.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM_ROOM);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
//        }
//    }
//
//    /**
//     * 延迟5分钟播放推荐菜
//     */
//    private void handleActionRecommend(RoomInfo roomInfo) {
////        Session session = Session.get(this);
////        List<RoomInfo> roomList = session.getRoomList();
////        if(roomList!=null&&roomList.contains(roomInfo)) {
////            int i = roomList.indexOf(roomInfo);
////            RoomInfo currentRoom = roomList.get(i);
////            currentRoom.setWelPlay(true);
////            currentRoom.setRecommendPlay(false);
////        }
//        Message msg = Message.obtain();
//        msg.what = MSG_RECOMMEND;
//        msg.obj = roomInfo;
//        mHandler.sendMessageDelayed(msg, 10 * 1000);
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//}
