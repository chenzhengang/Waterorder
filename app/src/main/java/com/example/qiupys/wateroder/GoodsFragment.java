package com.example.qiupys.wateroder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class GoodsFragment extends Fragment {

    int [] IMAGES = {R.drawable.water1, R.drawable.water2, R.drawable.water3, R.drawable.water4};
    String[] NAMES = {"农夫山泉", "百岁山", "娃哈哈矿泉水", "怡宝"};
    String[] PRICES = {"1.50", "3.00", "1.20", "1.80"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods, container, false);
        ListView listView = view.findViewById(R.id.list_goods);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);
        return view;
    }

    class CustomAdapter extends BaseAdapter {

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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_layout, null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_name = (TextView)view.findViewById(R.id.textView_name);
            TextView textView_price = (TextView)view.findViewById(R.id.textView_price);

            imageView.setImageResource(IMAGES[i]);
            textView_name.setText(NAMES[i]);
            textView_price.setText(PRICES[i]);

            return view;

        }
    }
}
