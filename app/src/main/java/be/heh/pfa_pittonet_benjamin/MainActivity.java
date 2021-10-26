package be.heh.pfa_pittonet_benjamin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import be.heh.pfa_pittonet_benjamin.database.DatabaseHelper;
import be.heh.pfa_pittonet_benjamin.database.User;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText _etLogin, _etPassword;
    private Button _btnLogin;
    private Button _btnSignUp;
    private DatabaseHelper _db = new DatabaseHelper(this);
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.user = new User();
        this._etLogin = findViewById(R.id.et_login);
        this._etPassword = findViewById(R.id.et_password);
        this._btnLogin = findViewById(R.id.btn_login);
        this._btnLogin.setOnClickListener(this);
        this._btnSignUp = findViewById(R.id.btn_signup);
        this._btnSignUp.setOnClickListener(this);

        //If no user in DB, first time launching app
        if (this._db.getAllUser().getCount() == 0) {
            FirstStart();
        }
    }

    //Force user to create admin user
    public void FirstStart() {
        Intent adminSignUp = new Intent(MainActivity.this, AdminSignUp.class);
        adminSignUp.putExtra("isFirstStart", true);
        startActivity(adminSignUp);
        finish();
    }

    //If button is clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //In case of login attemp
            case R.id.btn_login:
                if (this._etLogin.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter a mail address", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Cursor cursor = _db.getOneUser(this._etLogin.getText().toString().toLowerCase());
                        if (cursor.moveToFirst()) {
                            this.user.setName(cursor.getString(1));
                            this.user.setSurname(cursor.getString(2));
                            this.user.setMail(cursor.getString(3));
                            this.user.setPassword(cursor.getString(4));
                            this.user.setIsAdmin(cursor.getInt(5));
                        }
                        if (this.user.getMail().equals(this._etLogin.getText().toString().toLowerCase())) {
                            if (this.user.getPassword().equals(this._etPassword.getText().toString())) {
                                Intent menu = new Intent(this, MenuActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("user", this.user);
                                menu.putExtras(bundle);
                                startActivity(menu);
                                finish();
                            } else {
                                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //In case sign up is clicked
            case R.id.btn_signup:
                Intent superAdminIntent = new Intent(MainActivity.this, UserSignUp.class);
                startActivity(superAdminIntent);
                finish();
        }
    }
}