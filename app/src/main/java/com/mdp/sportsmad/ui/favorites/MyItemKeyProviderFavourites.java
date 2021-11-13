package com.mdp.sportsmad.ui.favorites;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

public class MyItemKeyProviderFavourites extends ItemKeyProvider<Long> {
    // We decide that out Keys will be of type Long
    // Options are:  Parcelable (and all subclasses like Uri), String, and Long
    // More info: https://developer.android.com/guide/topics/ui/layout/recyclerview-custom#select

    private static final String TAG = "ListOfItems, MyItemKeyProvider";

    /**
     * Creates a new provider with the given scope.
     *
     * @param scope Scope can't be changed at runtime.
     */
    @SuppressLint("LongLogTag")
    public MyItemKeyProviderFavourites(int scope) {
        super(scope);
        Log.d(TAG, "MyItemKeyProvider() called");
    }

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public Long getKey(int position) {
        Log.d(TAG, "getKey() called");
        return Long.valueOf(position);
    }

    @SuppressLint("LongLogTag")
    @Override
    public int getPosition(@NonNull Long key) {
        Log.d(TAG, "getPosition() called");
        return key.intValue();
    }

}
