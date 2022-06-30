package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
         return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherRVModel model= weatherRVModelArrayList.get(position);
        holder.temptv.setText(model.getTemp()+" °C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditioniv);
        holder.windtv.setText(model.getWindspeed()+" km/h");
        SimpleDateFormat input= new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output= new SimpleDateFormat("hh:mm aa");
        try {
            Date t= input.parse(model.getTime());
            holder.timetv.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windtv,temptv,timetv;
        private ImageView conditioniv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windtv=itemView.findViewById(R.id.tvwindspeed);
            temptv=itemView.findViewById(R.id.tvtemp);
            timetv=itemView.findViewById(R.id.tvtime);
            conditioniv=itemView.findViewById(R.id.ivcondition);


        }
    }
}
