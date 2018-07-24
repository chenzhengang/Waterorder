package com.example.qiupys.wateroder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


/**

 */
public class ReadMUActivity extends BaseNfcActivity {

    private UserDataManager userDataManager;
    private SharedPreferences sp;
    private String goods_id = 0+""; //莫名0
    String[] NAMES = {"农夫山泉", "百岁山", "娃哈哈矿泉水", "怡宝"};
    String[] PRICES = {"1.50", "3.00", "1.20", "1.80"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mu);
        sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (userDataManager==null){
            userDataManager=new UserDataManager(this);
            userDataManager.openDatabase();
        }
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

        String data = readTag(tag);
        if (data != null){
            //strarray = data.split("[ ]");

            //Toast.makeText(this,data, Toast.LENGTH_SHORT).show();
            String user_id = data.substring(0,4);
            user_id = user_id.replace("*","");
            goods_id = data.substring(4,8);
            goods_id= goods_id.replace("*","");
            Toast.makeText(getApplicationContext(),"\n用户id = "+ user_id +"\n商品id = " +goods_id, Toast.LENGTH_SHORT).show();

        }
    }

    public String readTag(Tag tag) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            //从第4页开始读
            byte[] data = ultralight.readPages(4);
            return new String(data, Charset.forName("utf-8"));
        } catch (Exception e) {
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void click(View view) {
        // TODO Auto-generated method stub
        //Log.i("指定onClick属性方式","点击事件");

        sendMessages();
    }

    private void sendMessages() {

        new Thread(new Runnable() {
            HttpURLConnection connection=null;
            BufferedReader mreader=null;//这一步写在外面不然finally无法处理
            @Override
            public void run() {
                try {
                    String username=sp.getString("USER_NAME",null);
                    Cursor cursor=userDataManager.fetchUserData(username);

                    if (cursor!=null) {
                        int phone_index = cursor.getColumnIndex("Phone");
                        int address_index = cursor.getColumnIndex("Address");
                        int id_index = cursor.getColumnIndex("ID");
                        String u_id = cursor.getString(id_index);
                        String sphone = cursor.getString(phone_index);
                        String saddress = cursor.getString(address_index);
                        String Client = username;
                        String Seller = "ZJU";
                        String Commodity = NAMES[Integer.parseInt(goods_id)];
                        String Address = saddress;
                        String Phone = sphone;
                        String Price = PRICES[Integer.parseInt(goods_id)] ;
//                        String Client = "yang";
//                        String Seller = "ZJU";
//                        String Commodity = "Water";
//                        String Address = "32-201";
//                        String Phone = "18805792102";
//                        Integer Price = 10 ;
                    //模拟器调试
                    URL url= new URL("http://10.0.2.2/test.php");
                    //mac ip地址 10.180.28.30
                    //URL url= new URL("http://10.180.24.0/test.php");
                    connection= ((HttpURLConnection) url.openConnection());
                    connection.setRequestMethod("POST");
                    DataOutputStream out =new DataOutputStream(connection.getOutputStream());
                    out.write(("Client="+Client+"&Seller="+Seller+"&Commodity="+Commodity+"&Address="+Address+"&Phone="+Phone+"&Price="+Price).getBytes());
                    //post请求
                    //结果接受成功
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    String line;
                    mreader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response= new StringBuilder();
                    while((line=mreader.readLine())!=null){
                        response.append(line+"\r\n");
                        Log.i("字符串",line);
                    }
                    //Log.i("发送数据",response.toString());
                    showView(response.toString());}
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(mreader==null){
                        try {
                            mreader.close();//你请求的网址如果不正确 这里会报错
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showView(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //这里进行了 UI操作子线程是不能更新UI的 需要用handler 但是这里的runOnUiRhread是可以的
                //因为它本身就是进行了handler的 点进去你会发现他进行的是一个senddelaymessage操作
                Toast.makeText(getApplicationContext(), "交易成功!", Toast.LENGTH_LONG).show();
                TextView textView3 = (TextView) findViewById(R.id.textView3);
                textView3.setText(s);
            }
        });
    }

    /*
    private void pay(String user) throws JSONException {
        String url = "http://114.215.85.51/seprac/bfbackend.php";
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String> param = new HashMap<String, String>();
        Map<String, String> para = new HashMap<String, String>();
        //获取当前用户预设订单
        params.put("username", user);
        params.put("method", "getPreOrder");
        final String result = HttpUtils.submitPostData(url, params, "utf-8");
        JSONObject jsonObjectone = new JSONObject(result);
        //获取当前用户银行账户
        param.put("username", user);
        param.put("method", "getUserInfo");
        final String resultse = HttpUtils.submitPostData(url, param, "utf-8");
        JSONObject jsonObjecttwo = new JSONObject(resultse);
        //交易
        para.put("username", user);
        para.put("sellname", sellname);
        para.put("useraccount", sonObjecttwo.getString("useraccount")); para.put("sellaccount", sellaccount);
        para.put("money", jsonObjectone.getString("sumMoney")); para.put("ordercontent", jsonObjectone.getString("content")); para.put("arrivingTime",
                jsonObjectone.getString("arrivingTime"));
        para.put("method", "trade");
        final String traderesult = HttpUtils.submitPostData(url, para,
                "utf-8");
        String[] resList = traderesult.split("&"); if(resList[0].equals("200"))
            Toast.makeText(getApplicationContext(), "交易成功!", Toast.LENGTH_LONG).show();
        else Toast.makeText(getApplicationContext(), traderesult, Toast.LENGTH_LONG).show();

    }
    */

}
