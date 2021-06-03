package com.llw.scandemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQ_CODE = 111;
    public static final int DECODE = 1;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    private Button btnScan;
    private TextView tvInfo;
    private Button btCopy,btBrowser,btClear;
    private RecyclerView recyclerView;
    private Group gpResult;
    private String qrCodeString;
    private DataCharAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(getApplication());
        initView();
    }

    public static final int MSG_RESULT = 1;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_RESULT:
                    HmsScan obj = (HmsScan) message.obj;
                    showResult(obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void showResult(HmsScan obj) {
        gpResult.setVisibility(View.VISIBLE);
//        Bitmap bitmap = ImageUtils.bytes2Bitmap(obj.);
//        ivImg.setImageBitmap(bitmap);
        qrCodeString=obj.originalValue;
        tvInfo.setText(obj.originalValue);
        historyAdapter.addData(0, qrCodeString);
        recyclerView.scrollToPosition(0);

    }

    //权限请求
    public void loadScanKitBtnClick(View view) {
        requestPermission(CAMERA_REQ_CODE, DECODE);
    }

    //编辑请求权限
    private void requestPermission(int requestCode, int mode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    //权限申请返回
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null) {
            return;
        }

        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }
    }

    //Activity回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                Message message = new Message();
                message.what = MSG_RESULT;
                message.obj = obj;
                handler.sendMessage(message);
//                Toast.makeText(this,obj.originalValue,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        btnScan = (Button) findViewById(R.id.btn_scan);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btCopy = (Button) findViewById(R.id.btCopy);
        gpResult = (Group) findViewById(R.id.gpResult);
        btBrowser = findViewById(R.id.btBrowser);
        btClear = findViewById(R.id.btClear);
        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort(qrCodeString);
                ClipboardUtils.copyText(qrCodeString);
            }
        });
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyAdapter.setList(new ArrayList<CharSequence>());
            }
        });
        btBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri content_url = Uri.parse(qrCodeString);
                intent.setData(content_url);
                startActivity(Intent.createChooser(intent, "请选择浏览器"));
            }
        });
        adapter();

    }

    private void adapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        historyAdapter = new DataCharAdapter();
        recyclerView.setAdapter(historyAdapter);
    }
}
