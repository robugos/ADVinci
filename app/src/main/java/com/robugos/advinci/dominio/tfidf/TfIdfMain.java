package com.robugos.advinci.dominio.tfidf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Mubin Shrestha
 */
public class TfIdfMain {
    private static List<String> lista;

    public static void calculaRecomendacao(String local, List<String> files) throws IOException {

        DocumentParser dp = new DocumentParser();
        dp.parseFiles(local, files);
        dp.tfIdfCalculator(); //calculates tfidf
        lista = dp.getCosineSimilarity(); //calculated cosine similarity
    }

    public List<String> getLista(){
        return lista;
    }

    public void orderList(List<String> list){
        HashMap<String, String> keys = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        List<String> order = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            String[] par = list.get(i).split(";");
            keys.put(par[1], par[0]);
            //values.put(Integer.toString(i), par[1]);
            order.add(par[1]);
        }
        Collections.sort(order, Collections.reverseOrder());
        lista.clear();
        for (int j=0; j<order.size(); j++){
            lista.add(keys.get(order.get(j))+";"+order.get(j));
            //System.out.println(keys.get(order.get(j))+";"+order.get(j));
        }

    }
}
