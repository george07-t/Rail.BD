package com.example.railbd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TrainSeatPlan extends AppCompatActivity {
    String togo, time, date, total, coachplan;
    private TextView showtrain, seats;
    GridLayout mainGrid;
    Double seatPrice = 500.00;
    Double totatCost = 0.0;
    int totalSeats = 0;
    TextView totalPrice;
    TextView totalBookedSeats;
    private Button buttonBook;
    int arr[];
    List<Integer> list = new ArrayList<>();
    String listString;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_seat_plan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mainGrid = (GridLayout) findViewById(R.id.mainGrid);
        totalBookedSeats = (TextView) findViewById(R.id.total_seats);
        totalPrice = (TextView) findViewById(R.id.total_cost);
        seats = findViewById(R.id.seats);
        buttonBook = (Button) findViewById(R.id.btnBook);
        showtrain = findViewById(R.id.showtrain);
        setToggleEvent(mainGrid);

        showtrain.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            togo = bundle.getString("loc");
            time = bundle.getString("timeshow");
            date = bundle.getString("date");
            coachplan = bundle.getString("coach");
            total = togo + time + date + coachplan;

        }
        showtrain.setVisibility(View.VISIBLE);
        showtrain.setText(togo + "\n" + date + " ( " + time + " BST )\n" + coachplan);

        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String totalPriceI = totalPrice.getText().toString().trim();
                String totalBookedSeatsI = totalBookedSeats.getText().toString().trim();


/*                PaymentDetail paymentDetail=new PaymentDetail(totalPriceI,totalBookedSeatsI,togo,time,date,coachplan,total);

                FirebaseUser user=firebaseAuth.getCurrentUser();
                databaseReference.child(user.getUid()).child("SeatDetails").setValue(paymentDetail);*/

                Intent intent = new Intent(TrainSeatPlan.this, GoForPayment.class);
                intent.putExtra("TOTALCOST", totalPriceI);
                intent.putExtra("TOTALSEAT", totalBookedSeatsI);
                intent.putExtra("togo", togo);
                intent.putExtra("time", time);
                intent.putExtra("date", date);
                intent.putExtra("coach", coachplan);
                intent.putExtra("total", total);
                intent.putExtra("seats",listString);


                startActivity(intent);

            }
        });


    }

    private void setToggleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#00d6a3"));
                        totatCost += seatPrice;
                        ++totalSeats;
                        list.add(finalI + 1);
                       // Toast.makeText(TrainSeatPlan.this, "You Selected Seat Number :" + (finalI + 1), Toast.LENGTH_SHORT).show();

                    } else {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        totatCost -= seatPrice;
                        --totalSeats;
                        int idx = 0;
                        while (idx < list.size()) {
                            if (list.get(idx) == finalI + 1) {
                                list.remove(idx);
                            } else {
                                idx++;
                            }
                        }
                       // Toast.makeText(TrainSeatPlan.this, "You Unselected Seat Number :" + (finalI + 1), Toast.LENGTH_SHORT).show();
                    }
                    totalPrice.setText("" + totatCost + "0");
                    totalBookedSeats.setText("" + totalSeats);
/*                    for(int i=0;i<arr.length;i++)
                    {
                        if(arr[i]==0){
                            continue;
                        }
                        list.add(arr[i]);
                    }*/
                    listString = list.toString();
                    listString = listString.substring(1, listString.length() - 1);
                    seats.setText(listString);
                }
            });
        }
    }
}