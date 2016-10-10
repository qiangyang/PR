package com.kaust.cs.Tools.SIMFUNC;

import com.kaust.cs.PaperPOJO.Paper;
import com.kaust.cs.Tools.TFIDF.ContentsParsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
/**
 * Created by yangq0a on 16/10/5.
 */
public class SimFunction {
    private static String stopFileName = "./Data/stopwords.txt";
    public static ArrayList<String> cutWord(String s) throws IOException {
        String[] cutWordResult = null;
        ArrayList<String> cutWordResults = new ArrayList<>();
        cutWordResult = s.split(" |:|\\.|//?|//(|//)");
        ArrayList<String> stopWords = ContentsParsing.getStopWrods(stopFileName);
        for(String word: cutWordResult){
            if(stopWords.contains(word) || stopWords.contains(word.toLowerCase())){
                continue;
            }else {
                cutWordResults.add(word.toLowerCase());
            }
        }
        return cutWordResults;
    }


    public static double simByJaccard(String s1, String s2)throws IOException{
        ArrayList<String> str1 = cutWord(s1);
        ArrayList<String> str2 = cutWord(s2);
        Set<String> union = new HashSet<String>();
        union.addAll(str1);
        union.addAll(str2);
        int inter = 0;
        for (String key : union) {
            if (str1.contains(key) && str2.contains(key)) {
                inter++;
            }
        }
        return 1.0 * inter / union.size();
    }
}
