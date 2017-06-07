package com.robugos.advinci.dominio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Created by Robson on 06/06/2017.
 */

public class Recommander {
    public static int[][] criarMatriz()throws FileNotFoundException, IOException {
        // Inicia a matriz com todos os elementos com valor -1
        //int[][] matrix = new int[6040][3592];
        int[][] matrix = new int[6040][3592];
        for (int i = 0; i<matrix.length; ++i){
            for (int j = 0; j<matrix[0].length; ++j)
            {
                matrix[i][j] = -1;
                //System.out.println(i+"-"+j+":-1");
            }
        }

        // Preenche a matriz com os valores lidos do arquivo
        BufferedReader br = new BufferedReader(new FileReader("src\\domain\\ratings.csv"));
        StringTokenizer st = null;
        String row;
        while ((row = br.readLine()) != null){
            st = new StringTokenizer(row, ",");
            while(st.hasMoreTokens()){
                int user = Integer.parseInt(st.nextToken());
                int movie = Integer.parseInt(st.nextToken());
                int rating = Integer.parseInt(st.nextToken());
                matrix[user-1][movie-1] = rating;
                st.nextToken();
            }
        }
        br.close();
        return matrix;
    }

    public static int[][] paraAvaliar()throws FileNotFoundException, IOException{
        //Pega os itens nao avalidados pelos usuário no arquivo
        BufferedReader br = new BufferedReader(new FileReader("src\\domain\\toBeRated.csv"));
        StringTokenizer st = null;
        String row;
        int[][] data = new int[6][2];
        int i = 0;
        while ((row = br.readLine()) != null){
            st = new StringTokenizer(row, ",");
            while(st.hasMoreTokens()){
                data[i][0] = Integer.parseInt(st.nextToken());
                data[i][1] = Integer.parseInt(st.nextToken());
            }
            i += 1;
        }
        br.close();
        return data;
    }

    public static float[] calcularSimilaridade(int[][] matrix,int[][] avaliar)throws FileNotFoundException, IOException{
        int len = avaliar.length;
        int lenUsers = matrix.length;
        int lenItens = matrix[0].length;
        //System.out.println("len: "+len+" lenUser:"+lenUsers+" lenMovies:"+lenMovies);
        float[] avaliacao = new float[len];
        int user = 0;
        int item = 0;
        for (int i = 0; i < len; ++i){
            user = avaliar[i][0];
            item = avaliar[i][1];
            float maiorNum = 0;
            float maiorDenom = 0;
            for (int j = 0; j < lenItens; ++j){
                int itemAlvo = 0;
                int itemN = 0;
                int count = 0;
                //System.out.println("i:"+i+" j:"+j+"- user:"+user+" movie:"+movie);
                if(matrix[user-1][j] != -1){
                    //System.out.println("dif -1>> user-1:"+(user-1)+" j:"+j+" valor:"+matrix[user-1][j]);
                    for (int k = 0; k < lenUsers; ++k){
                        if ((matrix[k][item-1] != -1) && (matrix[k][j] != -1)){
                            itemAlvo += matrix[k][item-1];
                            itemN += matrix[k][j];
                            count++;
                        }
                    }
                    if (count>0){
                        float mediaAlvo = (float)itemAlvo/(float)count;
                        float mediaN = (float)itemN/(float)count;
                        float num = 0;
                        float denom1 = 0;
                        float denom2 = 0;
                        for (int k = 0; k < lenUsers; ++k){
                            if ((matrix[k][item-1] != -1) && (matrix[k][j] != -1)){
                                num += (matrix[k][item-1] - mediaAlvo)*(matrix[k][j] - mediaN);
                                denom1 += (matrix[k][item-1] - mediaAlvo)*(matrix[k][item-1] - mediaAlvo);
                                denom2 += (matrix[k][j] - mediaN)*(matrix[k][j] - mediaN);
                            }
                        }

                        if (num>0){
                            if (denom1*denom2>0){
                                maiorDenom += num/(Math.sqrt(denom1*denom2));
                                maiorNum += matrix[user-1][j]*num/(Math.sqrt(denom1)*Math.sqrt(denom2));
                            }
                        }
                    }
                }
            }
            float predicao = 0;
            if(maiorDenom == 0){
                predicao = maiorNum+1;
            }
            else{
                predicao = (maiorNum/maiorDenom)+1;
            }
            avaliacao[i] = predicao;
        }
        return avaliacao;
    }

    public static void calcular()throws IOException{
        //System.out.println("Recommendation System Ratings!!!");
        int[][] matrix = criarMatriz();
        int[][] avaliar = paraAvaliar();


        float[] avaliacao = new float[avaliar.length];

        //System.out.println("Item Pearson");
        avaliacao = calcularSimilaridade(matrix,avaliar);
        try{
            PrintWriter writer = new PrintWriter("result2.csv", "UTF-8");
            for (int i = 0; i < avaliacao.length; ++i){
                writer.println(avaliacao[i]);
            }
            writer.close();
            System.out.println("Predição realizada com sucesso.");
        }catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
