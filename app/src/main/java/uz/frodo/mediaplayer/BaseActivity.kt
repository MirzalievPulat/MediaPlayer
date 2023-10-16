package uz.frodo.mediaplayer

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import uz.frodo.mediaplayer.adapter.MyAdapter
import uz.frodo.mediaplayer.databinding.ActivityBaseBinding
import uz.frodo.mediaplayer.models.Song

class BaseActivity : AppCompatActivity() {
    lateinit var binding: ActivityBaseBinding
    lateinit var list:ArrayList<Song>
    lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        list = ArrayList()
        adapter = MyAdapter(this,list)
        binding.rv.adapter = adapter

        checkPermissions()

//        binding.editText.addTextChangedListener(object :TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                searchSong(s.toString())
//            }
//        })


    }

    private fun checkPermissions() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MODIFY_AUDIO_SETTINGS
        ).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if (p0?.areAllPermissionsGranted() == true){
                    fetchSongs()
                }else{
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this@BaseActivity,Manifest.permission
                            .READ_EXTERNAL_STORAGE) &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(this@BaseActivity,Manifest.permission
                                .MODIFY_AUDIO_SETTINGS)){
                        AlertDialog.Builder(this@BaseActivity).apply {
                            setMessage("Please go to settings and give permissions")
                            setPositiveButton("OK") { dialog, which ->
                                val intent = Intent(
                                    Intent.ACTION_OPEN_DOCUMENT, Uri.fromParts(
                                        "package",
                                        this@BaseActivity.packageName, null
                                    )
                                )
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                startActivity(intent)
                                dialog.dismiss()
                            }
                        }.show()

                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                p1?.continuePermissionRequest()
            }
        })
            .check()
    }

    fun fetchSongs(){
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID,
        )
        val cursor = contentResolver.query(songUri,projection,selection,null,null)
        if (cursor != null && cursor.moveToFirst()){

            do {
                val id = cursor.getLong(0)
                val artist = cursor.getString(1)
                val name = cursor.getString(2)
                val data = cursor.getString(3)
                val date = cursor.getLong(4)
                val albumId = cursor.getLong(5)

                val uri = Uri.parse("content://media/external/audio/albumart")
                val albumUri= ContentUris.withAppendedId(uri, albumId)
                val albumArt = try {
                    MediaStore.Images.Media.getBitmap(contentResolver, albumUri)
                }catch (e:Exception){
                    null
                }


                if (isSongFile(data)) {
                    list.add(Song(id, artist, name, data, date, albumArt))
                }

            } while (cursor.moveToNext())
        }
        cursor?.close()
        adapter.notifyDataSetChanged()
    }
    private fun isSongFile(filePath: String): Boolean {
        val durationThresholdMillis = 30000
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(filePath)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
            duration >= durationThresholdMillis
        } catch (e: Exception) {
            false
        } finally {
            retriever.release()
        }
    }

    fun searchSong(value:String){
        var searchList = ArrayList<Song>()
        for (song in list){
            var added = false
            if (song.name!!.lowercase().contains(value.lowercase())){
                searchList.add(song)
                added = true
            }
            if (!added){
                if (song.artist!!.lowercase().contains(value.lowercase())){
                    searchList.add(song)
                }
            }

        }
        adapter.updateList(searchList)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (MyMediaPlayer.instance != null){
            MyMediaPlayer.instance!!.release()
        }

    }
}