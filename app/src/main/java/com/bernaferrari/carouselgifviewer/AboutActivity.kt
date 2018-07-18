package com.bernaferrari.carouselgifviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import androidx.core.net.toUri
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.IconicsDrawable

class AboutActivity : MaterialAboutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.About)
        super.onCreate(savedInstanceState)
    }

    override fun getActivityTitle() = applicationInfo.loadLabel(packageManager)

    override fun getMaterialAboutList(context: Context): MaterialAboutList =
        createMaterialAboutList(this)

    private fun createMaterialAboutList(c: Context): MaterialAboutList {
        val grey = ContextCompat.getColor(c, R.color.md_grey_800)
        val iconSize = 18

        val appCardBuilder = MaterialAboutCard.Builder()

        appCardBuilder.addItem(
            MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .desc("© 2018 Bernardo Ferrari")
                .icon(R.mipmap.ic_launcher)
                .build()
        )

        appCardBuilder.addItem(
            ConvenienceBuilder.createVersionActionItem(
                c,
                IconicsDrawable(c)
                    .icon(CommunityMaterial.Icon.cmd_update)
                    .color(grey)
                    .sizeDp(iconSize),
                c.getText(R.string.version),
                false
            )
        )

        appCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.licenses)
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .color(grey)
                        .sizeDp(iconSize)
                )
                .setOnClickAction {
                    startActivity(
                        Intent(
                            this,
                            AboutLicenseActivity::class.java
                        )
                    )
                }
                .build())

        val author = MaterialAboutCard.Builder()

        author.title(R.string.author)

        author.addItem(
            MaterialAboutActionItem.Builder()
                .text("Bernardo Ferrari")
                .subText("bernaferrari")
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_reddit)
                        .color(grey)
                        .sizeDp(iconSize)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebsiteOnClickAction(
                        c,
                        "https://www.reddit.com/user/bernaferrari".toUri()
                    )
                )
                .build()
        )

        author.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.github)
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .color(grey)
                        .sizeDp(iconSize)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebsiteOnClickAction(
                        c,
                        "https://github.com/bernaferrari/CarouselGifViewer".toUri()
                    )
                )
                .build()
        )

        author.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.email)
                .subText("bernaferrari2@gmail.com")
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .color(grey)
                        .sizeDp(iconSize)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createEmailOnClickAction(
                        c,
                        "bernaferrari2@gmail.com",
                        getString(R.string.email_subject)
                    )
                )
                .build()
        )

        val otherCardBuilder = MaterialAboutCard.Builder()
        otherCardBuilder.title(R.string.help)

        otherCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.bugs)
                .subText("bernaferrari2@gmail.com")
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_bug)
                        .color(grey)
                        .sizeDp(iconSize)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createEmailOnClickAction(
                        c,
                        "bernaferrari2@gmail.com",
                        getString(R.string.email_subject)
                    )
                )
                .build()
        )

        return MaterialAboutList(
            appCardBuilder.build(),
            author.build(),
            otherCardBuilder.build()
        )
    }
}