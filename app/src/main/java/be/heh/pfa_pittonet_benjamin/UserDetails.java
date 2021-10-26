package be.heh.pfa_pittonet_benjamin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import be.heh.pfa_pittonet_benjamin.database.DatabaseHelper;
import be.heh.pfa_pittonet_benjamin.database.User;

public class UserDetails extends AppCompatActivity {

    DatabaseHelper db;
    private EditText textName, textSurname, textMail, textPassword, textPasswordConfirm;
    private Button _btnSave, _btnDelete, _btnMakeAdmin, _btnMakeNormal;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private String _userMail;
    private User _user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        db = new DatabaseHelper(this);

        Bundle userDetail = this.getIntent().getExtras();
        _userMail = userDetail.getString("mail");
        Toast.makeText(UserDetails.this, "mail is: " + _userMail , Toast.LENGTH_SHORT).show();


        Cursor cursor = db.getOneUser(this._userMail);
        if (cursor.moveToFirst()) {
            _user.setId(cursor.getInt(0));
            _user.setName(cursor.getString(1));
            _user.setSurname(cursor.getString(2));
            _user.setMail(cursor.getString(3));
            _user.setPassword(cursor.getString(4));
            _user.setIsAdmin(cursor.getInt(5));

        }

        textName = (EditText) findViewById(R.id.editName);
        textSurname = (EditText) findViewById(R.id.editSurname);
        textMail = (EditText) findViewById(R.id.editMail);
        textPassword = (EditText) findViewById(R.id.editPassword);
        textPasswordConfirm = (EditText) findViewById(R.id.editPasswordConfirm);
        _btnMakeAdmin = findViewById(R.id.btnMakeAdmin);
        _btnMakeNormal = findViewById(R.id.btnMakeNormal);
        _btnSave = findViewById(R.id.btnSave);
        _btnDelete = findViewById(R.id.btnDelete);


        textName.setText(_user.getName());
        textSurname.setText(this._user.getSurname());
        textMail.setText(this._user.getMail());
        textMail.setEnabled(false);
        textPassword.setText(this._user.getPassword());
        textPasswordConfirm.setText(this._user.getPassword());

        if (_user.getIsAdmin() == 1){
            _btnMakeAdmin.setEnabled(false);
        }else if (_user.getIsAdmin() == 0){
            _btnMakeNormal.setEnabled(false);
        }

        _btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_user.getIsAdmin() == 0) {
                    if (db.getPrevious().getCount() == 0) {
                        new AlertDialog.Builder(UserDetails.this)
                                .setTitle("Alerte!")
                                .setMessage("Impossible de supprimer le dérnier utilisateur.")
                                .setNeutralButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        new AlertDialog.Builder(UserDetails.this)
                                .setTitle("Suppresion utilisateur")
                                .setMessage("Etes vous sur de vouloir supprimer l'utilisateur " + _user.getSurname() + " " + _user.getName())
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteUser(_user);
                                        Intent list = new Intent(UserDetails.this, MenuActivity.class);
                                        startActivity(list);
                                        finish();
                                    }
                                })
                                .setNegativeButton("Non", null)
                                .setIcon(android.R.drawable.ic_delete)
                                .show();
                    }
                } else if (_user.getIsAdmin() == 1){
                    new AlertDialog.Builder(UserDetails.this)
                            .setTitle("Alerte!")
                            .setMessage("Suppresion admin impossible.")
                            .setNeutralButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        _btnSave.setOnClickListener(new View.OnClickListener() {
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
                } else if (textPassword.toString().length() < 4) {
                    Toast.makeText(getApplicationContext(), "Mot de passe trop cours", Toast.LENGTH_SHORT).show();
                } else if (cnf_pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez confirmer votre mot de passe", Toast.LENGTH_SHORT).show();
                } else if (pass.equals(cnf_pass)) {
                    Toast.makeText(UserDetails.this, "Modification Utilisateur réussie !", Toast.LENGTH_SHORT).show();
                    _user.setName(name);
                    _user.setSurname(surname);
                    _user.setPassword(pass);
                    db.updateUser(_user);

                    Intent list = new Intent(UserDetails.this, MenuActivity.class);
                    startActivity(list);
                    finish();

                } else {
                    Toast.makeText(UserDetails.this, "Le mot de passe est incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        _btnMakeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _user.setIsAdmin(1);

                Runnable updatePerm = new Runnable(_user.getIsAdmin());
                new Thread(updatePerm).start();

                Toast.makeText(UserDetails.this, "Ajout de permission admin réussie.", Toast.LENGTH_SHORT).show();
                Intent list = new Intent(UserDetails.this, MainActivity.class);
                startActivity(list);
                finish();
            }
        });
        _btnMakeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _user.setIsAdmin(0);

                Runnable updatePerm = new Runnable(_user.getIsAdmin());
                new Thread(updatePerm).start();

                Toast.makeText(UserDetails.this, "Retrait de permission admin réussie.", Toast.LENGTH_SHORT).show();
                Intent list = new Intent(UserDetails.this, MainActivity.class);
                startActivity(list);
                finish();
            }
        });
    }


    class Runnable implements java.lang.Runnable{
        int isAdmin;
        Runnable(int isAdmin){
            this.isAdmin = isAdmin;
        }

        @Override
        public void run() {
            db.updateUserPerms(_user);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}