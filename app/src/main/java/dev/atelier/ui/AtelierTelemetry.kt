package dev.atelier.ui

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.atelier.data.AtelierSettings
import dev.atelier.data.MotionMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class DeviceTelemetry(
    val battery: Int? = null,
    val memoryUsed: Int? = null,
    val storageUsed: Int? = null
)

object DeviceTelemetryReader {
    fun read(context: Context):DeviceTelemetry {
        val battery = runCatching {
            val mgr = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            mgr.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                .takeIf { it in 0..100 }
        }.getOrNull()
        val ram = runCatching {
            val mgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = ActivityManager.MemoryInfo()
            mgr.getMemoryInfo(info)
            if(info.totalMem <= 0L) null else
                ((info.totalMem-info.availMem).toDouble()/info.totalMem*100)
                    .toInt().coerceIn(0,100)
        }.getOrNull()
        val storage = runCatching {
            val stat=StatFs(Environment.getDataDirectory().absolutePath)
            val size=stat.totalBytes
            if(size<=0L) null else
                ((size-stat.availableBytes).toDouble()/size*100)
                    .toInt().coerceIn(0,100)
        }.getOrNull()
        return DeviceTelemetry(battery,ram,storage)
    }
}

@Composable
fun AtelierTelemetryStrip(
    c: AtelierColors,
    settings: AtelierSettings,
    modifier: Modifier = Modifier
) {
    val context=LocalContext.current
    var data by remember { mutableStateOf(DeviceTelemetry()) }
    LaunchedEffect(context) {
        while(true) {
            data=withContext(Dispatchers.Default) { DeviceTelemetryReader.read(context) }
            delay(60000)
        }
    }
    Row(modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(13.dp)) {
        MiniGauge("BATTERY",data.battery,c,settings,Modifier.weight(1f))
        MiniGauge("MEMORY",data.memoryUsed,c,settings,Modifier.weight(1f))
        MiniGauge("STORAGE",data.storageUsed,c,settings,Modifier.weight(1f))
    }
}

@Composable
private fun MiniGauge(
    name:String, value:Int?, c:AtelierColors,
    settings:AtelierSettings,modifier:Modifier=Modifier
) {
    val portion by animateFloatAsState(
        targetValue=(value?:0).coerceIn(0,100)/100f,
        animationSpec=tween(
            if(settings.reduceMotion || settings.motionMode==MotionMode.STILL)0
            else if(settings.motionMode==MotionMode.EXPRESSIVE)680 else 420
        ),
        label="system-gauge-$name"
    )
    Column(modifier,verticalArrangement=Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()) {
            Text(name,color=c.muted,fontFamily=AtelierFonts.technical,fontSize=8.sp,
                modifier=Modifier.weight(1f))
            Text(value?.let{"$it%"}?:"–",color=c.text,
                fontSize=10.sp,fontWeight=FontWeight.Medium)
        }
        Box(Modifier.fillMaxWidth().height(2.dp)
            .background(c.border,RoundedCornerShape(3.dp))) {
            Box(Modifier.fillMaxWidth(portion.coerceIn(0f,1f)).fillMaxHeight()
                .background(c.accent,RoundedCornerShape(3.dp)))
        }
    }
}
