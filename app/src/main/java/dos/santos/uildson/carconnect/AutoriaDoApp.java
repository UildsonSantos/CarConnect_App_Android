package dos.santos.uildson.carconnect;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AutoriaDoApp extends AppCompatActivity {

    TextView textViewSobreApp;

    public static void sobre(AppCompatActivity activity) {

        Intent intent = new Intent(activity, AutoriaDoApp.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoria_do_app);

        setTitle(getString(R.string.sobre));

        textViewSobreApp = findViewById(R.id.textViewSobreApp);
        String text = getResources().getString(R.string.descricao_app);

        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf("CarConnect");
        int endIndex = startIndex + "CarConnect".length();

        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.pumpkin));
        spannableString.setSpan(foregroundSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        textViewSobreApp.setText(spannableString);
    }
}