package pl.edu.agh.activityrecognition;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int N_SAMPLES = 200;
    private static final String[] labels = {"Running", "Sitting", "Standing", "Walking"};

    private static final List<Float> x = new ArrayList<>();
    private static final List<Float> y = new ArrayList<>();
    private static final List<Float> z = new ArrayList<>();

    private HorizontalBarChart barChart;
    private BarData barData;
    private TextView activityType;
    private ImageView activityImage;

    private TensorFlowClassifier classifier;
    private float[] results;

    private TextToSpeech textToSpeech;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barChart = findViewById(R.id.barchart);
        activityType = findViewById(R.id.activityType);
        activityImage = findViewById(R.id.activityImage);

        classifier = new TensorFlowClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);

        initBarChart();
    }

    private void initBarChart() {
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(false);

        // X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Y-Axis
        List<YAxis> yAxes = Arrays.asList(barChart.getAxisLeft(), barChart.getAxisRight());
        for (YAxis yAxis : yAxes) {
            yAxis.setAxisMinimum(0f);
            yAxis.setAxisMaximum(1.01f);
            yAxis.setGranularity(0.1f);
        }

        // set mock data
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 0.70f));
        entries.add(new BarEntry(1, 0.20f));
        entries.add(new BarEntry(2, 0.10f));
        entries.add(new BarEntry(3, 0.5f));

        BarDataSet dataSet = new BarDataSet(entries, "Activity Recognition");
        dataSet.setValues(entries);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.invalidate();
    }

    @Override
    public void onInit(int status) {
        scheduleTimerTTS();
    }

    @Override
    protected void onPause() {
        cancelTimerTTS();
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        scheduleTimerTTS();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();

        x.add(event.values[0]);
        y.add(event.values[1]);
        z.add(event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // nothing here
    }

    private void activityPrediction() {
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {
            List<Float> data = new ArrayList<>();
            data.addAll(x);
            data.addAll(y);
            data.addAll(z);

            results = classifier.predictProbabilities(toFloatArray(data));

            updateData();

            x.clear();
            y.clear();
            z.clear();
        }
    }

    private void updateData() {
        // BarChart
        IBarDataSet dataSet = barData.getDataSetByIndex(0);
        for (int i = 0; i < results.length; i++) {
            dataSet.getEntryForIndex(i).setY(round(results[i]));
        }

        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.animateY(1000);

        // UI
        String activity = currentActivity(results);
        activityType.setText(activity);

        String imageName = "ic_" + activity.toLowerCase();
        activityImage.setImageResource(getResources().getIdentifier(imageName, "mipmap", getPackageName()));
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = f != null ? f : Float.NaN;
        }

        return array;
    }

    private static float round(float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private void scheduleTimerTTS() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (results == null || results.length == 0) {
                        return;
                    }
                    textToSpeech.speak(currentActivity(results), TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
                }
            }, 2500, 6000);
        }
    }

    private String currentActivity(float[] results) {
        float max = -1;
        int idx = -1;
        for (int i = 0; i < results.length; i++) {
            if (results[i] > max) {
                idx = i;
                max = results[i];
            }
        }

        return labels[idx];
    }

    private void cancelTimerTTS() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
