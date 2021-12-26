package com.example.checkListApp.timemanagement.parcel;

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

     public TimeParcelBuilder setTogglePrimer(boolean set){
        transactionParcelable.onTogglePrimer= set;
        return this;
     }

     public TimeParcelBuilder setRetainJsonData(String data){
        transactionParcelable.retainedJsonData  = data;
                return this;
     }

     public TimeParcel build(){
         return transactionParcelable;
     }


}
