package com.example.checkListApp.ui.main.EntryManagement.ListComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;

public class ToggleSwitchOrdering {
    
    public List<tNumber> listToOrder = new ArrayList<>();

    public static class tNumber{
        public int number;
        public boolean toggle;

        public tNumber(int number, boolean toggle){
            this.number = number; this.toggle = toggle;
        }
    }

    public void toggleNum(int index){

        try {
            listToOrder.get(index).toggle = !listToOrder.get(index).toggle;
            sortNumbers(index);
            showNumberList();
        }catch (IndexOutOfBoundsException e){
        e.printStackTrace();

        }

    }

    public  int[] getAffectedRangeToggleOn(int index){


        int[] range = new int[listToOrder.size()];

        for(int i = 0 ; i < listToOrder.size(); i++){

            int testA = listToOrder.get(index).number;
            int testB = listToOrder.get(i).number;

            if(listToOrder.get(index).number < listToOrder.get(i).number) {
                range[i] = listToOrder.indexOf(listToOrder.get(i));
            }else{
                range[i] = -1;
            }

        }

        return range;

    }

    public  int[] getAffectedRangeToggleOff(int index){

        int[] range = new int[listToOrder.size()];

        for(int i = 0 ; i < listToOrder.size(); i++){

            if(listToOrder.get(index).number > listToOrder.get(i).number
                    && !listToOrder.get(i).toggle
            ) {
                range[i] = listToOrder.indexOf(listToOrder.get(i));
            }else{
                range[i] = -1;
            }

        }

        return range;

    }

    public  int getHighestValueFromIndexesToggleOn(){
        
        int number = 0;

        for(tNumber tNumber : listToOrder){

            if(tNumber.toggle && number < tNumber.number){
                number = tNumber.number;
            }

        }

        return  number;

    }

    public  int getLowestValueFromIndexesToggleOff(){

        int number = listToOrder.size();

        for(tNumber tNumber : listToOrder){

            if(!tNumber.toggle && number > tNumber.number){
                number = tNumber.number;
            }

        }

        return  number;
    }

    public  void decrementNumbers(int[] range){

        int index=0;


        for(tNumber tNumber : listToOrder){

            if(range[index] != -1){

                if(tNumber.toggle)
                    tNumber.number--;
            }
            index++;
        }

    }

    public void incrementNumbers(int[] range){

        int index=0;

        for(tNumber tNumber : listToOrder){

            if(range[index] != -1){

                if(!tNumber.toggle)
                    tNumber.number++;
            }
            index++;
        }

    }

    public  void sortNumbers(int index){

        //assign highest value
        if(listToOrder.get(index).number != getHighestValueFromIndexesToggleOn()) {

            if (!listToOrder.get(index).toggle) {
                int[] range = getAffectedRangeToggleOn(index);

                IntPredicate intPredicate = number -> number==-1;

               // boolean stream = Arrays.stream(range).allMatch(intPredicate);
                if(checkAllOff(range)) {

                    if(getHighestValueFromIndexesToggleOn() != 0
                            && listToOrder.get(index).number < getHighestValueFromIndexesToggleOn()
                    ){
                        listToOrder.get(index).number =
                                getHighestValueFromIndexesToggleOn();
                        decrementNumbers(range);
                    }
                }

            }}


        if(listToOrder.get(index).number != getLowestValueFromIndexesToggleOff())

            if (listToOrder.get(index).toggle) {
                //assign lowest value of off Toggle
                int[] range = getAffectedRangeToggleOff(index);

                IntPredicate intPredicate = number -> number==-1;

              //  boolean stream = Arrays.stream(range).allMatch(intPredicate);
                if(checkAllOff(range)){

                    listToOrder.get(index).number =
                            getLowestValueFromIndexesToggleOff();
                    incrementNumbers(range);

                }
            }




    }

    public  boolean checkAllOff(int[] range){

        int summation=0;

        for(int i : range){
            if (i == -1) {
                summation++;
            }
        }

        return !(summation == range.length);

    }

    public  void showNumberList(){

        System.out.println("------------------------------");

        for(tNumber tNumber : listToOrder){

            if(tNumber.toggle) {
                System.out.print("[" + tNumber.number + "]");
            }else{
                System.out.print("['"+tNumber.number+"']");
            }
        }

        System.out.println("");

    }



}
