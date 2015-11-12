package com.example.neeraja.emojisclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Neeraja on 11/11/2015.
 */
public class ResultsAdapter extends ArrayAdapter<EmojisResults> {
    public ResultsAdapter(Context context, ArrayList<EmojisResults> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    private static class ViewHolder {
        ImageView ivEmotionImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmojisResults imageInfo = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_emotion_results, parent, false);
            viewHolder.ivEmotionImage = (ImageView) convertView.findViewById(R.id.ivEmotionImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivEmotionImage.setImageResource(0);
        Picasso.with(getContext()).load(imageInfo.image).resize(50, 50).placeholder(R.mipmap.ic_launcher).into(viewHolder.ivEmotionImage);
        return convertView;
    }
}