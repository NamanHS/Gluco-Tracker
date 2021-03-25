package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class viewGraph extends AppCompatActivity {

    private LineChart mpLineChart;
    FirebaseUser firebaseUser;
    String regNo;
    FirebaseFirestore firebaseFirestore;
    Boolean isArrayCreated1 = false;
    ArrayList<Entry> datavalsBeforeMeal = null;
    ArrayList<Entry> datavalsAfterMeal = null;

    ProgressBar pgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph);

        pgressBar = (ProgressBar) findViewById(R.id.pgressBar);


        //fetch reg No of user for query
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        regNo = firebaseUser.getUid().toString();

        //
        firebaseFirestore = FirebaseFirestore.getInstance();
        datavalsBeforeMeal = new ArrayList<>();
        datavalsAfterMeal = new ArrayList<>();
        firebaseFirestore.collection("GlucoReadingDB").whereEqualTo("regNo", regNo).orderBy("date", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("hey", "hey");
                            isArrayCreated1 = true;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Timestamp timestamp = (Timestamp) document.getData().get("date");
                                assert timestamp != null;
                                Date date = timestamp.toDate();
                                date.getTime();
                                //  datavals1.add(new Entry(((date.getTime())/1000),(Float) document.get("glucoReadingEntered")));

                                String showDate = date.toString();
                                String finDate = showDate.substring(4, 11) + showDate.substring(showDate.length() - 5, showDate.length());
                                Log.i("date", finDate);
                                Log.i("gluco", document.get("glucoReadingEntered").toString() + " mg/dL");
                                Long lg = Long.valueOf(date.getTime() / 1000);
                                float fg = (float) lg;
                                float reads = Float.parseFloat(Objects.requireNonNull(document.get("glucoReadingEntered")).toString());
                                if (document.get("interval").toString().equals("Before Meal")) {
                                    datavalsBeforeMeal.add(new Entry(fg, reads));

                                    Log.i("bm", finDate + document.get("glucoReadingEntered").toString());

                                } else {
                                    datavalsAfterMeal.add(new Entry(fg, reads));
                                    Log.i("AAm", finDate + document.get("glucoReadingEntered").toString());
                                }
                            }

                            mpLineChart = (LineChart) findViewById(R.id.line_chart);
                            mpLineChart.setPinchZoom(true);
                            mpLineChart.setTouchEnabled(true);
                            mpLineChart.setScaleEnabled(true);
                            mpLineChart.setDragEnabled(true);


                            LineDataSet lineDataSet1 = new LineDataSet(datavalsBeforeMeal, "BeforeMeal");
                            lineDataSet1.setColor(Color.parseColor("#FF0000"));
                            lineDataSet1.setCircleRadius(7);
                            lineDataSet1.setCircleColor(Color.parseColor("#FF0000"));
                            LineDataSet lineDataSet2 = new LineDataSet(datavalsAfterMeal, "After Meal");
                            lineDataSet2.setCircleColor(Color.parseColor("#000000"));
                            lineDataSet2.setColor(Color.parseColor("#000000"));
                            lineDataSet2.setCircleRadius(7);
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(lineDataSet1);
                            dataSets.add(lineDataSet2);

                            XAxis xAxis = mpLineChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                            mpLineChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());

                            LineData data = new LineData(dataSets);
                            mpLineChart.setData(data);
//                            mpLineChart.setVisibleXRangeMaximum(20);
//                            mpLineChart.moveViewToX(10);
                            mpLineChart.invalidate();
                            pgressBar.setVisibility(View.INVISIBLE);
                            mpLineChart.setVisibility(View.VISIBLE);
                        } else {
                            isArrayCreated1 = false;
                            Toast toast = Toast.makeText(getApplicationContext(), "SERVER ERROR\nPLEASE TRY AGAIN LATTER", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

    }
}



