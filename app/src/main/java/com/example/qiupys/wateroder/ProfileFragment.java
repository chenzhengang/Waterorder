package com.example.qiupys.wateroder;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ProfileFragment extends Fragment {
    private TextView user;
    private TextView phone;
    private TextView address;
    private TextView card;
    private TextView totalAKS;
    private TextView balance;

    private ImageView settings;

    private UserDataManager userDataManager;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sp=getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if (userDataManager==null){
            userDataManager=new UserDataManager(getContext());
            userDataManager.openDatabase();
        }

        user=view.findViewById(R.id.username);
        user.setText(sp.getString("USER_NAME",null));

        phone=view.findViewById(R.id.phone);
        address=view.findViewById(R.id.address);
        card=view.findViewById(R.id.bankcard);
        balance=view.findViewById(R.id.balance);
        totalAKS=view.findViewById(R.id.total_AKS);

        fill();

        settings=view.findViewById(R.id.setting);
        settings.setOnClickListener(listener);

        return view;
    }

    private void fill() {
        String username=sp.getString("USER_NAME",null);

        Cursor cursor=userDataManager.fetchUserData(username);
        Cursor cursor_bank=userDataManager.fetchCardData(username);
        Cursor cursor_aks=userDataManager.fetchAKSData(username);

        if (cursor!=null&&cursor_bank!=null&&cursor_aks!=null){
            int phone_index=cursor.getColumnIndex("Phone");
            int address_index=cursor.getColumnIndex("Address");
            int bankcard_index=cursor_bank.getColumnIndex("Card_Number");
            int balance_index=cursor_bank.getColumnIndex("Balance");

            String sphone=cursor.getString(phone_index);
            String saddress=cursor.getString(address_index);
            String sbank=cursor_bank.getString(bankcard_index);
            double sbalance=cursor_aks.getDouble(balance_index);

            if (sphone!=null){
                phone.setText(sphone);
            }
            if (saddress!=null){
                address.setText(saddress);
            }
            if (sbank!=null){
                card.setText(sbank);
            }

            balance.setText(String.valueOf(sbalance));
            totalAKS.setText(String.valueOf(cursor_aks.getCount()));
        }
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.setting:
                    Intent intent=new Intent(getActivity(),EditProfile.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
