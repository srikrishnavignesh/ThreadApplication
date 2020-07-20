package com.example.threadapplication.events_display;

import java.util.ArrayList;

//a model class
public class EventSection {

    String title;
    ArrayList<Events> eventList;
    EventSection(String title)
    {
        this.title=title;
        this.eventList=new ArrayList<Events>();
    }

}
