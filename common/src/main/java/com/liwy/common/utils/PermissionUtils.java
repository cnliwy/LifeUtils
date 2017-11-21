package com.liwy.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleDialog;
import com.yanzhenjie.permission.RationaleListener;
import com.yanzhenjie.permission.SettingDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 使用：
 * 1 初始化
 *    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
 *    List<String> permissionList = Arrays.asList(permissions);
 *    final PermissionUtils.PermissionBuilder builder = PermissionUtils.getBuilder(MainActivity.this,120,permissionList)
 *    .setActivity(MainActivity.this)// 如果需要onActivityResult回调的话
 *             .setRefusedMsg("权限被拒绝后将无法正常工作，请授予权限！")
 *             .setSettingMsg("请前往设置页面申请权限了");
 *  2 回调
 *      builder.setOnRefusedListener();
 *      builder.setOnSuccessListener();
 *  3 判断是否有权限
 *      PermissionUtils.ifHasPermission(MainActivity.this,permissionList);
 *  4 申请权限
 *       PermissionUtils.applyForPermission(builder);
 *
 * Created by liwy on 2017/9/14.
 */

public class PermissionUtils {
    // 权限申请成功后的回调
    public interface OnSuccessListener{
        public void onSuccess();
    }
    // 权限拒绝后的回调
    public interface OnRefusedListener{
        public void onRefused();
    }
    public static PermissionBuilder getBuilder(Context context,int requestCode, List<String> permissionList){
        return new PermissionBuilder(context, requestCode,permissionList);
    }
    public static PermissionBuilder getBuilder(Context context,int requestCode,String permission){
        List<String> permissionList = new ArrayList<>();
        permissionList.add(permission);
        return new PermissionBuilder(context, requestCode,permissionList);
    }
    public static PermissionBuilder getBuilder(){
        return new PermissionBuilder();
    }
    /**
     * 检测是否具备权限（文件读写、手机状态、定位）
     */
    public static boolean ifHasPermission(Context context,List<String> permissions){
        // 判断是否开启读取通讯录权限
        if (AndPermission.hasPermission(context,permissions)){
            System.out.println("已经有权限了");
            return true;
        }
        return false;
    }

    public static boolean ifHasPermission(Context context,String... permissions){
        return ifHasPermission(context, Arrays.asList(permissions));
    }

    // 申请权限
    public static void applyForPermission(final PermissionBuilder builder){
        AndPermission.with(builder.context)
                .requestCode(builder.requestCode)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        if (!ifHasPermission(builder.context, builder.permissionList)) {
                            gotoSetting(builder);
                        }else{
                            // 终于申请权限成功了
                            if (builder.onSuccessListener != null)builder.onSuccessListener.onSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        System.out.println("失败的权限" + deniedPermissions.toString());
                        // 申请权限失败，可以提醒一下用户。
                        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
                        if (AndPermission.hasAlwaysDeniedPermission(builder.context, deniedPermissions)) {
                            gotoSetting(builder);
                        }else{
                            //拒绝了权限，但未勾选不再提示的情况下，进入此处;默认只提醒一次
                            if (builder.isShowRationaleDialog) {
                                applyForPermission(builder);
                            }
                        }
                    }
                })
                .permission(builder.permissionList.toArray(new String[builder.permissionList.size()]))
                .rationale(new RationaleListener() {
                    // 拒绝后提示用户并继续申请权限
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        noticeUserAfterRefused(builder,rationale);
                    }
                }).start();
    }

    // 用户拒绝权限后提示用户申请权限
    private static void noticeUserAfterRefused(final PermissionBuilder builder, final Rationale rationale){
        builder.isShowRationaleDialog = false;
        // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
        RationaleDialog rationaleDialog = AndPermission.rationaleDialog(builder.context, rationale);
        // 第一种：默认提示语
        if (builder.refusedMsg != null && !"".equals(builder.refusedMsg)) {
            rationaleDialog.setMessage(builder.refusedMsg);
        }
        rationaleDialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                rationale.cancel(); // 用户拒绝申请。
                                // 用户拒绝申请权限
                                if (builder.onRefusedListener != null)builder.onRefusedListener.onRefused();
                            }
                        }).setPositiveButton("申请")
                        .show();
    }

    // 无法调起申请权限的提示框则提示用户前往设置页面授权
    private static void gotoSetting(final PermissionBuilder builder){
        SettingDialog settingDialog;
        if (builder.activity != null){
            // requestCode用于activity或者fragment通过onResult回调
            settingDialog = AndPermission.defaultSettingDialog(builder.activity, builder.requestCode);
        }else{
            settingDialog = AndPermission.defaultSettingDialog(builder.context);
        }
        if (builder.settingMsg != null && !"".equals(builder.settingMsg)) {
            // 第二种：自定义提示语
            settingDialog.setTitle("权限申请失败")
                    .setMessage(builder.settingMsg);
        }
        settingDialog.setPositiveButton("设置")
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("==============》设置弹框，用户拒绝前往设置页面授权");
                            dialog.dismiss();
                            if (builder.onRefusedListener != null)builder.onRefusedListener.onRefused();
                        }
                    });
        settingDialog.show();
    }

    public static class PermissionBuilder{
        boolean isShowRationaleDialog = true;
        Context context;
        Activity activity;
        // 用户拒绝权限后的提示信息，引导用户再次申请权限
        String refusedMsg;
        // 用户拒绝权限并勾选不再提示后，引导用户前往设置页面授权的提示信息
        String settingMsg;

        int requestCode;
        List<String> permissionList = new ArrayList<>();
        OnSuccessListener onSuccessListener;
        OnRefusedListener onRefusedListener;

        public PermissionBuilder() {
        }

        public PermissionBuilder(Context context, int requestCode, List<String> permissionList) {
            this.context = context;
            this.permissionList = permissionList;
            this.requestCode = requestCode;
        }

        public PermissionBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public PermissionBuilder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public PermissionBuilder setRefusedMsg(String refusedMsg) {
            this.refusedMsg = refusedMsg;
            return this;
        }

        public PermissionBuilder setSettingMsg(String settingMsg) {
            this.settingMsg = settingMsg;
            return this;
        }

        /**
         * 用于onActivityResult回调
         * @param requestCode
         * @return
         */
        public PermissionBuilder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        /**
         * 申请的权限集合
         * @param permissionList
         * @return
         */
        public PermissionBuilder setPermissionList(List<String> permissionList) {
            this.permissionList.addAll(permissionList);
            return this;
        }
        public PermissionBuilder addPermission(String permission){
            this.permissionList.add(permission);
            return this;
        }

        /**
         * 权限申请成功后的回调
         * @param onSuccessListener
         * @return
         */
        public PermissionBuilder setOnSuccessListener(OnSuccessListener onSuccessListener) {
            this.onSuccessListener = onSuccessListener;
            return this;
        }

        /**
         * 拒绝权限后的回调
         * @param onRefusedListener
         * @return
         */
        public PermissionBuilder setOnRefusedListener(OnRefusedListener onRefusedListener) {
            this.onRefusedListener = onRefusedListener;
            return this;
        }
    }
}
