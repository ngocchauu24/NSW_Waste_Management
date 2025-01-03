package app;

import java.util.ArrayList;

/**
 * Class represeting a LGA from the Studio Project database
 * In the template, this only uses the code and name for 2018 (so you will need to update this to handle all years)
 *
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

 public class LGA {
    // LGA Code
    private int code;
 
    // LGA Name
    private String name;
    private int population;
    private int households;
    private int collected;
    private int recycled;
    private double rate;
    private double average;
    private ArrayList<Double> percentages;

    
 
    /**
     * Create an LGA and set the fields
     */
    public LGA(int code, String name) {
       this.code = code;
       this.name = name;
    }
    public LGA(String name)
    {
      this.name = name;
    }

    public LGA(String name, int population, int households, int collected, int recycled, double rate, double average)
    {
      this.name = name;
      this.population = population;
      this.households = households;
      this.collected = collected;
      this.recycled = recycled;
      this.rate = rate;
      this.average = average;
    }

    public LGA(String name, ArrayList<Double> percentages, double difference)
    {
      this.name = name;
      this.percentages = percentages;
      this.rate = difference;
    }
 
    public int getCode() {
       return code;
    }
 
    public String getName() {
       return name;
    }

   public int getPopulation() {
      return population;
   }

   public int getHouseholds() {
      return households;
   }

   public int getCollected() {
      return collected;
   }

   public int getRecycled() {
      return recycled;
   }

   public double getRate() {
      return rate;
   }

   public double getAverage() {
      return average;
   }

   public ArrayList<Double> getPercentages()
   {
      return percentages;
   }
    

   

    
 }
 