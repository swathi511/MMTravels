package com.hjsoft.mmtravels.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SnapDistance {

    @SerializedName("snappedPoints")
    @Expose
    private List<SnappedPoint> snappedPoints = null;
    @SerializedName("warningMessage")
    @Expose
    private String warningMessage;

    public List<SnappedPoint> getSnappedPoints() {
        return snappedPoints;
    }

    public void setSnappedPoints(List<SnappedPoint> snappedPoints) {
        this.snappedPoints = snappedPoints;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

}
