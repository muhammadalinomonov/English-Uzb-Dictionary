package uz.gita.a5.mydicrionary

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.gita.a5.mydicrionary.data.AppDatabase
import uz.gita.a5.mydicrionary.data.local.Settings
import uz.gita.a5.mydicrionary.databinding.ScreenFavouriteBinding
import uz.gita.a5.mydicrionary.databinding.ScreenMainBinding
import uz.gita.a5.mydicrionary.utils.myApply
import java.util.*

class ScreenFavourite : Fragment(R.layout.screen_favourite), TextToSpeech.OnInitListener {

    private lateinit var clickBackListener: (() -> Unit)
    fun setClickBackListener(block: () -> Unit) {
        clickBackListener = block
    }

    val binding by viewBinding(ScreenFavouriteBinding::bind)
    private val sharedPref by lazy { Settings.getInstance(requireContext()) }
    private val database by lazy { AppDatabase.getInstance() }
    private val adapter by lazy { MainAdapter(database!!.getAllEnglishWords()) }
    private var tts: TextToSpeech? = null
    private lateinit var clickFavouriteListener: ((String) -> Unit)

    fun setListener(block: (String) -> Unit) {
        clickFavouriteListener = block
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tts = TextToSpeech(requireContext(), this)
        binding.myApply {
            myRv.adapter = adapter
            adapter.setLang(sharedPref.language)
            myRv.layoutManager = LinearLayoutManager(requireContext())

            val cursor = database!!.getAllFavourites()
            Log.d("TTT", "${cursor.count}")
            if (cursor.count == 0) {
                notFound.visibility = View.VISIBLE
            } else {
                notFound.visibility = View.GONE
            }
            adapter.setNotFountListener {
                if (it == 0) {
                    notFound.visibility = View.VISIBLE
                    if (sharedPref.language == "english")
                        notFoundText.text = "Not found"
                    else
                        notFoundText.text = "Topilmadi"
                }else{
                    notFound.visibility = View.GONE
                }
            }

            /*if (database!!.getAllFavourites().count == 0){
                notFound.visibility = View.VISIBLE
                if (sharedPref.language == "english"){
                    notFound.text = "Not found"
                }else{
                    notFound.text = "Topilmadi"
                }
            }
            else{
                notFound.visibility = View.INVISIBLE
            }*/
            adapter.upDateCursor(cursor)

            /*if (sharedPref.language == "english") {
                adapter.upDateCursor(database!!.getAllEnglishWords())
                adapter.upDateCursor(database!!.search(searchWord.text.toString(), "english"))
            } else {
                searchWord.hint = "Qidiruv - Search"
                adapter.upDateCursor(database!!.getAllUzbekWords())
                adapter.upDateCursor(database!!.search(searchWord.text.toString(), "uzbek"))
            }*/

            /*searchWord.doAfterTextChanged {

                //todo
                if (it.toString().isNotBlank()) {
                    val cursor = database?.search(it.toString(), sharedPref.language)
                    myRv.adapter = adapter
                    adapter.upDateCursor(cursor!!)
                } else if (sharedPref.language == "english") {
                    adapter.upDateCursor(database!!.getAllEnglishWords())
                } else {
                    adapter.upDateCursor(database!!.getAllUzbekWords())
                }
            }
            btnSwap.setOnClickListener {

                if (sharedPref.language == "english") {
                    sharedPref.language = "uzbek"
                    searchWord.hint = "Qidiruv - Search"
                    adapter.upDateCursor(database!!.getAllUzbekWords())
                    adapter.upDateCursor(database!!.search(searchWord.text.toString(), "uzbek"))
                } else {
                    sharedPref.language = "english"
                    searchWord.hint = "Search - Qidiruv"
                    adapter.upDateCursor(database!!.getAllEnglishWords())
                    adapter.upDateCursor(database!!.search(searchWord.text.toString(), "english"))
                }

                adapter.setLang(sharedPref.language)

                myRv.adapter = adapter
            }*/
            adapter.setClickLikeListener { id, like ->
                if (like == 1) database?.removeFavourite(id)
                else database?.addFavourite(id)
                if (sharedPref.language == "english") {
                    adapter.upDateCursor(database!!.getAllFavourites())
                } else {
                    adapter.upDateCursor(database!!.getAllUzbekWords())
                    adapter.upDateCursor(database!!.getAllFavourites())
                }
            }


        }
        adapter.setClickItemListener {
            showItemDialog(it)
        }

    }

    private fun showItemDialog(item: WordData) {
        val dialog = BottomSheetDialog(requireContext())

        if (sharedPref.language == "english") {
            dialog.setContentView(R.layout.word_dialog)

            dialog.findViewById<ImageView>(R.id.sound)?.setOnClickListener {
                tts!!.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, "")
            }
            val word = dialog.findViewById<TextView>(R.id.word)
            val translate = dialog.findViewById<TextView>(R.id.uzbek)
            val type = dialog.findViewById<TextView>(R.id.type)
            val transcript = dialog.findViewById<TextView>(R.id.transcript)
            word?.text = item.word
            translate?.text = item.translate
            type?.text = item.type
            transcript?.text = item.transcript


        } else {
            dialog.setContentView(R.layout.word_dialog_uzbek)
            dialog.findViewById<ImageView>(R.id.sound)?.visibility = View.GONE
            val word = dialog.findViewById<TextView>(R.id.word)
            val translate = dialog.findViewById<TextView>(R.id.uzbek)
            val type = dialog.findViewById<TextView>(R.id.type)

            var turi = item.type

            if (turi == "noun") {
                turi = "ot"
            }
            if (turi == "verb") {
                turi = "fel"
            }
            if (turi == "adj") {
                turi = "sifat"
            }
            if (turi == "adv") {
                turi = "ravish"
            }

            word?.text = item.word
            translate?.text = item.translate
            type?.text = turi


        }

        dialog.show()


    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.UK)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(
                    requireContext(),
                    "This language is not supported",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }
    }

}