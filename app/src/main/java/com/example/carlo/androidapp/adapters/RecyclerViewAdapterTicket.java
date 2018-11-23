package com.example.carlo.androidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.actividades.pay_getNames;

import java.util.ArrayList;

public class RecyclerViewAdapterTicket extends RecyclerView.Adapter<RecyclerViewAdapterTicket.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapterTick";
    private ArrayList<String> mTicketTypes = new ArrayList<>();
    private ArrayList<Double> mTicketPrice = new ArrayList<>();
    private Context mContext;
    private long unixtimestamp;
    private Intent mintent;
    private int tourid;


    public RecyclerViewAdapterTicket(Context mContext,ArrayList<String> mTicketTypes, ArrayList<Double> mTicketPrice, long unixtimestamp,int tourid) {
        Log.d(TAG, "RecyclerViewAdapterTicket: called.");
        this.mTicketTypes = mTicketTypes;
        this.mTicketPrice = mTicketPrice;
        this.mContext = mContext;
        this.unixtimestamp = unixtimestamp;
        this.tourid =tourid;
        mintent = new Intent(mContext,pay_getNames.class);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ticketcountselection,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        String price = mTicketPrice.get(i).toString();
        viewHolder.ticketType.setText(mTicketTypes.get(i));
        viewHolder.ticketPrice.setText(price);

        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              int ticketcount = Integer.parseInt(viewHolder.ticketNum.getText().toString())+1;
              if(ticketcount>10)
                  ticketcount-=1;
              String count = ticketcount+"";
              viewHolder.ticketNum.setText(count);
              mintent.putExtra(mTicketTypes.get(i),ticketcount);

            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ticketcount = Integer.parseInt(viewHolder.ticketNum.getText().toString())-1;
                if(ticketcount<0)
                    ticketcount=0;
                String count = ticketcount+"";
                viewHolder.ticketNum.setText(count);
                mintent.putExtra(mTicketTypes.get(i),ticketcount);
            }
        });


    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called." + mTicketTypes.size());
        return mTicketTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView ticketType, ticketPrice, ticketNum;
        Button plus, minus;
        RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketType = itemView.findViewById(R.id.ticketType);
            ticketPrice = itemView.findViewById(R.id.ticketPrice);
            ticketNum = itemView.findViewById(R.id.numOfTickets);
            plus = itemView.findViewById(R.id.moreTickets);
            minus = itemView.findViewById(R.id.lessTickets);
            relativeLayout = itemView.findViewById(R.id.relLayouttickets);
            
        }
    }

    public void startIntent(){
        mintent.putExtra("dateselected",unixtimestamp);
        mintent.putExtra("tour_id", tourid);
        mContext.startActivity(mintent);
    }
}
