package com.hjsoft.mmtravels.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 4/11/16.
 */
public class UpdatePojo {

    @SerializedName("Message")
    @Expose
    private String message;

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The Message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
