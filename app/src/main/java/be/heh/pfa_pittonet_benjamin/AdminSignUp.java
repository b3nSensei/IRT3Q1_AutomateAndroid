package be.heh.pfa_pittonet_benjamin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.heh.pfa_pittonet_benjamin.database.DatabaseHelper;

public class AdminSignUp extends AppCompatActivity {

    DatabaseHelper db;
    private EditText textName, textSurname, textMail, textPassword, textPasswordConfirm;
    private Button _btnSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription_admin);

        textName = (EditText)findViewById(R.id.editName);
        textSurname = (EditText)findViewById(R.id.editSurname);
        textMail = (EditText)findViewById(R.id.editMail);
        textPassword = (EditText)findViewById(R.id.editPassword);
        textPasswordConfirm = (EditText)findViewById(R.id.editPasswordConfirm);
        _btnSignUp = findViewById(R.id.btnSignUp);
        db = new DatabaseHelper(this);
        _btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String name = textName.getText().toString().trim().toLowerCase();
                String surname = textSurname.getText().toString().trim().toLowerCase();
                String mail = textMail.getText().toString().trim().toLowerCase();
                String pass = textPassword.getText().toString().trim();
                String cnf_pass = textPasswordConfirm.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
                } else if (surname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez entrer un prénom", Toast.LENGTH_SHORT).show();
                } else if (mail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez entrer un email", Toast.LENGTH_SHORT).show();
                } else if (!mail.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Veuillez entrer un email valide", Toast.LENGTH_SHORT).show();
                } else if (pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez entrer un mot de passe", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Mot de passe trop cours", Toast.LENGTH_SHORT).show();
                } else if (cnf_pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez confirmer votre mot de passe", Toast.LENGTH_SHORT).show();
                } else if (pass.equals(cnf_pass)) {
                    long val = db.addSuperUser(name, surname, mail, pass);
                    if (val > 0) {
                        Toast.makeText(AdminSignUp.this, "Inscription admin réussie !", Toast.LENGTH_SHORT).show();
                        Intent moveToLogin = new Intent(AdminSignUp.this, MainActivity.class);
                        startActivity(moveToLogin);
                    } else {
                        Toast.makeText(AdminSignUp.this, "Erreur : Le super Admin existe déjà !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminSignUp.this, "Le mot de passe est incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}