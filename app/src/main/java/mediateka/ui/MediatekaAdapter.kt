package mediateka.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediatekaAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentFavorites.newInstance()
            1 -> FragmentPlaylists.newInstance()
            else -> throw IllegalStateException()
        }
    }
}