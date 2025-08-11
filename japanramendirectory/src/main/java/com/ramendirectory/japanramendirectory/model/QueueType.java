package com.ramendirectory.japanramendirectory.model;

public enum QueueType {
    QUEUE_UP("排隊等候"),    // Line up in person
    TICKET("登記等候"),      // Get a ticket
    RESERVATION("預約"),   // Make a reservation
    OTHER("其他");       // Other methods
    
    private final String chineseLabel;
    
    QueueType(String chineseLabel) {
        this.chineseLabel = chineseLabel;
    }
    
    public String getChineseLabel() {
        return chineseLabel;
    }
} 