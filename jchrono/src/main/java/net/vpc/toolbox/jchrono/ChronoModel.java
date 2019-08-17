/**
 * ==================================================================== jchrono
 * application
 *
 * Description: <start>A simple portable ui chronometer application<end>
 *
 * Copyright (C) 2012-2012 Taha BEN SALAH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.toolbox.jchrono;

import java.beans.PropertyChangeSupport;

/**
 * Application Model (of the MVC Design Pattern)
 *
 * @author Taha Ben Salah
 */
public class ChronoModel {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private Period period = new Period(0);
    private Period currentPeriod = new Period(0);
    private Period lastPeriod = new Period(0);
    private Period averagePeriod = new Period(0);
    private long currentOffset = 0;
    private long currentStart = 0;
    private long offset = 0;
    private long start = 0;
    private long last = 0;
    private long workCount = 0;
    private long workMax = 0;
    private boolean running = false;

    public ChronoModel() {
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        Period old = this.period;
        this.period = period;
        changeSupport.firePropertyChange("period", old, period);
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        long old = this.offset;
        this.offset = offset;
        changeSupport.firePropertyChange("offset", old, offset);
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        long old = this.start;
        this.start = start;
        changeSupport.firePropertyChange("start", old, start);
    }

    public long getLast() {
        return last;
    }

    public void setLast(long last) {
        long old = this.last;
        this.last = last;
        changeSupport.firePropertyChange("last", old, last);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        boolean old = this.running;
        this.running = running;
        changeSupport.firePropertyChange("running", old, running);
    }

    public void setAveragePeriod(Period averagePeriod) {
        this.averagePeriod = averagePeriod;
    }

    public Period getWorkAverageTime() {
        return averagePeriod;
    }

    public Period getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(Period currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public Period getLastPeriod() {
        return lastPeriod;
    }

    public void setLastPeriod(Period lastPeriod) {
        this.lastPeriod = lastPeriod;
    }

    public long getWorkCount() {
        return workCount;
    }

    public void setWorkCount(long workCount) {
        long old = this.workCount;
        this.workCount = workCount;
        changeSupport.firePropertyChange("workCount", old, workCount);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public long getWorkMax() {
        return workMax;
    }

    public void setWorkMax(long workMax) {
        long old = this.workMax;
        this.workMax = workMax;
        changeSupport.firePropertyChange("workMax", old, workMax);
    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(long currentOffset) {
        this.currentOffset = currentOffset;
    }

    public long getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(long currentStart) {
        this.currentStart = currentStart;
    }
    
    
}
