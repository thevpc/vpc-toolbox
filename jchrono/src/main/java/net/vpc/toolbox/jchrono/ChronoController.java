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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Application Controller (of the MVC Design Pattern)
 *
 * @author Taha Ben Salah
 */
public class ChronoController {

    private ChronoModel model = new ChronoModel();
    private List<ChronoListener> listeners = new ArrayList<ChronoListener>();
    private Timer timer;

    private TimerTask createTask() {
        return new TimerTask() {

            @Override
            public void run() {
                timeElapsed();
                Period p = model.getPeriod();
                for (ChronoListener timeListener : listeners) {
                    timeListener.timeChanged(p);
                }
            }
        };
    }

    public ChronoController() {
    }

    private void timeElapsed() {
        long end = System.currentTimeMillis();
        ChronoModel m = getModel();
        m.setLast(end);
        Period period = new Period(end - m.getStart() + m.getOffset());
        m.setPeriod(period);
        Period cperiod = new Period(end - m.getCurrentStart() + m.getCurrentOffset());
        m.setCurrentPeriod(cperiod);
    }

    public void start() {
        long now = System.currentTimeMillis();
        model.setStart(now);
        model.setCurrentStart(now);
        timer = new Timer("ChronoController", true);
        timer.scheduleAtFixedRate(createTask(), 0, 1000);
        model.setRunning(true);
        for (ChronoListener chronoListener : listeners) {
            chronoListener.started();
        }
    }

    public void stop() {
        timeElapsed();
        model.setOffset(System.currentTimeMillis() - model.getStart() + model.getOffset());
        model.setCurrentOffset(System.currentTimeMillis() - model.getCurrentStart() + model.getCurrentOffset());
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        model.setRunning(false);
        for (ChronoListener chronoListener : listeners) {
            chronoListener.stopped();
        }
    }

    public void workStep() {
        timeElapsed();
        model.setWorkCount(model.getWorkCount() + 1);
        model.setLastPeriod(model.getCurrentPeriod());
        long workCount = model.getWorkCount();
        if (workCount > 0) {
            model.setAveragePeriod(new Period(model.getPeriod().getTime() / workCount));
        }else{
            model.setAveragePeriod(new Period(0));
        }
        model.setCurrentOffset(0);
        model.setCurrentStart(System.currentTimeMillis());
    }

    public void reset() {
        model.setWorkCount(0);
        model.setOffset(0);
        model.setPeriod(new Period(0));
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        model.setRunning(false);
        for (ChronoListener chronoListener : listeners) {
            chronoListener.reset();
        }
    }

    public ChronoModel getModel() {
        return model;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getModel().getChangeSupport().addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        getModel().getChangeSupport().addPropertyChangeListener(prop, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getModel().getChangeSupport().addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        getModel().getChangeSupport().addPropertyChangeListener(prop, listener);
    }

    public void addChronoListener(ChronoListener listener) {
        listeners.add(listener);
    }

    public void removeTimeListener(ChronoListener listener) {
        listeners.add(listener);
    }
}
