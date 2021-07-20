package com.example.remindat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BottomFlag extends BottomSheetDialogFragment {
        EditText enter;
        Button save;
        TextView latText;
        TextView longText;

        ArrayList<Model> m1;
        Adapter adapter1;
        Sqlite s2;
        Button btn_location;
        MainActivity ma;
        double latitude;
        double longitude;

    public BottomFlag(Adapter adapter1, ArrayList<Model> m1, MainActivity mainActivity) {
        this.ma = mainActivity;
        this.adapter1 = adapter1;
        this.m1=m1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.bottom,container,false);
            enter=view.findViewById(R.id.edittext);
            save=view.findViewById(R.id.save);
            latText=view.findViewById(R.id.latText);
            longText=view.findViewById(R.id.longText);
            btn_location=view.findViewById(R.id.selLocation);

            btn_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(ma,MapActivity.class);
                    startActivityForResult(myIntent,1);
                }
            });


              save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(latitude == 0 && longitude == 0)
                   {
                       Toast.makeText(ma, "Choose the destination", Toast.LENGTH_SHORT).show();
                        return;
                   }

                    String taskentered= enter.getText().toString();
                    m1.clear();
                    s2=new Sqlite(getContext());
                    s2.insertdata(taskentered,0, latitude, longitude);
//                    Toast.makeText(ma, s2.toString(), Toast.LENGTH_SHORT).show();

                    Cursor c1=s2.getalldata();
                    if (c1.getCount()==0){
                        Log.e(TAG, "onClick: "+"notask" );
                    }
                    else{
                        while (c1.moveToNext()){
                            Model m=new Model(c1.getString(1),c1.getInt(2),c1.getInt(0),c1.getDouble(3),c1.getDouble(4));
//                            Toast.makeText(ma, m.toString(), Toast.LENGTH_SHORT).show();
                            m1.add(m);


                        }
                       // Log.e(TAG, "onClick: "+m1.size() );

                    }
                    Log.e(TAG, "onClick: "+m1.size() );

                        adapter1.notifyDataSetChanged();
                        dismiss();

                }
            });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                String strEditText = data.getStringExtra("lat_long_Value");
                String s[] = strEditText.split(" ");
                latitude = Double.parseDouble(s[0]);
                longitude = Double.parseDouble(s[1]);

                latText.setText("Latitude : " +latitude);
                longText.setText("Longitude : " +longitude);
            }
        }
    }

}