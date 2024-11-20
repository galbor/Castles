package com.example.castles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This fragment either creates a room for you and your friends to play with
 * or joins a room
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlineRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineRoomFragment extends Fragment {

    public OnlineRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @return A new instance of fragment OnlineRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineRoomFragment newInstance() {
        OnlineRoomFragment fragment = new OnlineRoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onlineroom, container, false);
    }
}