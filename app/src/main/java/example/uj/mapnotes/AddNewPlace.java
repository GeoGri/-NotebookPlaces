package example.uj.mapnotes;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2015-12-20.
 */
public class AddNewPlace extends AppCompatActivity {

    @Bind(R.id.nameET)
    EditText namePlace;

    public static final String PLACE_NAME = "PLACE_NAME",
                                 BUNGLE ="Bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        ButterKnife.bind(this);
    }

    public void addPlace(View view){
        returnData(Activity.RESULT_OK);
    }

    public void cancelAddPlace(View view){
        returnData(Activity.RESULT_CANCELED);
    }

    public void returnData(int code){
        Intent resoultIntent = getIntent();
        Bundle extras = resoultIntent.getExtras();
        extras.putString(PLACE_NAME, namePlace.getText().toString());

        resoultIntent.putExtras(extras);
        setResult(code, resoultIntent);
        finish();
    }
}
