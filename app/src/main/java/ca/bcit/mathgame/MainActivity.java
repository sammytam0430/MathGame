package ca.bcit.mathgame;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    public static final int NOTIFICATION_ID = 5453;
    private static final String CHANNEL_ID = "5453";

    TextView textViewScore;
    Button buttonReset;

    TextView textViewFirstNumber;
    TextView textViewOperator;
    TextView textViewSecondNumber;

    Button buttonAnswer1;
    Button buttonAnswer2;
    Button buttonAnswer3;

    TextView textViewResult;

    int round;
    int score;
    int first;
    int second;
    int answer;
    String[] operators;
    String operator;
    int position;
    boolean end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        round = 0;
        score = 0;
        position = 0;
        end = false;
        operators = new String[]{"+", "-", "x", "/"};

        textViewScore = findViewById(R.id.textViewScore);
        buttonReset = findViewById(R.id.buttonReset);
        textViewFirstNumber = findViewById(R.id.textViewFirstNumber);
        textViewOperator = findViewById(R.id.textViewOperator);
        textViewSecondNumber = findViewById(R.id.textViewSecondNumber);
        buttonAnswer1 = findViewById(R.id.buttonAnswer1);
        buttonAnswer2 = findViewById(R.id.buttonAnswer2);
        buttonAnswer3 = findViewById(R.id.buttonAnswer3);
        textViewResult = findViewById(R.id.textViewResult);

        buttonReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reset();
            }
        });

        View.OnClickListener answer = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(v);
            }
        };

        buttonAnswer1.setOnClickListener(answer);
        buttonAnswer2.setOnClickListener(answer);
        buttonAnswer3.setOnClickListener(answer);

        start();
    }

    void start() {
        round++;
        position = (int) (Math.random() * 4);
        operator = operators[position];

        boolean verified = false;
        int range = 0;

        while (!verified) {
            first = (int) (Math.random() * 101);
            second = (int) (Math.random() * 101);

            if (position == 1) {
                verified = first > second;
            } else if (position == 3) {
                verified = second != 0 && first % second == 0;
            } else {
                verified = true;
            }
        }

        textViewFirstNumber.setText(String.valueOf(first));
        textViewSecondNumber.setText(String.valueOf(second));
        textViewOperator.setText(operator);

        switch (position) {
            case 0:
                answer = first + second;
                range = 200;
                break;
            case 1:
                answer = first - second;
                range = 100;
                break;
            case 2:
                answer = first * second;
                range = 10000;
                break;
            case 3:
                answer = first / second;
                range = 100;
                break;
        }

        switch ((int) (Math.random() * 3)) {
            case 0:
                buttonAnswer1.setText(String.valueOf(answer));
                buttonAnswer2.setText(String.valueOf((int) (Math.random() * range)));
                buttonAnswer3.setText(String.valueOf((int) (Math.random() * range)));
                break;
            case 1:
                buttonAnswer2.setText(String.valueOf(answer));
                buttonAnswer1.setText(String.valueOf((int) (Math.random() * range)));
                buttonAnswer3.setText(String.valueOf((int) (Math.random() * range)));
                break;
            case 2:
                buttonAnswer3.setText(String.valueOf(answer));
                buttonAnswer1.setText(String.valueOf((int) (Math.random() * range)));
                buttonAnswer2.setText(String.valueOf((int) (Math.random() * range)));
                break;
        }
    }

    void checkAnswer(View v) {
        Button buttonAnswer = findViewById(v.getId());
        int answer = Integer.parseInt(buttonAnswer.getText().toString());
        if (round <= 5 && !end) {
            if (answer == this.answer) {
                textViewResult.setText(getString(R.string.correct));
                score++;
            } else {
                textViewResult.setText(getString(R.string.wrong));
            }

            String runningScore = score + "/5";
            textViewScore.setText(runningScore);
            if (round == 5) {
                end = true;

                createNotificationChannel();

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle(getString(R.string.result))
                        .setContentText(getString(R.string.your_score_is) + score + "/5!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } else {
                start();
            }
        }
    }

    void reset() {
        round = 0;
        score = 0;
        end = false;
        textViewResult.setText(getString(R.string.result));
        textViewScore.setText(score + "/5");
        start();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 1";
            String description = "For testing";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
