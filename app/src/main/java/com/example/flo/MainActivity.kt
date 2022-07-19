package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var timer : Timer
    private var song:Song = Song()
    private var gson:Gson = Gson()
    private var mediaPlayer : MediaPlayer? = null
    private var playingStatus : Boolean = false

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        inputDummyAlbums()
        initBottomNavigation()

        binding.mainMiniplayerBtn.setOnClickListener {
            playingStatus = true
            setPlayerStatus(playingStatus)
        }

        binding.mainMiniplayerPauseBtn.setOnClickListener {
            playingStatus = false
            song.ing_second = mediaPlayer!!.currentPosition
            setPlayerStatus(playingStatus)
        }

        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())

        startTimer()
        val music = resources.getIdentifier("music_fmr", "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        setPlayerStatus(playingStatus)

    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun getJwt(): String?{
        val spf = this.getSharedPreferences("auth2",AppCompatActivity.MODE_PRIVATE)

        return spf!!.getString("jwt", "")
    }

    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.songProgressSb.progress = (song.second*100000) / song.playTime
    }
    private fun moveSong(direct: Int){
        if (nowPos + direct < 0){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }

        if (nowPos + direct >= songs.size){
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct
        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

    }

//    private fun setPlayer(song : Song){ //여기서 유저에게 보여주는 부분을 만들어줌.
//        binding.songTitleTv.text=song.title
//        binding.songSingerTv.text=song.singer
//        binding.songStartPointTv.text=String.format("%02d:%02d", ((song.ing_second * song.playTime)  / 100000) / 60,
//            ((song.ing_second * song.playTime) / 100000) % 60)
//        binding.songEndPointTv.text=String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
//        binding.albumExp2ImgIv.setImageResource(song.coverImg!!)
//        binding.songProgressSb.progress = song.ing_second
//
//        val music = resources.getIdentifier(song.music, "raw", this.packageName)
//        mediaPlayer = MediaPlayer.create(this, music)
//
//        if (song.isLike){
//            binding.myLikeIv.setImageResource(R.drawable.ic_my_like_on)
//        } else{
//            binding.myLikeIv.setImageResource(R.drawable.ic_my_like_off)
//        }
//
//        setPlayerStatus(song.isPlaying)
//    }

    override fun onStart() {
        super.onStart()
//        val sharePreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val songJson = sharePreferences.getString("songData", null)
//
//        song = if(songJson == null){
//            Song("라일락", "아이유(IU)", 0, 60, false, "music_fmr", 0, false)
//        }
//        else{
//            gson.fromJson(songJson, Song::class.java)
//        }
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 1)

        val songDB = SongDatabase.getInstance(this)!!

        song = if (songId == 0){
            songDB.songDao().getSong(1)
        }
        else{
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", song.id.toString())
        setMiniPlayer(song)

    }

    private fun startTimer(){
        timer = Timer(60, false)
        timer.start()
    }

    fun setPlayerStatus(isPlaying : Boolean){
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainMiniplayerPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else{
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainMiniplayerPauseBtn.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }
    }


    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true): Thread(){

        private var second : Int = 0
        public  var mills : Float = 0f

        override fun run(){
            super.run()
            try{
                while(true){

                    if(second == playTime){
                        break;
                    }

                    if(isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread{
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }
                        if(mills % 1000 == 0f){
                            second++
                        }
                    }
                }
            }
            catch(e: InterruptedException){
                Log.e("Song", "Thread가 죽었습니다. ${e.message}")
            }

        }
    }

//    override fun onPause() {
//        super.onPause()
//        setPlayerStatus(false)
//        song.second = ((binding.songProgressSb.progress * song.playTime) / 100) / 1000
//
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        val songJson = gson.toJson(song)
//        editor.putString("songData", songJson)
//
//        editor.apply()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        timer.interrupt()
//        mediaPlayer?.release()
//        mediaPlayer = null
//    }

    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_fmr",
                0,
                R.drawable.img_album_exp2,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                0,
                R.drawable.img_album_exp5,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "lilac",
                "아이유(IU)",
                0,
                230,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp4,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_nl",
                0,
                R.drawable.img_album_exp3,
                false,
            )
        )

        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_bwl",
                0,
                R.drawable.img_album_exp,
                false

            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun inputDummyAlbums(){
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbum()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                0, "Flu", "아이유(IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                1, "BBoom BBoom", "모모랜드(MOMOLAND)", R.drawable.img_album_exp5
            )
        )

        songDB.albumDao().insert(
            Album(
                2, "lilac", "아이유(IU)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                3, "Next Level", "에스파(AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                4, "Boys with Luv", "방탄소년단", R.drawable.img_album_exp
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }
}