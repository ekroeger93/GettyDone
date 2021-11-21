package com.example.checkListApp.timemanagement;

public class TimeParcelBuilder implements TimeParcelImpl {


    private final TimeParcel transactionParcelable = new TimeParcel();

    public TimeParcelBuilder setTimeStringValue(String time){
        transactionParcelable.timeStringVal = time;
        return this;
     }

   public TimeParcelBuilder setTimeNumberValue(int time){
         transactionParcelable.timeNumberValue = time;
         return this;
     }

    public TimeParcelBuilder setTimeIndexValue(int index){
         transactionParcelable.timeIndex = index;
         return this;
     }

     public TimeParcel build(){
         return transactionParcelable;
     }


}
