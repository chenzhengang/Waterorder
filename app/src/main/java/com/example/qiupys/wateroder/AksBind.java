package com.example.qiupys.wateroder;

//import android.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.List;

import com.example.qiupys.wateroder.BaseNfcActivity;

import static android.app.PendingIntent.getActivity;

public class AksBind extends BaseNfcActivity {

    int [] IMAGES = {R.drawable.water1, R.drawable.water2, R.drawable.water3, R.drawable.water4};
    String[] NAMES = {"农夫山泉", "百岁山", "娃哈哈矿泉水", "怡宝"};
    String[] PRICES = {"1.50", "3.00", "1.20", "1.80"};
    int position = 0;

    private UserDataManager userDataManager;
    private SharedPreferences sp;

    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_aks);

        sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (userDataManager==null){
            userDataManager=new UserDataManager(this);
            userDataManager.openDatabase();
        }
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AksBind.this,User.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.list_aks_goods);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.goods_layout, null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_name = (TextView)view.findViewById(R.id.textView_name);
            TextView textView_price = (TextView)view.findViewById(R.id.textView_price);
            Button btn = (Button)view.findViewById(R.id.bind);

            imageView.setImageResource(IMAGES[i]);
            textView_name.setText(NAMES[i]);
            textView_price.setText(PRICES[i]);
            btn.setTag(i);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //用户名
                    /*已测试 可读
                    String username=sp.getString("USER_NAME",null);
                    Cursor cursor=userDataManager.fetchUserData(username);

                    if (cursor!=null) {
                        int phone_index = cursor.getColumnIndex("Phone");
                        int address_index = cursor.getColumnIndex("Address");
                        int id_index = cursor.getColumnIndex("ID");
                        //String sphone=cursor.getString();
                        String u_id = cursor.getString(id_index);
                        String sphone = cursor.getString(phone_index);
                        String saddress = cursor.getString(address_index);
                        Log.d("电话：",sphone);
                        Log.d("地址：",saddress);
                        Log.d("id：",u_id);
                    }
                    */
                    Hint(i);
                }
            });
            return view;

        }
    }
    public void Hint(int i){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("绑定商品");
        builder.setMessage("确定绑定请按“确定”并将带有NFC功能的手机靠近屏幕，返回点击“取消”");
        position = i;
        Log.d("aaa","点击了"+position);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Toast.makeText(AksBind.this, "绑定成功",Toast.LENGTH_SHORT).show();
            }
        });


        builder.show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareUltralight") >= 0) {
                haveMifareUltralight = true;
                break;
            }
        }
        if (!haveMifareUltralight) {
            Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_SHORT).show();
            return;
        }
        writeTag(tag,position);
        Log.d("aaa","绑定了"+position);
    }

    public void writeTag(Tag tag,int position) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            //从第五页开始写，中文需要转换成GB2312格式
            //每页只有4个字节

            //用户名
            String username=sp.getString("USER_NAME",null);
            Cursor cursor=userDataManager.fetchUserData(username);

            if (cursor!=null){
                Log.d("cursor","不是null");
                int id_index =cursor.getColumnIndex("ID");
                String u_id = cursor.getString(id_index);
                int x= u_id.length();
                for (int i = 0;i<4-x;i++){
                    u_id = u_id + "*";
                }
                Log.d("id",u_id);
                //用户id
                ultralight.writePage(4, u_id.getBytes(Charset.forName("utf-8")));
                //商品id
                String gid = position+"";
                int y= gid.length();
                for (int i = 0;i<4-y;i++){
                    gid = gid + "*";
                }
                Log.d("gid",gid);
                ultralight.writePage(5, gid.getBytes(Charset.forName("utf-8")));
                Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("fill","失败");
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
    }

}
