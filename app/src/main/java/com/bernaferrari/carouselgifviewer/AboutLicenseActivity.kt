package com.bernaferrari.carouselgifviewer

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.IconicsDrawable

class AboutLicenseActivity : MaterialAboutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.About)
        super.onCreate(savedInstanceState)
    }

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        return createMaterialAboutLicenseList(context, R.color.md_grey_800)
    }

    override fun getActivityTitle(): CharSequence? {
        return getString(R.string.mal_title_licenses)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }

    private fun createLicenseCard(
        libraryTitle: String,
        year: String,
        name: String,
        license: OpenSourceLicense,
        context: Context,
        colorIcon: Int
    ): MaterialAboutCard {
        return ConvenienceBuilder.createLicenseCard(
            context,
            IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_book)
                .color(ContextCompat.getColor(context, colorIcon))
                .sizeDp(18),
            libraryTitle,
            year,
            name,
            license
        )
    }

    private fun createMaterialAboutLicenseList(c: Context, colorIcon: Int): MaterialAboutList {

        val materialAboutLIbraryLicenseCard = createLicenseCard(
            "material-about-library",
            "2016",
            "Daniel Stone",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )
        val androidIconicsLicenseCard = createLicenseCard(
            "Android Iconics",
            "2017",
            "Mike Penz",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val logger = createLicenseCard(
            "Logger",
            "2017",
            "Orhan Obut",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val ExoMedia = createLicenseCard(
            "ExoMedia",
            "2015-2017",
            "Brian Wernick",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val glide =
            createLicenseCard("Glide", "2017", "Google Inc.", OpenSourceLicense.MIT, c, colorIcon)

        val aosp = createLicenseCard(
            "The Android Open Source Project",
            "2018",
            "Google Inc.",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val rxjava = createLicenseCard(
            "RxJava: Reactive Extensions for the JVM",
            "2016-present",
            "RxJava Contributors",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val rxandroid = createLicenseCard(
            "RxAndroid: Reactive Extensions for Android",
            "2015",
            "The RxAndroid authors",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val rxrelay = createLicenseCard(
            "RxRelay",
            "2015",
            "Jake Wharton",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val groupie = createLicenseCard("Groupie", "2016", "", OpenSourceLicense.MIT, c, colorIcon)

        val discrete = createLicenseCard(
            "DiscreteScrollView",
            "2017",
            "Yaroslav Shevchuk",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val okhttp = createLicenseCard("OkHttp", "", "", OpenSourceLicense.APACHE_2, c, colorIcon)

        val kotlin = createLicenseCard(
            "Kotlin",
            "2010-2017",
            "JetBrains",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        val materialprogressbar = createLicenseCard(
            "MaterialProgressBar",
            "2015",
            "Hai Zhang",
            OpenSourceLicense.APACHE_2,
            c,
            colorIcon
        )

        return MaterialAboutList(
            aosp,
            androidIconicsLicenseCard,
            rxjava,
            rxandroid,
            rxrelay,
            materialprogressbar,
            groupie,
            discrete,
            materialAboutLIbraryLicenseCard,
            ExoMedia,
            glide,
            logger,
            kotlin,
            okhttp
        )
    }
}

