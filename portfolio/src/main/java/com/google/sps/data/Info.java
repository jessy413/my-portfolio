package com.google.sps.data;


public final class Info {

    private final long id;
    private final String title;
    private final long timestamp;
  
    public Info(long id, String title, long timestamp) {
      this.id = id;
      this.title = title;
      this.timestamp = timestamp;
    }
  }