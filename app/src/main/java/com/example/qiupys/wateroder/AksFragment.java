package com.example.qiupys.wateroder;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AksFragment extends Fragment {

    private ArrayList<String> AKS=new ArrayList<>();

    private SharedPreferences sp;
    private UserDataManager userDataManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aks, container, false);

        sp=getActivity().getSharedPreferences("userInfo",0);
        if (userDataManager==null){
            userDataManager=new UserDataManager(getContext());
            userDataManager.openDatabase();
        }

        String username=sp.getString("USER_NAME",null);
        Cursor cursor=userDataManager.fetchAKSData(username);
        if (cursor!=null){
            Log.i("UserDataManager",String.valueOf(cursor.getCount()));
            int index=cursor.getColumnIndex("AKS_ID");
            do{
                AKS.add(cursor.getString(index));
            }while (cursor.moveToNext());
        }
        ListView listView = view.findViewById(R.id.list_aks);

        AksAdapter aksAdapter = new AksAdapter();

        listView.setAdapter(aksAdapter);

        return view;
    }

    private class AksAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return AKS.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.aks_layout, null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_id= (TextView)view.findViewById(R.id.textView_id);
            final AppCompatButton bind = (AppCompatButton)view.findViewById(R.id.bind);

            bind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), AksBind.class);
                    startActivity(intent);
                }
            });

            imageView.setImageResource(R.mipmap.logo);
            textView_id.setText(AKS.get(i));

            return view;
        }
    }

}
