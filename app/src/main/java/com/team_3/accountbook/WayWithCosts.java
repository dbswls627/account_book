package com.team_3.accountbook;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;


public class WayWithCosts {
    @Embedded public Way way;
    @Relation(parentColumn = "wayId",
            entityColumn = "FK_wayId")
    public List<Cost> cost;



    public Way getWay() {
        return way;
    }

    public void setWay(Way way) {
        this.way = way;
    }

    public List<Cost> getCost() {
        return cost;
    }

    public void setCost(List<Cost> cost) {
        this.cost = cost;
    }
}
