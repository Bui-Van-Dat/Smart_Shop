package com.example.homemanagement.model;

import com.example.homemanagement.model.Channel;
import com.example.homemanagement.model.Feeds;

import java.util.ArrayList;
import java.util.List;

public class JsonReadThingspeak
{
    Channel channel;
    List<Feeds> feeds = new ArrayList<>();

    public JsonReadThingspeak() {
    }

    public JsonReadThingspeak(Channel channel, List<Feeds> feeds) {
        this.channel = channel;
        this.feeds = feeds;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<Feeds> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feeds> feeds) {
        this.feeds = feeds;
    }
}
