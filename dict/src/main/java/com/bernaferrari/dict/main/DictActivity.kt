package com.bernaferrari.dict.main

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import com.bernaferrari.dict.R
import com.bernaferrari.dict.data.DictDataSource
import javax.inject.Inject

class DictActivity : BaseMvRxActivity() {

    @Inject
    lateinit var repo: DictDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gif_activity)
    }
}
