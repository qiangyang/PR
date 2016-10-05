package com.kaust.cs.Tools.TFIDF;

/**
 * Created by yangq0a on 16/9/5.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.kaust.cs.PaperPOJO.Paper;

public class ContentsParsing {

    private static ArrayList<String> stopWords = new ArrayList<String>();
    private static HashMap<String, HashMap<String, Float>> allTheTf = new HashMap<String, HashMap<String, Float>>();
    private static HashMap<String, HashMap<String, Integer>> allTheNormalTF = new HashMap<String, HashMap<String, Integer>>();
    private static String stopFileName = "./Data/stopwords.txt";

    //根据参数选择使用title=0, keywords = 1, abstract = 2进行tfidf分析
    public static String[] cutWord(Paper p) throws IOException {
        String[] cutWordResult = null;
        ArrayList<String> cutWordResults = new ArrayList<>();
        String text = p.getPaperTitle();
        cutWordResult = text.split(" |:|\\.");
        getStopWrods(stopFileName);
        int i = 0;
        for(String word: cutWordResult){
            if(stopWords.contains(word) || stopWords.contains(word.toLowerCase())){
                continue;
            }else {
                cutWordResults.add(word.toLowerCase());
            }
        }
        return cutWordResults.toArray(new String[]{});
    }

    public static ArrayList<String> getStopWrods(String fileName){
        try
        {
            BufferedReader in=new BufferedReader(new FileReader(fileName));
            String line=in.readLine();
            while (line!=null)
            {
                stopWords.add(line);
                line=in.readLine();
            }
            in.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return stopWords;
    }

    public static HashMap<String, Float> tf(String[] cutWordResult) {
        HashMap<String, Float> tf = new HashMap<String, Float>();//正规化
        int wordNum = cutWordResult.length;
        int wordtf = 0;
        for (int i = 0; i < wordNum; i++) {
            wordtf = 0;
            for (int j = 0; j < wordNum; j++) {
                if (cutWordResult[i] != " " && i != j) {
                    if (cutWordResult[i].equals(cutWordResult[j])) {
                        cutWordResult[j] = " ";
                        wordtf++;
                    }
                }
            }
            if (cutWordResult[i] != " ") {
                tf.put(cutWordResult[i], (new Float(++wordtf)) / wordNum);
                cutWordResult[i] = " ";
            }
        }
        return tf;
    }

    public static HashMap<String, Integer> normalTF(String[] cutWordResult) {
        HashMap<String, Integer> tfNormal = new HashMap<String, Integer>();//没有正规化
        int wordNum = cutWordResult.length;
        int wordtf = 0;
        for (int i = 0; i < wordNum; i++) {
            wordtf = 0;
            if (cutWordResult[i] != " ") {
                for (int j = 0; j < wordNum; j++) {
                    if (i != j) {
                        if (cutWordResult[i].equals(cutWordResult[j])) {
                            cutWordResult[j] = " ";
                            wordtf++;

                        }
                    }
                }
                tfNormal.put(cutWordResult[i], ++wordtf);
                cutWordResult[i] = " ";
            }
        }
        return tfNormal;
    }

    public static Map<String, HashMap<String, Float>> tfOfAll(Vector<Paper> vec) throws IOException {
        for (Paper p : vec) {
            HashMap<String, Float> dict = new HashMap<String, Float>();
            dict = ContentsParsing.tf(ContentsParsing.cutWord(p));
            allTheTf.put(p.getTitleByID(p.getPaperID()), dict);
//            if(p.getPaperID().equals("53e99f94b7602d970286c0a9")){
//                System.out.print("ssss"+p.getPaperTitle());
//            }
        }
        return allTheTf;
    }

    public static Map<String, HashMap<String, Integer>> NormalTFOfAll(Vector<Paper> vec) throws IOException {
        for (Paper p : vec) {
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            dict = ContentsParsing.normalTF(ContentsParsing.cutWord(p));
            allTheNormalTF.put(p.getPaperID(), dict);
        }
        return allTheNormalTF;
    }

    public static Map<String, Float> idf(Vector<Paper> vec) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //公式IDF＝log((1+|D|)/|Dt|)，其中|D|表示文档总数，|Dt|表示包含关键词t的文档数量。
        Map<String, Float> idf = new HashMap<String, Float>();
        List<String> located = new ArrayList<String>();
        NormalTFOfAll(vec);
        float Dt = 1;
        float D = allTheNormalTF.size();//文档总数
        System.out.println(D+"=========");
        Map<String, HashMap<String, Integer>> tfInIdf = allTheNormalTF;//存储各个文档tf的Map

        for(String id : tfInIdf.keySet()){
            HashMap<String, Integer> temp = tfInIdf.get(id);
            for(String word : temp.keySet()){
                Dt = 1;
                if (!(located.contains(word))){
                    for (String k: allTheNormalTF.keySet()) {
                        //System.out.println("K"+k+"  id"+id);
                        if (!k.equals(id)){
                            HashMap<String, Integer> temp2 = tfInIdf.get(k);
                            if(temp2 == null) continue;
                            //System.out.println(temp2.size());
                            if (temp2.keySet().contains(word)) {
                                located.add(word);
                                Dt = Dt + 1;
                                continue;
                            }
                        }
                    }
                    idf.put(word, Log.log((1 + D) / Dt, 10));
                }
            }
        }
        return idf;
    }
//    public static Map<String, Float> idf(Vector<Paper> vec) throws FileNotFoundException, UnsupportedEncodingException, IOException {
//        //公式IDF＝log((1+|D|)/|Dt|)，其中|D|表示文档总数，|Dt|表示包含关键词t的文档数量。
//        Map<String, Float> idf = new HashMap<String, Float>();
//        List<String> located = new ArrayList<String>();
//        NormalTFOfAll(vec);
//        float Dt = 1;
//        float D = allTheNormalTF.size();//文档总数
//        System.out.println(D+"=========");
//        List<String> key = fileList;//存储各个文档名的List
//        Map<String, HashMap<String, Integer>> tfInIdf = allTheNormalTF;//存储各个文档tf的Map
//
//        for (int i = 0; i < D; i++) {
//            HashMap<String, Integer> temp = tfInIdf.get(key.get(i));
//            for (String word : temp.keySet()) {
//                Dt = 1;
//                if (!(located.contains(word))) {
//                    for (int k = 0; k < D; k++) {
//                        if (k != i) {
//                            HashMap<String, Integer> temp2 = tfInIdf.get(key.get(k));
//                            if (temp2.keySet().contains(word)) {
//                                located.add(word);
//                                Dt = Dt + 1;
//                                continue;
//                            }
//                        }
//                    }
//                    idf.put(word, Log.log((1 + D) / Dt, 10));
//                }
//            }
//        }
//        return idf;
//    }
    public static Map<String, HashMap<String, Float>> tfidf(Vector<Paper> vec) throws IOException {
        //System.out.println(vec.size()+"=========");
        Map<String, Float> idf = ContentsParsing.idf(vec);
        //System.out.println(idf.size()+"idf=========");
        Map<String, HashMap<String, Float>> tf = ContentsParsing.tfOfAll(vec);
//        Map<String, HashMap<String, Float>> tfidf = new HashMap<String, HashMap<String, Float>>();
        for (String pID : tf.keySet()) {
            Map<String, Float> singelFile = tf.get(pID);
            for (String word : singelFile.keySet()) {
                singelFile.put(word, (idf.get(word)) * singelFile.get(word));
            }
        }
        return tf;
    }
}
