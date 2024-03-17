package ca.dmi.uqtr.applicationchat.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        // Retourne le fragment correspondant à la position
        switch (position) {
            case 0:
                return new SignInFrag();
            case 1:
                return new SignUpFrag();
            case 2:
                return new ResetFrag();
            default:
                // Fragment par défaut
                return new SignInFrag();
        }
    }

    @Override
    public int getItemCount() {
        // Nombre total de fragments
        return 3;
    }
}
