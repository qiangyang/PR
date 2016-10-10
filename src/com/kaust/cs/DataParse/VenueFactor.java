package com.kaust.cs.DataParse;

import com.kaust.cs.PaperPOJO.Paper;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by yangq0a on 16/8/31.
 */
public class VenueFactor {
    public static String file = "/Users/yangq0a/Documents/DBLPData/dblp.txt";
    Vector<Paper> v = new DataImport().dataImport(file);

    public HashMap<String, Integer> getVenues(){
        HashMap<String, Integer> venues = new HashMap<>();
        for(Paper p: v){
            String name = p.getVenue();
            if (venues.containsKey(name)){
                int times = venues.get(name);
                venues.put(name, times++);
            }else{
                venues.put(name, 1);
            }
        }
        return venues;
    }

    public HashMap<String, Double> getVenuesImpactFactor(){
        HashMap<String, Double> venueImpactFacor = new HashMap<>();
        HashMap<String, Integer> venues = getVenues();
        for(String venueName: venues.keySet()){
            int citationTimes = 0;
            int occuringTimes = venues.get(venueName);
            for(Paper p: v){
                if(p.getVenue()!=null) {
                    if (p.getVenue().equals(venueName)) {
                        String index = p.getPaperID();
                        for (Paper p1 : v) {
                            if (p1.getReferenceList().contains(index)) {
                                citationTimes++;
                            }
                        }
                    }
                }
            }
            //double impactFactor = citationTimes*venues.get(venueName)*1.0/(v.size()*venues.size());
            double impactFactor = citationTimes*1.0/occuringTimes;
            venueImpactFacor.put(venueName, impactFactor);
        }
        System.out.println(venueImpactFacor);
        return venueImpactFacor;
    }

    public static void main(String[] args){
        VenueFactor vf = new VenueFactor();
        vf.getVenuesImpactFactor();
    }
}
