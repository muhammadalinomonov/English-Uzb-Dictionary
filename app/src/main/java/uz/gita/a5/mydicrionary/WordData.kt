package uz.gita.a5.mydicrionary

import java.io.Serializable

data class WordData(
    val id: Int,
    val word:String,
    val type:String,
    val transcript:String,
    val translate:String,
    val favourite:Int
): Serializable