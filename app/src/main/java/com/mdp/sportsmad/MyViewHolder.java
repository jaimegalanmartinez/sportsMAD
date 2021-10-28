package com.mdp.sportsmad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    Context context;
    TextView title;
    TextView street;
    TextView type;
    ImageView imageType;

    private static final String TAG = "ListOfItems, MyViewHolder";

    public MyViewHolder(Context ctxt, View itemView) {
        super(itemView);
        context = ctxt;
        title = itemView.findViewById(R.id.title);
        street = itemView.findViewById(R.id.street);
        type = itemView.findViewById(R.id.type);
        imageType = itemView.findViewById(R.id.imageView);
    }

    void bindValues(com.mdp.sportsmad.model.SportCenter item) {
        // give values to the elements contained in the item view
        //Default values

        title.setText(item.getTitle());//"CDM"+item.getTitle().substring(26)
        street.setText(item.getStreet());
        type.setText(item.getType());

        //Assign an image and type text description to each item depending on their type
        String typeSplit[]= item.getType().split("/");
        switch (typeSplit[typeSplit.length-1]){
            case "Piscinas":
                imageType.setImageResource(R.drawable.piscina_icon);
                type.setText("Swimming pool");
                break;
            case "Gimnasios":
                imageType.setImageResource(R.drawable.sport_icon_png_10);
                type.setText("Gym");
                break;
            case "Rocodromo":
                imageType.setImageResource(R.drawable.png_clipart_rock_climbing_computer_icons_climbing_harnesses_rock_text_hand);
                type.setText("Climbing wall");
                break;
            case "CamposEstadiosFutbol":
                imageType.setImageResource(R.drawable._57_3576682_png_file_jugador_de_futbol_icono);
                type.setText("Football stadium");
                break;
            case "Embarcaderos":
                imageType.setImageResource(R.drawable.__pier);
                type.setText("Pier");
                break;
            case "PistasTenisBadminton":
                imageType.setImageResource(R.drawable.tennisplayerplayingtennis_89117);
                type.setText("Badminton & tennis court");
                break;
            case "CanchasBaloncesto":
                imageType.setImageResource(R.drawable.baloncesto);
                type.setText("Basketball court");
                break;
            default:
                imageType.setImageResource(R.drawable.ic_launcher_foreground);
        }

    }

    @SuppressLint("LongLogTag")
    @Nullable
    public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {

        Log.d(TAG, "getItemDetails() called");

        ItemDetailsLookup.ItemDetails<Long> itemdet = new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                Log.d(TAG, "ItemDetailsLookup.ItemDetails<Long>.getPosition() called");
                return getAdapterPosition();
            }

            @Nullable
            @Override
            public Long getSelectionKey() {
                Log.d(TAG, "ItemDetailsLookup.ItemDetails<Long>.getSelectionKey() called");
                return (long) (getAdapterPosition());
            }
        };

        return itemdet;
    }
}