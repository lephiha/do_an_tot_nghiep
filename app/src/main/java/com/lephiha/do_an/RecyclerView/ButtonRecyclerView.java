package com.lephiha.do_an.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.lephiha.do_an.CallVideo.ChooseDoctorActivity;
import com.lephiha.do_an.Model.Setting;
import com.lephiha.do_an.R;
import com.lephiha.do_an.SearchPage.SearchActivity;
import com.lephiha.do_an.ServicePage.ServiceActivity;

import java.util.List;

public class ButtonRecyclerView extends RecyclerView.Adapter<ButtonRecyclerView.ViewHolder> {

    private Context context;
    private List<Setting> list;

    public ButtonRecyclerView(Context context, List<Setting> list)
    {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_view_element_button, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Setting option = list.get(position);
        String name = option.getName();
        String id = option.getId();
        int icon = option.getIcon();

        holder.name.setText(name);
        holder.button.setImageResource( icon );
        holder.button.setOnClickListener(view -> {
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            Intent intent;
            String filterKey  = context.getString(R.string.service);

            switch (id) {
                case "specialityExamination":
                    intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("filterKey", filterKey );
                    context.startActivity(intent);
                    break;

                case "generalExamination":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "1" );
                    context.startActivity(intent);
                    break;

                case "heartExamination":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "6" );
                    context.startActivity(intent);
                    break;

                case "pregnantExamination":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "8" );
                    context.startActivity(intent);
                    break;

                case "toothExamination":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "10" );
                    context.startActivity(intent);
                    break;

                case "eyeExamination":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "11" );
                    context.startActivity(intent);
                    break;

                case "medicalTestExamination":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "17" );
                    context.startActivity(intent);
                    break;

                case "covid19":
                    intent = new Intent(context, ServiceActivity.class);
                    intent.putExtra("serviceId", "22" );
                    context.startActivity(intent);
                    break;

                case "call_video":
                    intent = new Intent(context, ChooseDoctorActivity.class);
                    intent.putExtra("serviceId", "23" );
                    context.startActivity(intent);
                    break;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout layout;
        private final androidx.appcompat.widget.AppCompatImageButton button;
        private final TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.elementLinearLayout);
            button = itemView.findViewById(R.id.elementButton);
            name = itemView.findViewById(R.id.elementName);
        }
    }
}
