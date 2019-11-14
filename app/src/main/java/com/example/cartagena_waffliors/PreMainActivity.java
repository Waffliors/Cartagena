package com.example.cartagena_waffliors;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PreMainActivity extends AppCompatActivity {
    EditText nomeUsuario;
    Button botaoIniciaJogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);

        nomeUsuario = findViewById(R.id.userNameText);
        botaoIniciaJogo = findViewById(R.id.buttonStartGame);
        botaoIniciaJogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }
}
