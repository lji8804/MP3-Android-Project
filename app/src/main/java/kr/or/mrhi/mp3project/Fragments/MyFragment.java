package kr.or.mrhi.mp3project.Fragments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import kr.or.mrhi.mp3project.MySQLiteOpenHelper;

// 다른 fragment들이 상속받는 class 생성
public class MyFragment extends Fragment {
    public MySQLiteOpenHelper fragmentHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentHelper = new MySQLiteOpenHelper(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentHelper = null;
    }
}
