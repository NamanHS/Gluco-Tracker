package tech.hans.glucotracker;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;

import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Objects;

public class generateReport extends AppCompatActivity {


    EditText namedReport;
    FirebaseUser firebaseUser;
    String regNo;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference myRef;
    FirebaseDatabase database;


    String dateCompare = "";
    boolean isFirstEntry = true;
    String greetName,email;

    MediaPlayer mp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        mp = MediaPlayer.create(this,getResources().getIdentifier("aknowledge", "raw", getPackageName()));
        progressBar = (ProgressBar) findViewById(R.id.progressbarview);
        progressBar.setVisibility(View.INVISIBLE);

        namedReport = (EditText) findViewById(R.id.namedReport);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        regNo = firebaseUser.getUid().toString();

        database = FirebaseDatabase.getInstance("https://gluco-tracker-app-default-rtdb.firebaseio.com/");
        myRef = database.getReference("UserData");

        firebaseFirestore = FirebaseFirestore.getInstance();

        myRef.child(regNo).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                greetName = Objects.requireNonNull(dataSnapshot.getValue()).toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child(regNo).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void createPDF() throws FileNotFoundException {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path,namedReport.getText().toString() + ".pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        final Document documentFile = new Document(pdfDocument);

        final Paragraph paragraph = new Paragraph();

        Text text1 = new Text("PATIENT NAME : ").setBold();
        Text text2 = new Text(greetName+ "\n").setBold();
        paragraph.add(text1).add(text2);

        Text text3 = new Text("PATIENT EMAIL ID : ").setBold();
        Text text4 = new Text(email + "\n\n\n").setUnderline();
        paragraph.add(text3).add(text4);



        float[] coloumnWidth = {100f,100f,100f,100f};
        final Table table = new Table(coloumnWidth);

        table.addCell("Date");
        table.addCell("Interval");
        table.addCell("Blood Glucose Reading");
        table.addCell("Notes");

        firebaseFirestore.collection("GlucoReadingDB").whereEqualTo("regNo",regNo).orderBy("date", Query.Direction.DESCENDING).orderBy("interval").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(getApplicationContext(), "INSUFFICIENT DATA TO CREATE REPORT", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {


                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    Timestamp timestamp = (Timestamp) document.getData().get("date");


                                    assert timestamp != null;
                                    Date date = timestamp.toDate();
                                    String showDate = date.toString();
                                    String finDate = showDate.substring(4, 11) + showDate.substring(showDate.length() - 5, showDate.length());
                                    if (!isFirstEntry) {
                                        if (!finDate.equals(dateCompare)) {
                                            table.addCell("\n");
                                            table.addCell("\n");
                                            table.addCell("\n");
                                            table.addCell("\n");
                                        }
                                    }

                                    dateCompare = finDate;
                                    table.addCell(String.valueOf(finDate));
                                    table.addCell(document.get("interval").toString());
                                    table.addCell(document.get("glucoReadingEntered").toString() + " mg/dL");
                                    if (document.get("optionalNotes").toString().equals("No Notes Added")) {
                                        table.addCell("");
                                    } else {
                                        table.addCell(document.get("optionalNotes").toString());
                                    }
                                    isFirstEntry = false;

                                }
                                documentFile.add(paragraph);
                                documentFile.add(table);
                                documentFile.close();

                                mp.start();
                                new AlertDialog.Builder(generateReport.this).setCancelable(false)
                                        .setTitle("PDF REPORT GENERATED")
                                        .setMessage(namedReport.getText().toString() + ".pdf is Downloaded,\nYou will find this in your Downloads")
                                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).create().show();

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        }else{
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(getApplicationContext(), "SERVER ERROR\nPlease try again latter", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                    }
                });
    }

    public void generateReport(View v){
        if(namedReport.getText().toString().isEmpty()){
            namedReport.setError("PLEASE NAME YOUR PDF REPORT FILE");
        }else {
            progressBar.setVisibility(View.VISIBLE);
            try {
                isFirstEntry = true;
                dateCompare = "";
                createPDF();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



}