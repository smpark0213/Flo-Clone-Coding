package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding
    lateinit var timer : Timer
    private var mediaPlayer : MediaPlayer? = null
    private var gson: Gson = Gson()
    public var temp : Int = 0

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    var isOne : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        binding.myLikeOnIb.setOnClickListener {
//            setLikeStatus(false)
//        }
//
//        binding.myLikeOffIb.setOnClickListener {
//            setLikeStatus(true)
//        }

        binding.repeatInactiveOn1Ib.setOnClickListener {
            isOne = false
            setRepeatStatus(0)
        }

        binding.repeatInactiveIb.setOnClickListener {
            setRepeatStatus(1)
        }

        binding.repeatInactiveOnIb.setOnClickListener {
            isOne = true
            setRepeatStatus(2) //한곡 반복
        }

        binding.randomInactiveOnIb.setOnClickListener {
            setRandomStatus(false)
        }

        binding.randomInactiveIb.setOnClickListener {
            setRandomStatus(true)
        }

//        binding.myLikeIv.setOnLongClickListener{
//            //Toast.makeText(this,"좋아요를 표시했습니다.", Toast.LENGTH_SHORT).show()
//        }

        initPlayList()
        initSong()
        initClickListener()

    }

    private fun moveSong(direct: Int){
        if (nowPos + direct < 0){
            Toast.makeText(this,"first song",Toast.LENGTH_SHORT).show()
            return
        }

        if (nowPos + direct >= songs.size){
            Toast.makeText(this,"last song",Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct
        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener{
            finish()
        }

        binding.play32Ib.setOnClickListener{
            setPlayerStatus(true)
        }
        binding.pause32Ib.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.skipNext32Ib.setOnClickListener {
            moveSong(+1)
        }
        binding.skipPrevious32Ib.setOnClickListener {
            moveSong(-1)
        }

        binding.myLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

    private fun initSong(){ //여기서 intent값을 이용해서 초기 세팅
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)
        Log.d("now Song ID",songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if(!isLike){
                binding.myLikeIv.setImageResource(R.drawable.ic_my_like_on)
            }
            else{
                binding.myLikeIv.setImageResource(R.drawable.ic_my_like_off)
            }

    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for(i in 0 until songs.size){
            if(songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song : Song){ //여기서 유저에게 보여주는 부분을 만들어줌.
        binding.songTitleTv.text=song.title
        binding.songSingerTv.text=song.singer
        binding.songStartPointTv.text=String.format("%02d:%02d", ((song.ing_second * song.playTime)  / 100000) / 60,
            ((song.ing_second * song.playTime) / 100000) % 60)
        binding.songEndPointTv.text=String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.albumExp2ImgIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = song.ing_second

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        if (song.isLike){
            binding.myLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.myLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying)
    }

    fun setPlayerStatus(isPlaying : Boolean){
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying
//        mediaPlayer?.start()
//        mediaPlayer?.pause()
        if(isPlaying){
//            if(temp == 0){ //처음 들어왔을때만 여기 올거임
//                temp = 1
//                mediaPlayer?.seekTo(songs[nowPos].ing_second)
//            }
            Log.e("real second", songs[nowPos].second.toString())
            Log.e("real ing second", songs[nowPos].ing_second.toString())
            binding.play32Ib.visibility = View.INVISIBLE
            binding.pause32Ib.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else{
            binding.play32Ib.visibility = View.VISIBLE
            binding.pause32Ib.visibility = View.INVISIBLE
            if(mediaPlayer?.isPlaying == true){
//                songs[nowPos].ing_second = mediaPlayer!!.currentPosition
                mediaPlayer?.pause()
            }
        }
    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    fun setRepeatStatus(Repeat : Int){
        if(Repeat == 0){
            binding.repeatInactiveIb.visibility = View.VISIBLE
            binding.repeatInactiveOnIb.visibility = View.GONE
            binding.repeatInactiveOn1Ib.visibility = View.GONE
        }
        if(Repeat == 1){
            binding.repeatInactiveIb.visibility = View.GONE
            binding.repeatInactiveOnIb.visibility = View.VISIBLE
            binding.repeatInactiveOn1Ib.visibility = View.GONE
        }

        if(Repeat == 2){
            binding.repeatInactiveIb.visibility = View.GONE
            binding.repeatInactiveOnIb.visibility = View.GONE
            binding.repeatInactiveOn1Ib.visibility = View.VISIBLE
        }
    }


    fun setRandomStatus(isRandom : Boolean){
        if(isRandom){
            binding.randomInactiveIb.visibility = View.INVISIBLE
            binding.randomInactiveOnIb.visibility = View.VISIBLE
        }
        else{
            binding.randomInactiveIb.visibility = View.VISIBLE
            binding.randomInactiveOnIb.visibility = View.INVISIBLE
        }
    }

//    fun setLikeStatus(isLike : Boolean){
//        if(isLike){
//            binding.myLikeOffIb.visibility = View.INVISIBLE
//            binding.myLikeOnIb.visibility = View.VISIBLE
//        }
//        else{
//            binding.myLikeOffIb.visibility = View.VISIBLE
//            binding.myLikeOnIb.visibility = View.INVISIBLE
//        }
//    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true): Thread(){

        private var second : Int = ((songs[nowPos].ing_second * songs[nowPos].playTime)  / 100000)
        private  var mills : Float = ((songs[nowPos].ing_second / 100) * songs[nowPos].playTime).toFloat()

        override fun run(){

            super.run()
            try{
                while(true){
                    //Log.e("songsecond", mills.toString())
                    if(second == playTime && isOne == true){
                        Log.e("tjfakdurldhsl?", mills.toString())
                        second = 0
                        mills = 0f
                    }
                    if(second == playTime && isOne == false){
                        break;
                    }

                    if(isPlaying){
                        sleep(50)
                        mills += 50
                        //Log.e("come here", mills.toString())

                        runOnUiThread{
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }
                        if(mills % 1000 == 0f){
                            Log.e("come here", second.toString())
                            runOnUiThread{
                                binding.songStartPointTv.text=String.format("%02d:%02d", second / 60, second % 60)
                            }
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

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime) / 100) / 1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("songid", songs[nowPos].id)

        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}