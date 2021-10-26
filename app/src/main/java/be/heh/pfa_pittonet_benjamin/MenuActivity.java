package be.heh.pfa_pittonet_benjamin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import be.heh.pfa_pittonet_benjamin.database.User;
import be.heh.pfa_pittonet_benjamin.process.LevelSettingActivity;
import be.heh.pfa_pittonet_benjamin.process.TabletSettingActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button _btn_tablet_packaging, _btn_level_control, _btn_users, _btn_logout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this._btn_tablet_packaging = findViewById(R.id.btn_tablet_packaging);
        this._btn_level_control = findViewById(R.id.btn_level_control);
        this._btn_users = findViewById(R.id.btn_users);
        this._btn_logout = findViewById(R.id.btn_log_out);

        this._btn_tablet_packaging.setOnClickListener(this);
        this._btn_level_control.setOnClickListener(this);
        this._btn_users.setOnClickListener(this);
        this._btn_logout.setOnClickListener(this);

        Bundle userDetail = this.getIntent().getExtras();
        this.user = (User) userDetail.getSerializable("user");

        if (this.user.getIsAdmin() == 0) {
            findViewById(R.id.view_management).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_management).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_management).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btn_tablet_packaging:
                Toast.makeText(MenuActivity.this, "Automate de comprimées" , Toast.LENGTH_SHORT).show();
                Intent tablet = new Intent(this, TabletSettingActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("user", this.user);
                tablet.putExtras(bundle);
                startActivity(tablet);
                break;

            case R.id.btn_level_control:
                Toast.makeText(MenuActivity.this, "Automate de niveau de liquide" , Toast.LENGTH_SHORT).show();
                Intent level = new Intent(this, LevelSettingActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("user", this.user);
                level.putExtras(bundle);
                startActivity(level);
                break;

            case R.id.btn_users:
                if (this.user.getIsAdmin() == 0) {
                    Toast.makeText(MenuActivity.this, "Opérations non permise." , Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(MenuActivity.this, "Gestion des utilisateurs", Toast.LENGTH_SHORT).show();
                    Intent list = new Intent(this, UserList.class);
                    startActivity(list);
                }
                break;

            case R.id.btn_log_out:
                Toast.makeText(MenuActivity.this, "Log out réussi." , Toast.LENGTH_SHORT).show();
                Intent logout = new Intent(this, MainActivity.class);
                startActivity(logout);
                finish();
                break;
        }
    }
}
