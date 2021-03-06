package com.example.luizhenrique.numerosloteria.Presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.luizhenrique.numerosloteria.Model.Jogo;
import com.example.luizhenrique.numerosloteria.Model.Resultado;
import com.example.luizhenrique.numerosloteria.Services.GeradorDeNumeros;
import com.example.luizhenrique.numerosloteria.Services.JogoManager;
import com.example.luizhenrique.numerosloteria.Services.RealmServices;
import com.example.luizhenrique.numerosloteria.Services.ResultadoService;
import com.example.luizhenrique.numerosloteria.View.DetalhesJogoView;

import java.util.ArrayList;
import java.util.List;

public class DetalhesJogoPresenterImpl implements DetalhesJogoPresenter {

    private JogoManager  jogoManager;
    private DetalhesJogoView detalhesJogoView;
    private Context ctx;

    public DetalhesJogoPresenterImpl(DetalhesJogoView detalhesJogoView, Context context){

        jogoManager = new JogoManager();
        this.detalhesJogoView = detalhesJogoView;
        this.ctx = context;
    }

// Verifica quais numeros jogados foram acertados

    public ArrayList<Integer> verificarNumerosAcertos(Resultado resultado, Jogo jogo){

        ArrayList<Integer> numerosAcertos = new ArrayList<>();

        int[] numerosJogados = GeradorDeNumeros.ParseToInt(jogo);

        if (resultado.getNumero() == null){

            return numerosAcertos;
        }else{

            List<Object> numerosSorteados = resultado.getSorteio();

            for (int nums : numerosJogados) {
                for (Object numerosSorteado : numerosSorteados) {
                    if (numerosSorteado.equals(nums)) {
                        numerosAcertos.add(nums);
                    }
                }
            }
            return numerosAcertos;
        }
    }

    public ArrayList<ArrayList<Integer>> verificarNumerosAcertosDuplaSena(Resultado resultado,Jogo jogo) {

        ArrayList<ArrayList<Integer>> numerosAcertos = new ArrayList<>();
        numerosAcertos.add(new ArrayList<Integer>());
        numerosAcertos.add(new ArrayList<Integer>());

        int[] numerosJogados = GeradorDeNumeros.ParseToInt(jogo);

        List<Integer> numsJogo1 = (ArrayList<Integer>) resultado.getSorteio().get(0);
        List<Integer> numsJogo2 = (ArrayList<Integer>) resultado.getSorteio().get(1);


        for (int nums : numerosJogados) {
            for (Object numerosSorteado : numsJogo1) {
                if (numerosSorteado.equals(nums)) {
                    numerosAcertos.get(0).add(nums);
                }
            }
        }

        for (int nums : numerosJogados) {
            for (Object numerosSorteado : numsJogo2) {
                if (numerosSorteado.equals(nums)) {
                    numerosAcertos.get(1).add(nums);
                }
            }
        }

        return numerosAcertos;
    }

    // verifica quanto a apsota rendeu em reais

    public Object verificarPremiacao(Resultado res, Jogo jogo){

        Object valorPremio = 0;
        int[] premiacaoes;
        int count = 0;

        int acertos = verificarNumerosAcertos(res,jogo).size();

        premiacaoes = jogoManager.getAcertos(jogo.tipoJogo.toLowerCase());



        //count = premiacaoes.length;

        for (int num: premiacaoes){

            if (num == acertos){

                valorPremio = res.getRateio().get(count);

            }else{
                count++;
            }
        }

        return valorPremio;

    }

    public Resultado carregarResultadoOff(String filename){

        Resultado resultado = new ResultadoService().carregarResultadoOfflinebyFilename(filename);

        return  resultado;
    }

    public ArrayList<Float> verificarPremiacaoDuplaSena(Resultado res, Jogo jogo){

        ArrayList<Float> valorPremio = new ArrayList<>();
        int[] premiacaoes;
        int count = 0;

        int acertos1 = verificarNumerosAcertosDuplaSena(res,jogo).get(0).size();
        int acertos2 = verificarNumerosAcertosDuplaSena(res,jogo).get(1).size();

        ArrayList<Float> rateioJogo1 =  (ArrayList<Float>) res.getRateio().get(0);
        ArrayList<Float> rateioJogo2 =  (ArrayList<Float>) res.getRateio().get(1);


        premiacaoes = jogoManager.getAcertos(jogo.tipoJogo.toLowerCase());

        for (int num: premiacaoes){

            if (num == acertos1){

                valorPremio.add(rateioJogo1.get(count));

            }else{
                count++;
            }
        }

        for (int num: premiacaoes){

            if (num == acertos2){

                valorPremio.add(rateioJogo2.get(count));
            }else{
                count++;
            }
        }

        return  valorPremio;
    }

    @Override
    public Object verificarPremiacaoTime(Resultado resultado, Jogo jogo) {

        Object premioTimeCoracao = 0;

        if (resultado.getTime().equals(jogo.timeDoCoracao)){

            premioTimeCoracao = resultado.getRateio().get(5);

        }

        return premioTimeCoracao;
    }

    public Object verificarPremiacaoMes(Resultado resultado, Jogo jogo){

        Object premiacaoMes = 0;

        if (resultado.getMes().equals(jogo.mesDeSorte)){
            premiacaoMes = resultado.getRateio().get(4);
        }

        return premiacaoMes;
    }

    public Jogo carregarRealmJogo(int id,Context context){

        return new RealmServices(context).getJogo(id);
    }

    @Override
    public void getResultadoAnterior(String tipoJogo, int sorteio) {

        String txt = "";

        Resultado resAnt = new ResultadoService().carregarResultadoOffline(tipoJogo.toLowerCase());

        if (resAnt.getNumero() != null && resAnt.getNumero() < sorteio && resAnt.getNumero() != sorteio-1){

            txt = "Sorteio não ocorreu ainda!";
        }
        else if (resAnt.getNumero() != null && resAnt.getNumero() == sorteio-1){

            txt = "Sorteio ocorrerá dia "+ ResultadoService.formatarData(resAnt.getProximoData());
        }
        else if (resAnt.getNumero() != null && resAnt.getNumero() < sorteio || resAnt == null && verificarConexao() == false){

            txt = "Conecte-se a internet para verificar resultados";
        }
        else {
            txt = "Conecte-se a internet para verificar resultados";
        }

        detalhesJogoView.setTextView(txt);
    }

    public boolean verificarConexao() {

        ConnectivityManager cm;
        NetworkInfo info;
        cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = cm.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }
}