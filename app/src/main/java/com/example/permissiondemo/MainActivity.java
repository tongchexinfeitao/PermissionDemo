package com.example.permissiondemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GO_TO_SETTING_REQUEST_CODE = 10;
    public static String TAG = "permission_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 请求权限和打开相机
     *
     * @param view
     */
    public void openCamera(View view) {


        //用法一
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //检测是否授权
//            if (this.checkSelfPermission(CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
//                Log.e(TAG, "===========检查权限---用户已经拥有相机这个权限了");
//                startCamera();
//            } else {
//                Log.e(TAG, "===========检查权限---用户没有相机这个权限");
//                ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_REQUEST_CODE);
//            }
//        }else{
//            startCamera();
//        }

        //用法二
        //检测是否授权
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "===========检查权限---用户已经拥有相机这个权限了");
            startCamera();
        } else {
            Log.e(TAG, "===========检查权限---用户没有相机这个权限");
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GO_TO_SETTING_REQUEST_CODE:
                if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "===========设置页面返回之后-再次检查权限---用户已经拥有相机这个权限了");
                    startCamera();
                } else {
                    Log.e(TAG, "===========设置页面返回之后-再次检查权限---用户没有开启这个权限，在这不用再去请求权限了");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (permissions[0].equals(CAMERA_PERMISSION)) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "===========权限回调---用户同意了");
                        startCamera();
                    } else {
                        Log.e(TAG, "===========权限回调---用户拒绝了");
                        /**
                         * 用户拒绝权限，而且没有勾选不再提醒， shouldShowRequestPermissionRationale会返回true，其他返回false
                         */
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION)) {
                            Log.e(TAG, "=========== shouldShowRequestPermissionRationale 返回值为 true");
                            //解释权限，用户点击确定，继续申请权限
                            showTipExplainPermission();
                        } else {
                            //返回false ，用户勾选了  不再询问，之后系统也不会再弹出系统权限弹框，所以我们自己弹框解释
                            Log.e(TAG, "=========== shouldShowRequestPermissionRationale 返回值为 false");
                            //告诉用户缺少权限，可以通过setting页面打开
                            showTipGoSetting();
                        }
                    }
                }
                break;
        }
    }


    /**
     * 对话框 -- 给用户解释需要的权限
     */
    public void showTipExplainPermission() {
        new AlertDialog.Builder(this)
                .setTitle("帮助")
                .setMessage("需要相机权限，去拍照")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 对话框 -- 告诉用户怎么去打开权限
     */
    public void showTipGoSetting() {
        new AlertDialog.Builder(this)
                .setTitle("帮助")
                .setMessage("当前应用缺少【相机】权限\n\n请点击\"设置\"-\"应用权限\"去打开所需权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //告诉用户怎么去打开权限
                        goToSetting();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    /**
     * 跳转到设置权限页面
     */
    private void goToSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, GO_TO_SETTING_REQUEST_CODE);
    }


    /**
     * 打开相机
     */
    public void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

//    public void requestCall(View view) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//            Log.e(TAG, "===========检查权限---用户已经拥有CALL_PHONE这个权限了");
//        } else {
//            Log.e(TAG, "===========检查权限---用户没有CALL_PHONE这个权限");
//            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CALL_PHONE}, 1000);
//        }
//    }
//
//    public void checkReadPhoneState(View view) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            Log.e(TAG, "===========检查权限---用户已经拥有 READ_PHONE_STATE 这个权限了");
//        } else {
//            Log.e(TAG, "===========检查权限---用户没有开启READ_PHONE_STATE这个权限");
//        }
//    }
}
