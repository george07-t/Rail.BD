package com.example.railbd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ShowTicketAfterPayment extends AppCompatActivity {
    private TextView togo1, date1, time1, coach1, seatnum1, seats1, price1, ticknum1, username, numbers;
    String ticketnum, togo, date, time, coach, seatnum, totalid, seats;
    private Button down, ok;
    PdfDocument document;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String userid;
    Bitmap bmp, scaledbmp;
    int pageHeight = 1120;
    int pagewidth = 792;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ticket_after_payment);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("userprofile");
        userid = user.getUid();
        seats = getIntent().getStringExtra("seats");
        togo = getIntent().getStringExtra("togo");
        time = getIntent().getStringExtra("time");
        date = getIntent().getStringExtra("date");
        coach = getIntent().getStringExtra("coach");
        totalid = getIntent().getStringExtra("total");
        seatnum = getIntent().getStringExtra("seatsnum");
        ticketnum = getIntent().getStringExtra("ticketnum");
        username = findViewById(R.id.username);
        numbers = findViewById(R.id.numbers);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.train);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
        databaseReference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile = snapshot.getValue(UserProfile.class);
                if (userProfile != null) {
                    String fullname = userProfile.name;
                    String pnumber = userProfile.number;
                    username.setText(fullname);
                    numbers.setText(pnumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowTicketAfterPayment.this, "Something Happen ", Toast.LENGTH_SHORT).show();
            }
        });
        togo1 = findViewById(R.id.togo1);
        date1 = findViewById(R.id.date1);
        time1 = findViewById(R.id.time1);
        coach1 = findViewById(R.id.coach1);
        seatnum1 = findViewById(R.id.seatnum1);
        seats1 = findViewById(R.id.seats1);
        price1 = findViewById(R.id.price1);
        ticknum1 = findViewById(R.id.ticknum);
        ticknum1.setText(ticketnum);
        togo1.setText(togo);
        date1.setText(date);
        time1.setText(time);
        coach1.setText(coach);
        seatnum1.setText(seatnum);
        seats1.setText(seats);
        price1.setText(totalid);
        down = findViewById(R.id.down);
        ok = findViewById(R.id.ok);
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createpdf();
                Toast.makeText(ShowTicketAfterPayment.this, "created", Toast.LENGTH_SHORT).show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowTicketAfterPayment.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createpdf() {

       /* PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageinfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page mypage1 = document.startPage(mypageinfo);
        Canvas canvas = mypage1.getCanvas();
        canvas.drawBitmap(s, 0, 0, paint);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(70);
        title.setColor(Color.rgb(21, 135, 108));
        canvas.drawText("Rail.BD" +
                "\nOnline Ticket Booking System", pagewidth, 370, title);

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(70);
        canvas.drawText("JOURNEY DETAILS", 1200 / 2, 370, title);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(50);
        canvas.drawText("Ticket Number: " + ticketnum, 200, 630, paint);
        canvas.drawText("Location(From-To) : " + togo, 200, 730, paint);
        canvas.drawText("Journey Date      : " + date, 200, 830, paint);
        canvas.drawText("Journey Time      : " + time, 200, 930, paint);
        canvas.drawText("Class Name        : S_CHAIR ", 200, 1030, paint);
        canvas.drawText("Coach Name        : " + coach, 200, 1130, paint);
        canvas.drawText("Coach Seats       : " + seatnum, 200, 1230, paint);
        canvas.drawText("Total Seats       : " + seats, 200, 1330, paint);
        canvas.drawText("FARE              : " + totalid, 200, 1430, paint);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(70);
        canvas.drawText("PASSENGER DETAILS", 1200 / 2, 1550, title);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(50);
        canvas.drawText("Name: " + username, 200, 1650, paint);
        canvas.drawText("Mobile Number: " + numbers, 200, 1750, paint);
        canvas.drawText("National ID Card: 1960488722", 200, 1850, paint);

        document.finishPage(mypage1);
        File file = new File(Environment.getExternalStorageDirectory(), "ticket1.pdf");
        try {
            document.writeTo(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();*/


        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, 56, 40, paint);

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("A portal for IT professionals.", 209, 100, title);
        canvas.drawText("Geeks for Geeks", 209, 80, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is sample document which we have created.", 396, 560, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        File file = new File(Environment.getExternalStorageDirectory(), "GFG.pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(ShowTicketAfterPayment.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
    }


    private boolean checkPermission() {
// checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
// requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {


                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


}