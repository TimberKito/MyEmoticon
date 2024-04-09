package com.timber.soft.myemoticon

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.timber.soft.myemoticon.databinding.ActivityMainBinding
import com.timber.soft.myemoticon.model.RootDataModel
import com.timber.soft.myemoticon.tools.AppTools
import com.timber.soft.myemoticon.tools.AppTools.dpCovertPx

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var fragmentList: ArrayList<Fragment> = arrayListOf()
    private val rootModelList: MutableList<RootDataModel> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.root.setPadding(0, dpCovertPx(this), 0, 0)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE) or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        binding.layoutShop.setOnClickListener() {
            val url = getString(R.string.google_play_link) + packageName
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(url))
            startActivity(intent)
        }

        binding.layoutShare.setOnClickListener() {
            val url = getString(R.string.google_play_link) + packageName
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(intent)
        }

        binding.textVersion.text = getVersionName()

        binding.btMenu.setOnClickListener() {
            binding.drawerRoot.openDrawer(GravityCompat.START)
        }

        binding.drawerRoot.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                drawerView.isClickable = true
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

        rootModelList.addAll(
            AppTools.parseJsonFile(assets.open("data.json"))
        )
        rootModelList.shuffle()

        for (i in rootModelList) {
            binding.tabLayout.addTab(
                binding.tabLayout.newTab()
            )
        }

        for (i in 0 until binding.tabLayout.tabCount) {
            val rootDataModel = rootModelList[i]
            fragmentList.add(MainViewPagerFragment(rootDataModel))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                val textView = TextView(this@MainActivity)
                val selectedSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX, 24f, resources.displayMetrics
                )
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
                textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD) //加粗
                textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.main_color))
                textView.text = p0!!.text
                p0.customView = textView
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                p0?.customView = null
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        binding.viewpager.offscreenPageLimit = 3
        binding.viewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return fragmentList.size
            }

            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return rootModelList[position].categoryName
            }
        }
        binding.tabLayout.setupWithViewPager(binding.viewpager)

    }

    private fun getVersionName(): String {
        val pInfo: PackageInfo
        try {
            pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return ""
        }
        return "Version: " + pInfo.versionName
    }

}