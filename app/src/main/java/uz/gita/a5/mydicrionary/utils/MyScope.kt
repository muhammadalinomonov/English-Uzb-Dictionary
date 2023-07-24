package uz.gita.a5.mydicrionary.utils

fun <T> T.myApply( block: T.()-> Unit) {
    block(this)
}