package be.heh.pfa_pittonet_benjamin.process;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import be.heh.pfa_pittonet_benjamin.MenuActivity;
import be.heh.pfa_pittonet_benjamin.R;
import be.heh.pfa_pittonet_benjamin.database.User;

public class LevelSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText _edIP, _edRack, _edSlot;
    private Button _btnApiCon;
    private SharedPreferences preferences = null;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.preferences = getSharedPreferences("level", MODE_PRIVATE);
        this._edIP = findViewById(R.id.edIP);
        this._edIP.setText(this.preferences.getString("IP", ""));
        this._edRack = findViewById(R.id.edRack);
        this._edRack.setText(this.preferences.getString("RACK", ""));
        this._edSlot = findViewById(R.id.edSlot);
        this._edSlot.setText(this.preferences.getString("SLOT", ""));

        this._btnApiCon = findViewById(R.id.btnApiCon);
        this._btnApiCon.setOnClickListener(this);

        Bundle userDetail = this.getIntent().getExtras();
        this.user = (User) userDetail.getSerializable("user");
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btnApiCon:
                if (Patterns.IP_ADDRESS.matcher(this._edIP.getText().toString()).matches()) {
                    if (this._edRack.getText().toString().isEmpty() || this._edSlot.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Slot ou rack invalide.", Toast.LENGTH_SHORT).show();
                    } else {
                        this.preferences.edit()
                                .putString("IP", this._edIP.getText().toString())
                                .putString("RACK", this._edRack.getText().toString())
                                .putString("SLOT", this._edSlot.getText().toString())
                                .apply();
                        bundle = new Bundle();
                        bundle.putSerializable("user", this.user);
                        Intent level = new Intent(this, LevelControlActivity.class);
                        level.putExtras(bundle);
                        startActivity(level);
                        finish();
                    }
                } else Toast.makeText(this, "IP non valide", Toast.LENGTH_SHORT).show();
        }
    }
}