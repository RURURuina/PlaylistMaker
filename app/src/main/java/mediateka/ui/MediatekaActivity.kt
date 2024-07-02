package mediateka.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding

class MediatekaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatekaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mediatekaBack.setOnClickListener {
            finish()
        }

        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        viewPager.adapter = MediatekaAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.customView = createTabView(position)
        }.attach()

    }

    private fun createTabView(position: Int): View {
        val tabView = layoutInflater.inflate(R.layout.tab_style, null)
        val tabText = tabView.findViewById<TextView>(R.id.favorites_text)
        tabText.text = when (position) {
            0 -> getString(R.string.favorites)
            1 -> getString(R.string.playlists)
            else -> ""
        }
        return tabView

    }

}