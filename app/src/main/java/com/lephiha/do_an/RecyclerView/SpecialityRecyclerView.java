package com.lephiha.do_an.RecyclerView;

import android.content.Context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.Model.Speciality;
import com.lephiha.do_an.R;
import com.lephiha.do_an.SpecialityPage.SpecialityActivity;
import com.lephiha.do_an.configAPI.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;
import org.w3c.dom.Text;

public class SpecialityRecyclerView extends RecyclerView.Adapter<SpecialityRecyclerView.ViewHolder> {

    private Context context;
    private List<Speciality> list;
    private int layoutElemet; //layout cua recyclerview_special 1 va 2

    public SpecialityRecyclerView(Context context, List<Speciality> list, int layoutElemet) {
        this.context = context;
        this.list = list;
        this.layoutElemet = layoutElemet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layoutElemet, parent, false);

        return new SpecialityRecyclerView.ViewHolder(view);
    }
    @Override
    @SuppressLint("ResourceType")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Speciality element = list.get(position);
        String uploadUri = Constant.UPLOAD_URI();

        int id = element.getId();
        String name = element.getName();
        String image = element.getImage().length() > 0 ?
                uploadUri + element.getImage() : context.getString(R.drawable.default_speciality);


        if( element.getImage().length() > 0)
        {
            Picasso.get().load(image).into(holder.image);
        }

        holder.name.setText(name);
        holder.layout.setOnClickListener(view->{
            Intent intent = new Intent(context, SpecialityActivity.class);
            intent.putExtra("specialityId",String.valueOf(id) );
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout layout;
        private ImageView image;
        private TextView name;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.elementLayout);
            image = itemView.findViewById(R.id.elementImage);
            name = itemView.findViewById(R.id.elementName);
        }
    }

}
