package com.darijastickers.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONObject

class MainActivity : Activity() {
 private val dark=Color.rgb(26,54,49)
 private val green=Color.rgb(20,120,91)
 override fun onCreate(savedInstanceState:Bundle?) {
  super.onCreate(savedInstanceState)
  val scroll=ScrollView(this);scroll.setBackgroundColor(Color.rgb(250,248,241))
  val layout=LinearLayout(this);layout.orientation=LinearLayout.VERTICAL;layout.setPadding(24,32,24,32);layout.layoutDirection=View.LAYOUT_DIRECTION_RTL
  scroll.addView(layout);setContentView(scroll)
  val title=TextView(this);title.text="🇲🇦  Darija Stickers";title.textSize=29f;title.setTypeface(null,Typeface.BOLD);title.setTextColor(dark);title.gravity=Gravity.CENTER
  layout.addView(title)
  val sub=TextView(this);sub.text="ستيكرز مغربية زوينة لواتساب\nاختر المجموعة وزيدها لواتساب بضغطة واحدة";sub.textSize=16f;sub.gravity=Gravity.CENTER;sub.setTextColor(dark);sub.setPadding(0,14,0,26);layout.addView(sub)
  val packs=JSONObject(resources.openRawResource(R.raw.sticker_packs).bufferedReader().use{it.readText()}).getJSONArray("sticker_packs")
  for(i in 0 until packs.length()) {
   val p=packs.getJSONObject(i);val id=p.getString("identifier")
   val card=LinearLayout(this);card.orientation=LinearLayout.VERTICAL;card.setPadding(18,18,18,18);card.setBackgroundColor(Color.WHITE)
   val name=TextView(this);name.text=p.getString("name");name.textSize=23f;name.setTextColor(dark);name.setTypeface(null,Typeface.BOLD);card.addView(name)
   val preview=LinearLayout(this);preview.orientation=LinearLayout.HORIZONTAL;preview.gravity=Gravity.CENTER;preview.setPadding(0,12,0,12)
   val arr=p.getJSONArray("stickers")
   for(j in 0 until arr.length()) {
    val file=arr.getJSONObject(j).getString("image_file")
    val img=ImageView(this);val bitmap=assets.open(file).use{android.graphics.BitmapFactory.decodeStream(it)};img.setImageBitmap(bitmap)
    preview.addView(img,LinearLayout.LayoutParams(0,105,1f))
   }
   card.addView(preview)
   val btn=Button(this);btn.text="إضافة إلى واتساب  +";btn.setTextColor(Color.WHITE);btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green))
   btn.setOnClickListener { addPack(id,p.getString("name")) };card.addView(btn)
   val params=LinearLayout.LayoutParams(-1,-2);params.bottomMargin=20;layout.addView(card,params)
  }
  val note=TextView(this);note.text="نسخة تجريبية • جميع الرسومات أصلية • لا علاقة للتطبيق بشركة WhatsApp";note.gravity=Gravity.CENTER;note.setTextColor(Color.GRAY);layout.addView(note)
 }
 private fun addPack(id:String,name:String){
  val intent=Intent("com.whatsapp.intent.action.ENABLE_STICKER_PACK")
  intent.putExtra("sticker_pack_id",id)
  intent.putExtra("sticker_pack_authority",StickerProvider.AUTHORITY)
  intent.putExtra("sticker_pack_name",name)
  intent.setPackage("com.whatsapp")
  try{startActivityForResult(intent,200)}catch(e:Exception){
   intent.setPackage("com.whatsapp.w4b")
   try{startActivityForResult(intent,200)}catch(e2:Exception){Toast.makeText(this,"ثبت WhatsApp أو WhatsApp Business أولاً",Toast.LENGTH_LONG).show()}
  }
 }
}
