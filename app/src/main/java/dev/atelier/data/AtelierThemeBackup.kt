package dev.atelier.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Human-readable, local-only ATELIER theme export.
 *
 * Only appearance preferences, icon overrides and app pins are serialized.
 * No device IDs, app usage, health data, or credential-like fields are read.
 */
object AtelierThemeBackup {
    private const val FORMAT="atelier.theme.v1"
    const val MAX_BYTES=262144

    fun encode(settings:AtelierSettings):String {
        val v=JSONObject()
            .put("material",settings.material.name)
            .put("clockStyle",settings.clockStyle.name)
            .put("iconStyle",settings.iconStyle.name)
            .put("iconWeight",settings.iconWeight.name)
            .put("motionMode",settings.motionMode.name)
            .put("wallpaperScene",settings.wallpaperScene.name)
            .put("parallaxEnabled",settings.parallaxEnabled)
            .put("ambientEnabled",settings.ambientEnabled)
            .put("openingSequenceEnabled",settings.openingSequenceEnabled)
            .put("hapticsEnabled",settings.hapticsEnabled)
            .put("focusMode",settings.focusMode)
            .put("reduceMotion",settings.reduceMotion)
            .put("showLabels",settings.showLabels)
            .put("favorites",JSONArray(settings.favorites.sorted()))
            .put("iconOverrides",JSONObject(settings.iconOverrides.toSortedMap()))
        return JSONObject()
            .put("format",FORMAT)
            .put("settings",v)
            .toString(2)
    }

    fun decode(contents:String):AtelierSettings {
        require(contents.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) {
            "Theme backup exceeds size limit"
        }
        val root=JSONObject(contents)
        require(root.optString("format")==FORMAT) {
            "Not an ATELIER theme backup"
        }
        val data=root.getJSONObject("settings")
        fun <T:Enum<T>> select(values:Array<T>,key:String,default:T):T =
            values.firstOrNull { it.name==data.optString(key) } ?: default
        fun flag(key:String,default:Boolean):Boolean =
            if(data.has(key)) data.optBoolean(key,default) else default

        val savedPins=data.optJSONArray("favorites") ?: JSONArray()
        require(savedPins.length()<=1000) {"Too many saved app favorites"}
        val pins=buildSet {
            for(index in 0 until savedPins.length()) {
                val id=savedPins.optString(index)
                if(id.isNotBlank() && id.length<=256) add(id)
            }
        }
        val savedIcons=data.optJSONObject("iconOverrides") ?: JSONObject()
        require(savedIcons.length()<=1000) {"Too many custom app icons"}
        val overrides=buildMap {
            val iterator=savedIcons.keys()
            while(iterator.hasNext()) {
                val key=iterator.next()
                val glyph=savedIcons.optString(key)
                if(key.isNotBlank() && key.length<=256 &&
                    glyph.matches(Regex("[A-Z][A-Z0-9_]{1,45}"))) {
                    put(key,glyph)
                }
            }
        }
        return AtelierSettings(
            material=select(Material.entries.toTypedArray(),"material",Material.INK),
            clockStyle=select(ClockStyle.entries.toTypedArray(),"clockStyle",ClockStyle.SIGNATURE),
            iconStyle=select(IconStyle.entries.toTypedArray(),"iconStyle",IconStyle.THEMED),
            iconWeight=select(IconWeight.entries.toTypedArray(),"iconWeight",IconWeight.CORE),
            motionMode=select(MotionMode.entries.toTypedArray(),"motionMode",MotionMode.EXPRESSIVE),
            wallpaperScene=select(WallpaperScene.entries.toTypedArray(),"wallpaperScene",WallpaperScene.SILKFIELD),
            parallaxEnabled=flag("parallaxEnabled",true),
            ambientEnabled=flag("ambientEnabled",true),
            openingSequenceEnabled=flag("openingSequenceEnabled",true),
            hapticsEnabled=flag("hapticsEnabled",true),
            focusMode=flag("focusMode",false),
            reduceMotion=flag("reduceMotion",false),
            showLabels=flag("showLabels",true),
            favorites=pins,
            iconOverrides=overrides
        )
    }
}
