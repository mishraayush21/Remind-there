package com.example.remindat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Adapter extends RecyclerView.Adapter<Adapter.Viewholder> {
    Context context;
    ArrayList<Model> models;
    SetData s1;

    public Adapter(Context context, ArrayList<Model> models, SetData s1) {
        this.context = context;
        this.models = models;
        this.s1=s1;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        final Model temp=models.get(position);
        holder.checkBox.setText(models.get(position).getTask());
        Log.e(TAG, "onBindViewHolder: "+temp.getStatus() );


        if(temp.getStatus()==1){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=temp.getId();
                Log.e(TAG, "onClick: "+id );
                s1.delete(id);
            }


        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=temp.getId();
                Log.e(TAG,"=-"+holder.checkBox.isChecked());
                if (holder.checkBox.isChecked()){
                    s1.update(id,1);
                    holder.checkBox.setChecked(true);

                }else {
                    s1.update(id,0);
                    holder.checkBox.setChecked(false);
                };
            }
        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        ImageView img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.checkbox);
            img=itemView.findViewById(R.id.delete);
        }
    }
}
