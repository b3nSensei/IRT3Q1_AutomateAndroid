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

import org.w3c.dom.Text;

import be.heh.pfa_pittonet_benjamin.database.DatabaseHelper;

public class UserSignUp extends AppCompatActivity {

    DatabaseHelper db;
    private EditText textName, textSurname, textMail, textPassword, textPasswordConfirm;
    private TextView _tvPerm;
    private Button _btnSignUp, _btnDelete, _btnMakeAdmin, _btnMakeNormal;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        textName = (EditText) findViewById(R.id.editName);
        textSurname = (EditText) findViewById(R.id.editSurname);
        textMail = (EditText) findViewById(R.id.editMail);
        textPassword = (EditText) findViewById(R.id.editPassword);
        textPasswordConfirm = (EditText) findViewById(R.id.editPasswordConfirm);
        _btnSignUp = findViewById(R.id.btnSave);
        _btnDelete = findViewById(R.id.btnDelete);
        _btnMakeAdmin = findViewById(R.id.btnMakeAdmin);
        _btnMakeNormal = findViewById(R.id.btnMakeNormal);
        _btnMakeAdmin.setVisibility(View.GONE);
        _btnMakeNormal.setVisibility(View.GONE);
        _btnDelete.setVisibility(View.GONE);
        _tvPerm = findViewById(R.id.tv_perm);
        _tvPerm.setVisibility(View.GONE);


        db = new DatabaseHelper(this);
        _btnSignUp.setOnClickListener(new View.OnClickListener() {
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
                    long val = db.addUser(name, surname, mail, pass);
                    if (val > 0) {
                        Toast.makeText(UserSignUp.this, "Inscription Utilisateur réussie !", Toast.LENGTH_SHORT).show();
                        Intent moveToLogin = new Intent(UserSignUp.this, MainActivity.class);
                        startActivity(moveToLogin);
                        finish();
                    } else {
                        Toast.makeText(UserSignUp.this, "Erreur : L'utilisateur existe déjà !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserSignUp.this, "Le mot de passe est incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}