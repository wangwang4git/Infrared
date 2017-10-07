package com.powergrid.victor.infraredpowergrid.infrared.json;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangwang on 2017/9/17.
 */

public class DeviceInterval implements Serializable {
    public String name;
    public List<Interval> intervals;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    @Override
    public String toString() {
        return "DeviceInterval{" +
                "name='" + name + '\'' +
                ", intervals=" + intervals +
                '}';
    }

    static public class Interval implements Serializable {
        public String name;
        public String[] descs;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getDescs() {
            return descs;
        }

        public void setDescs(String[] descs) {
            this.descs = descs;
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "name='" + name + '\'' +
                    ", descs=" + Arrays.toString(descs) +
                    '}';
        }
    }
}


