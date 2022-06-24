package com.wimbli.WorldBorder.Events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBorderFillProgressEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private World world;
    private int reportTarget, reportTotal, reportNum;

    public WorldBorderFillProgressEvent(World world, int reportTarget, int reportTotal, int reportNum){
        this.world = world;
        this.reportTarget = reportTarget;
        this.reportTotal = reportTotal;
        this.reportNum = reportNum;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public World getWorld()
    {
        return world;
    }

    public int getReportTarget(){
        return reportTarget;
    }

    public int getReportTotal(){
        return reportTotal;
    }

    public int getReportNum(){
        return reportNum;
    }

    public double getPercent(){
        return ((double)(reportTotal + reportNum) / (double)reportTarget) * 100;
    }

}