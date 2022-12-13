package com.example.railbd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MakePayment extends AppCompatActivity {
    private TextView t1;
    Button buy, cancel;
    AlertDialog.Builder alertBuilder;
    UserTicketDetails userTicketDetails;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);
        t1 = findViewById(R.id.pay);
        final String total = getIntent().getStringExtra("TOTALCOSTI");
        final String seats = getIntent().getStringExtra("TOTALSEAT");
        final String togo = getIntent().getStringExtra("togo");
        final String time = getIntent().getStringExtra("time");
        final String date = getIntent().getStringExtra("date");
        final String coach = getIntent().getStringExtra("coach");
        final String totalid = getIntent().getStringExtra("total");
        final String seatnum = getIntent().getStringExtra("seats");
        databaseReference = FirebaseDatabase.getInstance().getReference("userticket");
        t1.setText("From->To: " + togo + "\nJourney Date: " + date + "(" + time + "BST)\nCoach Name: " + coach + "\nSeat No: " + seatnum + "\nTotal Seats: " + seats + "\nFare: " + total + " TK(BDT)");
        buy = findViewById(R.id.btnBuy);
        cancel = findViewById(R.id.button05);
        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(MakePayment.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(MakePayment.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + cardForm.getCardNumber() + "\n" +
                            "Card expiry date: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "Card CVV: " + cardForm.getCvv() + "\n" +
                            "Postal code: " + cardForm.getPostalCode() + "\n" +
                            "Phone number: " + cardForm.getMobileNumber() + "\nFare: " + total + " TK(BDT)");
                    String cardnumber = cardForm.getCardNumber();
                    String moblie = cardForm.getMobileNumber();
                    String cardname = cardForm.getCardholderName();
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            String ticketnum=databaseReference.push().getKey();
                            userTicketDetails = new UserTicketDetails(togo, date, time, coach, seatnum, total, cardnumber, moblie, cardname,ticketnum);
                            FirebaseDatabase.getInstance().getReference("userticket").child(ticketnum).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userTicketDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(MakePayment.this, "Thank you for purchase", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MakePayment.this, MainActivity.class);
                                                startActivity(intent);

                                                finish();
                                            } else {
                                                Toast.makeText(MakePayment.this, "Unsuccessful Payment", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(MakePayment.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MakePayment.this, GoForPayment.class);
                startActivity(intent);
                finish();
            }
        });
    }
}