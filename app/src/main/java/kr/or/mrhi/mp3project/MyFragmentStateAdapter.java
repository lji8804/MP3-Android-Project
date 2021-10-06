package kr.or.mrhi.mp3project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import kr.or.mrhi.mp3project.Fragments.AgeFragment;
import kr.or.mrhi.mp3project.Fragments.ArtistFragment;
import kr.or.mrhi.mp3project.Fragments.FavoriteFragment;
import kr.or.mrhi.mp3project.Fragments.RandomFragment;

public class MyFragmentStateAdapter extends FragmentStateAdapter {
    final static int LIST_INDEX_COUNT = 4;
    public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                ArtistFragment artistFragment = new ArtistFragment();
                return artistFragment;
            case 1:
                AgeFragment ageFragment = new AgeFragment();
                return ageFragment;
            case 2:
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                return favoriteFragment;
            case 3:
                RandomFragment randomFragment = new RandomFragment();
                return randomFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return LIST_INDEX_COUNT;
    }
}
