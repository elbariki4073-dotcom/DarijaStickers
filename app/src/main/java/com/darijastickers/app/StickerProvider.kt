package com.darijastickers.app

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.content.res.AssetFileDescriptor
import org.json.JSONObject
import java.io.File

class StickerProvider : ContentProvider() {
 companion object {
  const val AUTHORITY = "com.darijastickers.app.stickercontentprovider"
  const val META = "metadata"
  val columns = arrayOf("identifier","name","publisher","tray_image_file","android_play_store_link","ios_app_store_link","publisher_email","publisher_website","privacy_policy_website","license_agreement_website","image_data_version","avoid_cache","animated_sticker_pack")
 }
 private val packs by lazy { JSONObject(context!!.resources.openRawResource(R.raw.sticker_packs).bufferedReader().use { it.readText() }).getJSONArray("sticker_packs") }
 override fun onCreate() = true
 private fun pack(id:String):JSONObject? { for (i in 0 until packs.length()) { val p=packs.getJSONObject(i); if(p.getString("identifier")==id)return p };return null }
 override fun query(uri:Uri, projection:Array<out String>?, selection:String?, selectionArgs:Array<out String>?, sortOrder:String?):Cursor? {
  val parts=uri.pathSegments
  if(parts.isEmpty())return null
  if(parts[0]==META) {
   val result=MatrixCursor(columns)
   val ids=if(parts.size>=2) listOf(parts[1]) else (0 until packs.length()).map { packs.getJSONObject(it).getString("identifier") }
   for(id in ids){ val p=pack(id)?:continue
    result.addRow(arrayOf<Any?>(id,p.getString("name"),p.getString("publisher"),p.getString("tray_image_file"),"","","","","","",p.optString("image_data_version","1"),if(p.optBoolean("avoid_cache"))1 else 0,0))
   };return result
  }
  if(parts[0]=="stickers" && parts.size==2) {
   val p=pack(parts[1])?:return null
   val cursor=MatrixCursor(arrayOf("sticker_file_name","sticker_emoji"))
   val stickers=p.getJSONArray("stickers")
   for(i in 0 until stickers.length()){val s=stickers.getJSONObject(i);val emojis=s.optJSONArray("emojis");cursor.addRow(arrayOf(s.getString("image_file"),if(emojis!=null)(0 until emojis.length()).joinToString(","){emojis.getString(it)} else ""))}
   return cursor
  }
  return null
 }
 override fun openAssetFile(uri:Uri,mode:String):AssetFileDescriptor? {
  if(mode!="r")return null
  val parts=uri.pathSegments
  if(parts.size!=3 || parts[0]!="stickers")return null
  val p=pack(parts[1])?:return null
  val filename=parts[2]
  val allowed=filename==p.getString("tray_image_file") || (0 until p.getJSONArray("stickers").length()).any{p.getJSONArray("stickers").getJSONObject(it).getString("image_file")==filename}
  if(!allowed || filename.contains("/"))return null
  val file=File(context!!.cacheDir,"stickers/${parts[1]}/$filename")
  if(!file.exists()){file.parentFile?.mkdirs();context!!.assets.open(filename).use{input->file.outputStream().use{output->input.copyTo(output)}}}
  return AssetFileDescriptor(ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY),0,AssetFileDescriptor.UNKNOWN_LENGTH)
 }
 override fun getType(uri:Uri):String? = if(uri.pathSegments.firstOrNull()=="stickers") "image/webp" else "vnd.android.cursor.dir/vnd.$AUTHORITY.stickerpack"
 override fun insert(uri:Uri,values:ContentValues?):Uri?=null
 override fun delete(uri:Uri,selection:String?,selectionArgs:Array<out String>?):Int=0
 override fun update(uri:Uri,values:ContentValues?,selection:String?,selectionArgs:Array<out String>?):Int=0
}
