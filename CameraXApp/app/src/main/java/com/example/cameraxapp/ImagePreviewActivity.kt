package com.example.cameraxapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.example.cameraxapp.viewmodel.PreviewViewModel
import kotlinx.android.synthetic.main.activity_image_preview.*
import java.io.File
import java.util.*


val EXTENSION_WHITELIST = arrayOf("JPG")

class ImagePreviewActivity : AppCompatActivity() {

    lateinit var previewViewModel: PreviewViewModel
    private lateinit var mediaList: MutableList<File>

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment {
            previewViewModel._position.value =position.toString()


            return ImagePreviewFragment.create(mediaList[position])}
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
         previewViewModel =ViewModelProviders.of(this).get(PreviewViewModel::class.java)
        if (intent !=null && intent.extras !=null){
            val rootDirectory = File(intent.getStringExtra("path")!!)
            mediaList = rootDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
            }?.sortedDescending()?.toMutableList() ?: mutableListOf()
        }
        photo_view_pager.apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(supportFragmentManager)
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
        previewViewModel.position.observe(this, androidx.lifecycle.Observer {
            val fileName: String  = mediaList[it.toInt()].toString()

            val fileSaperator = "."
            val fileSaperatorTemp = "/"

            Log.d("TAG", "onCreate: "+it)
            val FileNamePrefix = fileName.substring(0, fileName.lastIndexOf(fileSaperatorTemp))
            val FileNameSuffix = fileName.substring(fileName.lastIndexOf(fileSaperator) +1, fileName.length)
            val timeStemp = fileName.substring(fileName.lastIndexOf(fileSaperatorTemp) +1, fileName.length)
            val time = timeStemp.substring(0,timeStemp.lastIndexOf(fileSaperator) )

            btnImageDetails.setOnClickListener {
                AlertDialog.Builder(this).apply {
                    setTitle("Image Details")
                    setMessage(FileNamePrefix + "\n" +
                            time)


                }.create().show()
            }
        })


    }
}