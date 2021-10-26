package be.heh.pfa_pittonet_benjamin.process;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import be.heh.pfa_pittonet_benjamin.database.User;
import be.heh.pfa_pittonet_benjamin.R;
import be.heh.pfa_pittonet_benjamin.SymaticS7.S7ReadTask;
import be.heh.pfa_pittonet_benjamin.SymaticS7.S7WriteTask;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TabletPackagingActivity extends AppCompatActivity implements View.OnClickListener {

    private S7ReadTask readTaskS7;
    private S7WriteTask writeTaskS7;
    private User user;

    private GridLayout gridWrite;
    private TextView tv_title, tv_bottles, tv_pill, tv_operation, tv_motor;
    private ArrayList<TextView> tvs = new ArrayList<>();
    private ArrayList<RadioButton> radioButtons = new ArrayList<>();
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private EditText writeValue;
    private Button modify, send;

    private SharedPreferences preferences = null;
    private String _ip, _rack, _slot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);
        try {
            this.preferences = getSharedPreferences("tablet", MODE_PRIVATE);
            this._ip = this.preferences.getString("IP", "");
            this._rack = this.preferences.getString("RACK", "");
            this._slot = this.preferences.getString("SLOT", "");
            System.out.println(this._ip + " " + this.preferences);
        } catch (Exception e) {
        }

        Bundle userDetail = this.getIntent().getExtras();
        this.user = (User) userDetail.getSerializable("user");

        this.gridWrite = findViewById(R.id.gridWrite);

        this.tv_title = findViewById(R.id.tv_info);
        this.tv_bottles = findViewById(R.id.tv_tp_bottles);
        this.tv_pill = findViewById(R.id.tv_tp_pilldemand);
        this.tv_operation = findViewById(R.id.tv_tp_inoperation);
        this.tv_motor = findViewById(R.id.tv_tp_bandmotor);
        this.writeValue = findViewById(R.id.et_writeValue);

        this.rb1 = findViewById(R.id.radio1);
        this.rb2 = findViewById(R.id.radio2);
        this.rb3 = findViewById(R.id.radio3);
        this.rb4 = findViewById(R.id.radio4);
        this.rb5 = findViewById(R.id.radio5);

        this.tvs.add(tv_title);
        this.tvs.add(tv_bottles);
        this.tvs.add(tv_pill);
        this.tvs.add(tv_operation);
        this.tvs.add(tv_motor);

        radioButtons.add(rb1);
        radioButtons.add(rb2);
        radioButtons.add(rb3);
        radioButtons.add(rb4);
        radioButtons.add(rb5);

        this.modify = findViewById(R.id.btnModify);
        this.modify.setOnClickListener(this);
        this.send = findViewById(R.id.bt_send);
        this.send.setOnClickListener(this);

        if (this.user.getIsAdmin() == 0){
            this.modify.setVisibility(View.GONE);
        }

        View v = this.modify;
        startRead(v);
    }

    @Override
    public void onBackPressed() {
        readTaskS7.Stop();
        finish();
    }

    public void startRead(View v){
        readTaskS7 = new S7ReadTask(v, tvs);
        readTaskS7.Start(this._ip, this._rack, this._slot);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnModify:
                if (this.modify.getText().equals("Modifier Valeur")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.modify.setText("Arret modifications");
                    if (this.user.getIsAdmin() == 1) {
                        gridWrite.setVisibility(View.VISIBLE);
                    }
                } else {
                    this.modify.setText("Modifier Valeur");
                    gridWrite.setVisibility(View.GONE);
                    this.tv_bottles.setText(null);
                    this.tv_pill.setText(null);
                    this.tv_operation.setText(null);
                    this.tv_motor.setText(null);
                }
                break;

            case R.id.bt_send:
                Boolean checked = false;
                for (RadioButton r : radioButtons) if (r.isChecked()) checked = true;
                if (this.writeValue.getText().toString().isEmpty()) Toast.makeText(this, "Veuillez entrez une valeur", Toast.LENGTH_SHORT).show();
                else if (!checked) Toast.makeText(this, "Veuillez choisir un DB", Toast.LENGTH_SHORT).show();
                else if (checked) {
                    writeTaskS7 = new S7WriteTask();
                    writeTaskS7.Start(this._ip, this._rack, this._slot);
                    int value = Integer.parseInt(this.writeValue.getText().toString());
                    if (rb1.isChecked()) writeTaskS7.WriteByte(5, value);
                    else if (rb2.isChecked()) writeTaskS7.WriteByte(6, value);
                    else if (rb3.isChecked()) writeTaskS7.WriteByte(7, value);
                    else if (rb4.isChecked()) writeTaskS7.WriteByte(8, value);
                    else if (rb5.isChecked()) writeTaskS7.WriteInt(18, value);
                    writeTaskS7.Stop();
                }
            default:
        }
    }
}