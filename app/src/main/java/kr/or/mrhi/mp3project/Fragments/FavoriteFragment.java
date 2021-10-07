package kr.or.mrhi.mp3project.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import kr.or.mrhi.mp3project.MusicData;
import kr.or.mrhi.mp3project.MyRecyclerAdapter;
import kr.or.mrhi.mp3project.R;

public class FavoriteFragment extends MyFragment {
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private MyRecyclerAdapter myRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, container, false);
        fragmentHelper.selectTbl(arrayList, 2);
        RecyclerView recyclerView = view.findViewById(R.id.favorite_recycler_view);
        myRecyclerAdapter = new MyRecyclerAdapter(arrayList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myRecyclerAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentHelper.selectTbl(arrayList, 2);
        myRecyclerAdapter.notifyDataSetChanged();
    }
}
