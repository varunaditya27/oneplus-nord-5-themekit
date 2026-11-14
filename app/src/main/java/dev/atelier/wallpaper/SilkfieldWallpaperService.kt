package dev.atelier.wallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat
import dev.atelier.data.Material
import dev.atelier.data.WallpaperScene

/**
 * ATELIER / cinema-grade procedural wallpaper engine.
 *
 * Three locally rendered scenes, OS wallpaper parallax, restrained tap ripples.
 * Neither foreground services nor sensor polling. It spends no frames when
 * hidden, motion-disabled or in battery-saving mode.
 */
class SilkfieldWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = SilkfieldEngine()

    inner class SilkfieldEngine : Engine() {
        private val handler=Handler(Looper.getMainLooper())
        private val renderer=AtelierSceneRenderer()
        private val prefs=getSharedPreferences("atelier.v1",Context.MODE_PRIVATE)
        private val power=getSystemService(Context.POWER_SERVICE) as PowerManager
        private var visible=false
        private var destroyed=false
        private var xOffset=.5f
        private var yOffset=.5f
        private var zoom=0f
        private var touchX=-1f
        private var touchY=-1f
        private var lastTouch=0L
        private val origin=SystemClock.elapsedRealtime()
        private val ticker=Runnable { drawFrame() }

        private val listener=SharedPreferences.OnSharedPreferenceChangeListener { _,key ->
            if(key in setOf("reduce_motion","motion_mode","wallpaper_scene","material",
                    "parallax_enabled","ambient_enabled")) {
                handler.removeCallbacks(ticker)
                if(visible) drawFrame()
            }
        }
        private val receiver=object:BroadcastReceiver(){
            override fun onReceive(context:Context?,intent:Intent?){
                if(intent?.action==PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                    handler.removeCallbacks(ticker)
                    if(visible) drawFrame()
                }
            }
        }

        init {
            setOffsetNotificationsEnabled(true)
            setTouchEventsEnabled(true)
            prefs.registerOnSharedPreferenceChangeListener(listener)
            ContextCompat.registerReceiver(
                this@SilkfieldWallpaperService,receiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        private fun scene():WallpaperScene=runCatching {
            WallpaperScene.valueOf(prefs.getString("wallpaper_scene","SILKFIELD")!!)
        }.getOrDefault(WallpaperScene.SILKFIELD)

        private fun material():Material=runCatching {
            Material.valueOf(prefs.getString("material","INK")!!)
        }.getOrDefault(Material.INK)

        private fun animated():Boolean=
            prefs.getBoolean("ambient_enabled",true) &&
            !prefs.getBoolean("reduce_motion",false) &&
            prefs.getString("motion_mode","EXPRESSIVE")!="STILL" &&
            !power.isPowerSaveMode

        override fun onVisibilityChanged(isVisible:Boolean) {
            visible=isVisible
            handler.removeCallbacks(ticker)
            if(isVisible)drawFrame()
        }

        override fun onSurfaceChanged(holder:SurfaceHolder,format:Int,width:Int,height:Int) {
            super.onSurfaceChanged(holder,format,width,height)
            if(visible)drawFrame()
        }

        override fun onOffsetsChanged(
            xOffset:Float,yOffset:Float,xOffsetStep:Float,yOffsetStep:Float,
            xPixelOffset:Int,yPixelOffset:Int
        ) {
            this.xOffset=xOffset
            this.yOffset=yOffset
            if(visible)drawFrame()
        }

        override fun onZoomChanged(zoom:Float) {
            this.zoom=zoom.coerceIn(0f,1f)
            if(visible)drawFrame()
        }

        override fun onTouchEvent(event:MotionEvent) {
            super.onTouchEvent(event)
            if(event.actionMasked==MotionEvent.ACTION_DOWN && animated()) {
                touchX=event.x
                touchY=event.y
                lastTouch=SystemClock.elapsedRealtime()
                if(visible)drawFrame()
            }
        }

        private fun drawFrame(){
            if(destroyed||!visible)return
            var canvas:android.graphics.Canvas?=null
            try{
                canvas=surfaceHolder.lockCanvas()
                if(canvas!=null){
                    val now=SystemClock.elapsedRealtime()
                    val seconds=if(animated())(now-origin)/1000f else 0f
                    val parallax=prefs.getBoolean("parallax_enabled",true)
                    renderer.render(
                        canvas,scene(),material(),seconds,
                        if(parallax) xOffset+(zoom*.05f) else .5f,
                        if(parallax) yOffset else .5f,
                        touchX,touchY,
                        if(lastTouch>0L)(now-lastTouch)/1000f else 5f
                    )
                }
            }catch(_:IllegalArgumentException){
                // Surface may disappear between visibility and lock.
            }catch(_:IllegalStateException){
                // The surface is being replaced by the wallpaper picker.
            }finally{
                if(canvas!=null)runCatching{surfaceHolder.unlockCanvasAndPost(canvas)}
            }
            handler.removeCallbacks(ticker)
            if(animated()&&visible&&!destroyed){
                val cadence=if(prefs.getString("motion_mode","EXPRESSIVE")=="EXPRESSIVE")
                    42L else 85L
                handler.postDelayed(ticker,cadence)
            }
        }

        override fun onDestroy() {
            destroyed=true
            visible=false
            handler.removeCallbacks(ticker)
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
            runCatching{unregisterReceiver(receiver)}
            super.onDestroy()
        }
    }
}
