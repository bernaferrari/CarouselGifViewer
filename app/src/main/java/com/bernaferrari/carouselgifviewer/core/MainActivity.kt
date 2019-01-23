package com.bernaferrari.carouselgifviewer.core

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import com.bernaferrari.carouselgifviewer.R

class MainActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
