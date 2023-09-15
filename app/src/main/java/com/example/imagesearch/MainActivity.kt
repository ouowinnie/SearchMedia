package com.example.imagesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.imagesearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            fragmentBtn1.setOnClickListener{
                setFragment(SearchFragment())
            }
            fragmentBtn2.setOnClickListener {
                setFragment(StorageFragment())
            }
        }
        setFragment(SearchFragment())
    }
    private fun setFragment(frag : Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainerView, frag)
            setReorderingAllowed(true)
            addToBackStack("")
        }
    }
}