package com.example.firebasetestapp.extention

import android.view.View

fun View.Visible(isVisible : Boolean){
    this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}