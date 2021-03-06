package com.hjsoft.mmtravels.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 28/10/16.
 */
public class Pojo {

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
