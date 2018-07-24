package com.example.qiupys.wateroder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.qiupys.wateroder.R;

public class FavoritesFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        Button btn= view.findViewById(R.id.button);
        btn.setOnClickListener(listener);

        return  view;
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("按钮","xxx");
            Intent intent = new Intent(getActivity(),ReadMUActivity.class);
            startActivity(intent);
        }
    };
}
