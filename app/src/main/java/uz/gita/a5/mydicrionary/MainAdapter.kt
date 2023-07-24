package uz.gita.a5.mydicrionary

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DataSetObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.a5.mydicrionary.databinding.ItemWordBinding
import uz.gita.a5.mydicrionary.databinding.ScreenMainBinding

class MainAdapter(private var cursor: Cursor) : Adapter<MainAdapter.MyViewHolder>() {

    private var isValid = false
    private var lang = ""
    private lateinit var notFoundListener:((Int) ->Unit)

    private var clickListener: ((WordData) -> Unit)? = null
    private var clickLikeListener: ((Int, Int) -> Unit)? = null


    fun setClickItemListener(block: (WordData) -> Unit) {
        clickListener = block
    }

    fun setClickLikeListener(block: ((Int, Int) -> Unit)) {
        clickLikeListener = block
    }

    private val notifyDataSetObserver = object : DataSetObserver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged() {
            isValid = true
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInvalidated() {
            isValid = false
            notifyDataSetChanged()
        }
    }

    init {
        cursor.registerDataSetObserver(notifyDataSetObserver)
        isValid = true
    }

    fun upDateCursor(newCursor: Cursor) {
        notFoundListener.invoke(newCursor.count)
        isValid = false
        cursor.unregisterDataSetObserver(notifyDataSetObserver)
        cursor.close()

        newCursor.registerDataSetObserver(notifyDataSetObserver)
        cursor = newCursor
        isValid = true
        notifyDataSetChanged()

    }

    @SuppressLint("Range")
    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textWord: TextView = view.findViewById(R.id.txt_orginal)
        private val textType: TextView = view.findViewById(R.id.txt_type)
        private val imageLike: ImageView = view.findViewById(R.id.is_saved)

        init {
            imageLike.setOnClickListener {
                cursor.moveToPosition(absoluteAdapterPosition)

                val cursorFav = cursor.getInt(cursor.getColumnIndex("favourite"))
                val cursorId = cursor.getInt(cursor.getColumnIndex("id"))
                val wordtype = cursor.getString(2)
                if (cursorFav == 1) {
                    imageLike.setImageResource(R.drawable.saved)
                } else {
                    imageLike.setImageResource(R.drawable.img_1)
                }
                clickLikeListener?.invoke(cursorId, cursorFav)
            }
            view.setOnClickListener {
                cursor.moveToPosition(absoluteAdapterPosition)

                val english = cursor.getString(cursor.getColumnIndex("english"))
                val uzbek = cursor.getString(cursor.getColumnIndex("uzbek"))
                val word: String
                val translate: String

                if (lang == "english") {
                    word = english
                    translate = uzbek
                    view.findViewById<TextView>(R.id.txt_type).visibility = View.VISIBLE

                } else {
                    word = uzbek
                    translate = english
                }

                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val type = cursor.getString(cursor.getColumnIndex("type"))
                val like = cursor.getInt(cursor.getColumnIndex("favourite"))
                val transcript = cursor.getString(cursor.getColumnIndex("transcript"))


                clickListener?.invoke(WordData(id, word, type, transcript, translate, like))
            }
        }


        fun bind() {



            cursor.moveToPosition(adapterPosition)
            val isFavourite: Int = cursor.getInt(6)

            val word = cursor.getString(if (lang == "english") 1 else 4)

            textWord.text = word
            var type = cursor.getString(2)
            if (lang == "english") {

            } else {
                if (type == "noun") type = "ot"
                if (type == "adv") type = "ravish"
                if (type == "verb") type = "fe'l"
                if (type == "adj") type = "sifat"
            }
            view.findViewById<TextView>(R.id.txt_type).text = type
            if (isFavourite == 1) {
                imageLike.setImageResource(R.drawable.saved)
            } else imageLike.setImageResource(R.drawable.img_1)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return if (isValid) cursor.count else 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
    }

    fun onDestroy() {
        cursor.unregisterDataSetObserver(notifyDataSetObserver)
        cursor.close()
    }

    fun setLang(language: String) {
        this.lang = language
    }

    fun setNotFountListener(block: (Int) -> Unit){
        notFoundListener = block
    }
}