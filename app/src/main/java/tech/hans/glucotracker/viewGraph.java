package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class viewGraph extends AppCompatActivity {

    private LineChart mpLineChart;
    FirebaseUser firebaseUser;
    String regNo;
    FirebaseFirestore firebaseFirestore;
    ArrayList<Entry> datavalsBeforeMeal = null;
    ArrayList<Entry> datavalsAfterMeal = null;

    ProgressBar pgressBar;
    TextView graphmsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph);

        pgressBar = (ProgressBar) findViewById(R.id.pgressBar);
        graphmsg = (TextView) findViewById(R.id.graphmsg);
        graphmsg.setVisibility(View.INVISIBLE);


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
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Timestamp timestamp = (Timestamp) document.getData().get("date");
                                assert timestamp != null;
                                Date date = timestamp.toDate();
                                date.getTime();
                                Long lg = Long.valueOf(date.getTime() / 1000);
                                float fg = (float) lg;
                                float reads = Float.parseFloat(Objects.requireNonNull(document.get("glucoReadingEntered")).toString());
                                if (document.get("interval").toString().equals("Before Meal")) {
                                    datavalsBeforeMeal.add(new Entry(fg, reads));
                                } else {
                                    datavalsAfterMeal.add(new Entry(fg, reads));
                                }
                            }

                            mpLineChart = (LineChart) findViewById(R.id.line_chart);
                            mpLineChart.setPinchZoom(true);
                            mpLineChart.setTouchEnabled(true);
                            mpLineChart.setScaleEnabled(true);
                            mpLineChart.setDragEnabled(true);

                            //before meal max and min limit lines

                            LimitLine bfmax = new LimitLine(130f,"Before Meal Maximum");
                            bfmax.setLineWidth(4f);
                            bfmax.enableDashedLine(10f,10f,0f);
                            bfmax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            bfmax.setTextSize(10f);
                            bfmax.setTextColor(Color.parseColor("#0000FF"));

                            LimitLine bfmin = new LimitLine(100f,"Before Meal Minimum");
                            bfmin.setLineWidth(4f);
                            bfmin.enableDashedLine(10f,10f,0f);
                            bfmin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            bfmin.setTextSize(10f);
                            bfmin.setTextColor(Color.parseColor("#0000FF"));

                            //after meal

                            LimitLine afmax = new LimitLine(180f,"After Meal Maximum");
                            afmax.setLineWidth(4f);
                            afmax.setLineColor(Color.parseColor("#000000"));
                            afmax.enableDashedLine(10f,10f,0f);
                            afmax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            afmax.setTextSize(10f);
                            afmax.setTextColor(Color.parseColor("#0000FF"));


                            YAxis leftAxis = mpLineChart.getAxisLeft();
                            leftAxis.setAxisMinimum(10f);
                            leftAxis.removeAllLimitLines();
                            leftAxis.addLimitLine(bfmax);
                            leftAxis.addLimitLine(bfmin);
                            leftAxis.addLimitLine(afmax);




                            //After meal max and min limit lines

                           // LimitLine afmax = new LimitLine()



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
                            mpLineChart.getDescription().setEnabled(false);



                            if(((data.getXMax()-data.getXMin())>0)){
                                mpLineChart.invalidate();
                                Log.i("hello",Integer.toString(data.getEntryCount()));
                                mpLineChart.setVisibleXRangeMaximum((data.getXMax()-data.getXMin())/4);
                                pgressBar.setVisibility(View.INVISIBLE);
                                mpLineChart.setVisibility(View.VISIBLE);
                            }else{
                                pgressBar.setVisibility(View.INVISIBLE);
                                graphmsg.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "SERVER ERROR\nPLEASE TRY AGAIN LATTER", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

    }
}



