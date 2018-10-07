package com.example.user.grabngo.Admin;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.user.grabngo.R;

public class DiscountDetailActivity extends AppCompatActivity {

    private TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_detail);

        textViewDescription = (TextView)findViewById(R.id.description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        setTitle("Promotion Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
