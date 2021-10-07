package kr.or.mrhi.mp3project.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import kr.or.mrhi.mp3project.MusicData;
import kr.or.mrhi.mp3project.MyRecyclerAdapter;
import kr.or.mrhi.mp3project.R;

public class RandomFragment extends MyFragment {
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private MyRecyclerAdapter myRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.random_fragment, container, false);
        fragmentHelper.selectTbl(arrayList, 3);

        RecyclerView recyclerView = view.findViewById(R.id.random_recycler_view);
        myRecyclerAdapter = new MyRecyclerAdapter(arrayList, getContext());
        recyclerView.setAdapter(myRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        EditText edt_search = view.findViewById(R.id.edt_search);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = edt_search.getText().toString();
                fragmentHelper.searchTbl(arrayList, text);
                myRecyclerAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentHelper.selectTbl(arrayList, 3);
        myRecyclerAdapter.notifyDataSetChanged();
    }
}
