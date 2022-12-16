package com.example.railbd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TicketShowAdapter extends RecyclerView.Adapter<TicketShowAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserTicketDetails> list;

    public TicketShowAdapter(Context context, ArrayList<UserTicketDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TicketShowAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.ticketshow_sample, parent, false);

        return new TicketShowAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketShowAdapter.MyViewHolder holder, int position) {
        UserTicketDetails userTicketDetails=list.get(position);
        holder.ticketnumber.setText(userTicketDetails.getTicketnum());
        holder.togo.setText(userTicketDetails.getTogo());
        holder.datetime.setText(userTicketDetails.getDate()+"("+userTicketDetails.getTime()+" BST)");
        holder.seatnumber.setText(userTicketDetails.getSeats());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ticketnumber, togo, datetime, seatnumber;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketnumber = itemView.findViewById(R.id.ticketnumber);
            togo = itemView.findViewById(R.id.togo);
            datetime = itemView.findViewById(R.id.datetime);
            seatnumber = itemView.findViewById(R.id.seatnumber);
        }
    }

}
