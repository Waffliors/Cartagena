package com.example.cartagena_waffliors;

import android.view.View;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

//Classe que coleta e filtra o nome do usuário para utilizar na aplicação
public class PreMainActivity extends AppCompatActivity {
    private Button startGameButton;
    private EditText userName;

    //Na criação da activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pre_main);
        //Bind dos elementos da activity
        userName = findViewById(R.id.userNameText);
        startGameButton = findViewById(R.id.buttonStartGame);
        //Adiciona listener no botão
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = userName.getText().toString();
                //Se String for aprovada no filtro
                if (stringFilter(userNameString)) {
                    Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
                    intent.putExtra("userName", userNameString);
                    startActivity(intent);
                }
            }
        });
    }

    //Filtro de String
    private boolean stringFilter(String strng) {
        //Filtra espaço na String
        if (strng.contains(" ")) {
            Toast.makeText(this.getApplicationContext(),
                    "O nome não pode ter espaços"
                    , Toast.LENGTH_LONG).show();
            return false;
        }

        //Filtra pelo tamanho da String
        if (strng.length() > 8) {
            Toast.makeText(this.getApplicationContext(), "O nome deve ter menos de 8 caracteres"
                    , Toast.LENGTH_LONG).show();
            return false;
        }

        //Filtra os caracteres especiais
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(strng);
        boolean b = m.find();
        if (b) {
            Toast.makeText(this.getApplicationContext(),
                    "O nome só pode ser formado por letras e números"
                    , Toast.LENGTH_LONG).show();
            return false;
        }
        //Se passou nos dois filtros, é aprovado
        return true;
    }
}