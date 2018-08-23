package com.example.luizhenrique.numerosloteria.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.luizhenrique.numerosloteria.R;
import com.example.luizhenrique.numerosloteria.Services.GeradorDeNumeros;

import java.util.ArrayList;

public class NumerosFavoritos extends AppCompatActivity {



    ArrayList<Integer> numerosSelecionados;
    FloatingActionButton fab_adicionar;
    Button btnGerarNumeros;
    TextView tvNumeros;
    int[] numeroDezenas;
    int[] numerosJogo;
    String numeros;
    String tipoJogo;
    int rangeJogo;
    int dezenas;
    Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeros_favoritos);

        it = getIntent();

        numerosSelecionados = new ArrayList<>();

        tipoJogo = it.getStringExtra("jogo");
        rangeJogo = it.getIntExtra("numeroBolas",0);
        dezenas = it.getIntExtra("dezenas",0);

        fab_adicionar = findViewById(R.id.fab_adc_numero);
        btnGerarNumeros = findViewById(R.id.btnGerarNumeros);
        tvNumeros = findViewById(R.id.numerosSelecionados);

        GridLayout gridLayoutMeusNumeros = findViewById(R.id.gridNumerosFavoritos);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(160,160);

        SharedPreferences sharedPreferences = getSharedPreferences("app",MODE_PRIVATE);

        if (sharedPreferences.contains("meus_numeros_favoritos")){
            numeros = sharedPreferences.getString("meus_numeros_favoritos","");
            int dezenas = sharedPreferences.getInt("dezenas", 0);
            numeroDezenas = GeradorDeNumeros.ParseToInt(numeros,dezenas);
        }
        else{
            numeroDezenas = new int[1];
        }

        fab_adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(NumerosFavoritos.this,SelecaoNumeros.class);
                startActivity(it);
            }
        });

        if (sharedPreferences.contains("meus_numeros_favoritos")) {

            for (int i = 0; i < numeroDezenas.length; i++) {

                if (numeroDezenas[i] < rangeJogo){
                    TextView t = new TextView(this);
                    t.setText(String.valueOf(numeroDezenas[i]));
                    t.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
                    t.setLayoutParams(lp);
                    t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    t.setTextSize(18);
                    t.setTextColor(Color.DKGRAY);
                    t.setBackgroundResource(R.drawable.bola);
                    t.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NumeroClicado(v);
                        }
                    });
                    gridLayoutMeusNumeros.addView(t);
                }
            }
        }

        btnGerarNumeros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;

                numerosJogo = new int[numerosSelecionados.size()];

                for (int n: numerosSelecionados){

                    numerosJogo[count] = n;
                    count++;
                }

                String numeros = GeradorDeNumeros.ParseToString(GeradorDeNumeros.gerarNumerosByFavoritos(numerosJogo,dezenas,rangeJogo));

                tvNumeros.setText(numeros);
            }
        });
    }

    public void NumeroClicado(View view){

        TextView texto = (TextView) view;
        int corDefault = ((TextView) view).getCurrentTextColor();
        int count = 0;

        if (count < dezenas) {

            if (corDefault != Color.BLACK){

                numerosSelecionados.add(Integer.valueOf(String.valueOf(texto.getText())));
                view.setBackgroundResource(R.drawable.bolaselecionada);
                ((TextView) view).setTextColor(Color.BLACK);
                ((TextView) view).setTypeface(null, Typeface.BOLD);
                count++;
                //toolbarSelecao.setTitle("Dezenas: "+String.valueOf(count));

            }else{
                count--;
                //toolbarSelecao.setTitle("Dezenas: "+String.valueOf(count));
                view.setBackgroundResource(R.drawable.bola);
                ((TextView) view).setTextColor(Color.DKGRAY);
                ((TextView) view).setTypeface(null,Typeface.NORMAL);
            }
        }

        if (count == dezenas && corDefault == Color.BLACK){

            count--;
            //toolbarSelecao.setTitle("Dezenas: "+String.valueOf(count));
            ((TextView) view).setTextColor(Color.DKGRAY);
            view.setBackgroundResource(R.drawable.bola);
            ((TextView) view).setTypeface(null,Typeface.NORMAL);
        }
    }
}
