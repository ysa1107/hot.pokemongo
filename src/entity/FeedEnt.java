/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author ysa
 */
public class FeedEnt {
    public int id;
    public String thumbnail;
    public String media_url;
    public String share_url;
    public String title;
    public int media_type; // 1: image, 2: gif, 3: mp4, 4: youtube
    public int liked;
    public int viewed;
    public int downloaded;
    public int commented;
    public String description;
    public long created = System.currentTimeMillis();
    public int state;
    public String source;
    public String ext_key;
    public String md5;
    public boolean readmore;
    public String duration;
    public int score;
    public String tags;
    public String alternate_url;
    public int category_id;
    public int width;
    public int height;
}
