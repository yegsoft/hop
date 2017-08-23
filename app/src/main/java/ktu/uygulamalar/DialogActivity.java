package ktu.uygulamalar;

/**
 * Created by Yusuf on 15.8.2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity implements OnClickListener {

    Button ok_btn, cancel_btn;
    String message;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);

        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        cancel_btn = (Button) findViewById(R.id.cancel_btn_id);

        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        if(getIntent().getExtras() != null){
            String location = getIntent().getExtras().getString("alert");
            Log.d("Yusuf", "AAAAAAAAAAAAAAAAAAAAAAA "+location);
            message=location;
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.setText(message+" bölgesinde şarkı keşfedildi!");
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok_btn_id:

                Intent in = new Intent(this, FullscreenActivity.class);
                String intToSend = message;
                in.putExtra("location", intToSend);
                startActivity(in);

                this.finish();

                break;

            case R.id.cancel_btn_id:

                this.finish();

                break;
        }

    }

    void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

}
