package uz.gita.a5.mydicrionary


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val screenHome = ScreenMain()
        val screenFavourite = ScreenFavourite()
        /*fragmentMain.setListener {
            supportFragmentManager.beginTransaction()
                .addToBackStack(fragmentMain::class.java.name)
                .replace(R.id.container, fragmentFav)
                .commit()
        }*/
        screenFavourite.setClickBackListener {
            supportFragmentManager.popBackStack()
        }
        setCurrentFragment(screenHome)

        findViewById<BottomNavigationView>(R.id.bar_menu).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(screenHome)
                R.id.favourites -> setCurrentFragment(screenFavourite)
                /*R.id.share -> {
                    val appMsg : String = "Hey !, Chack out this app for Share Button :-" +
                            "https://play.google.com/store/apps/details?id=uz.gita.a5.mydicrionary"
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, appMsg)
                    intent.type = "test/plain"
                    startActivity(intent)
                }*/
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }
}