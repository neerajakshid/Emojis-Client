package com.example.neeraja.emojisclient;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.List;

/**
 * Created by Neeraja on 11/11/2015.
 */
@Table(name = "EmojisResults")
public class EmojisResults extends Model{
    @Column(name = "text")
    public String text;
    @Column(name = "image")
    public String image;

    public EmojisResults (){} // default constructor



    public EmojisResults(String text, String image){
        this.text = text;
        this.image = image;
    }

    public static List<EmojisResults> loadEmojis(){
        List<EmojisResults> list = new Select().from(EmojisResults.class).orderBy("text ASC").execute();
        return list;
    }

    public static List<EmojisResults> getItemSearchQuery(String query){
           List<EmojisResults> items = SQLiteUtils.rawQuery(EmojisResults.class, "SELECT * from EmojisResults where text like ?", new String[]{query});
           return items;
       }
    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }
}
