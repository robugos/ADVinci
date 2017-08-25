package com.robugos.advinci.dominio.tfidf;

import java.io.IOException;
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
}
