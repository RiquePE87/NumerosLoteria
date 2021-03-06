package com.example.luizhenrique.numerosloteria.Presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.luizhenrique.numerosloteria.Model.Resultado;
import com.example.luizhenrique.numerosloteria.Services.JogoManager;
import com.example.luizhenrique.numerosloteria.View.DetalhesSorteioView;

import java.util.ArrayList;

public class DetalhesSorteioPresenterImpl implements DetalhesSorteioPresenter {

    private DetalhesSorteioView detalhesSorteioView;
    int rows = 1;
    Context ctx;

    public DetalhesSorteioPresenterImpl(DetalhesSorteioView detalhesSorteioView, Context context){

        this.detalhesSorteioView = detalhesSorteioView;
        this.ctx = context;
    }

    public void preencherGanhadores(Resultado res) {

        detalhesSorteioView.removerLinhas();
        rows = 1;
        int[] acertos = new JogoManager().getAcertos(res.getTipo().toLowerCase());

        if (detalhesSorteioView != null) {

            if (res.getTipo().equals("dupla-sena")){

                ArrayList<Integer> premioJogo1 = (ArrayList<Integer>) res.getRateio().get(1);
                ArrayList<Integer> premioJogo2 = (ArrayList<Integer>) res.getRateio().get(0);
                ArrayList<Integer> ganhadoresJogo1 = (ArrayList<Integer>) res.getGanhadores().get(1);
                ArrayList<Integer> ganhadoresJogo2 = (ArrayList<Integer>) res.getGanhadores().get(0);

                gerarGanhadores(ganhadoresJogo1,premioJogo1,acertos,false);
                gerarGanhadores(ganhadoresJogo2,premioJogo2,acertos,true);
            }
            else
            {
                for (int i = 0; i < res.getGanhadores().size();i++){

                    if (res.getTipo().equals("timemania") && acertos[i] == 2) {

                        detalhesSorteioView.inserirLinha("Time do Coração ", String.valueOf(res.getGanhadores().get(i)), res.getRateio().get(i), rows,false);

                    }else if (res.getTipo().equals("dia-de-sorte") && acertos[i] == 3){

                        detalhesSorteioView.inserirLinha("Mês ", String.valueOf(res.getGanhadores().get(i)), res.getRateio().get(i), rows,false);
                    }
                    else{
                        detalhesSorteioView.inserirLinha(String.valueOf(acertos[i]), String.valueOf(res.getGanhadores().get(i)),res.getRateio().get(i),rows,false);
                        rows++;
                    }
                }
            }
        }
    }

    public void gerarGanhadores(ArrayList<Integer> ganhadores,ArrayList<Integer> premioJogo, int[] acertos, boolean flag){

        for (int i = 0; i < ganhadores.size(); i++){

            detalhesSorteioView.inserirLinha(String.valueOf(acertos[i]),String.valueOf(ganhadores.get(i)),premioJogo.get(i),rows,flag);
            rows++;
        }
    }

    public boolean verificarConexao() {

        ConnectivityManager cm;
        NetworkInfo info;
        cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = cm.getActiveNetworkInfo();

        return info != null && info.isConnected();

    }
}
