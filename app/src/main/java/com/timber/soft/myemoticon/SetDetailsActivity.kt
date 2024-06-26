package com.timber.soft.myemoticon

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.timber.soft.myemoticon.databinding.ActivityDetailsBinding
import com.timber.soft.myemoticon.model.ChildDataModel
import com.timber.soft.myemoticon.tools.AppTools.downLoadFile
import com.timber.soft.myemoticon.tools.AppTools.dpCovertPx
import com.timber.soft.myemoticon.tools.AppVal
import com.timber.soft.myemoticon.tools.DownloadListener
import java.io.File

class SetDetailsActivity : AppCompatActivity(), DownloadListener {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var identifierName: String
    private lateinit var dataModel: ChildDataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // 设置Padding上边距留出沉浸式状态栏空间
        binding.root.setPadding(0, dpCovertPx(this), 0, 0)
        // 设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE) or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.TRANSPARENT
        }
        dataModel = intent.getSerializableExtra(AppVal.KEY_EXTRA) as ChildDataModel
        identifierName = dataModel.identifierName
        binding.backBt.setOnClickListener() {
            finish()
        }

        binding.emoTitle.text = dataModel.title

        val cacheDir = cacheDir
        val newPath: String = "$cacheDir/$identifierName"
        val zipUrl = dataModel.zipUrl
        val file = File(newPath)
        if (!file.exists()) {
            downLoadFile(this@SetDetailsActivity, zipUrl, newPath, this)
        } else {
            initImgData(newPath)
        }


        binding.addIconBt.setOnClickListener() {

            val intent = Intent()
            intent.setAction(AppVal.STICKER_ACTION)
            intent.putExtra(AppVal.KEY_PACK_ID, identifierName)
            intent.putExtra(AppVal.KEY_PACK_AUTHORITY, AppVal.AUTHOR)
            intent.putExtra(AppVal.KEY_PACK_NAME, title)
            try {
                startActivityForResult(intent, 200)
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext, "WhatsApp not found", Toast.LENGTH_SHORT
                ).show()
                val builder = AlertDialog.Builder(this@SetDetailsActivity)
                builder
                    .setTitle("WhatsApp is not installed")
                    .setMessage("Do you want to install?").setCancelable(false)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        // 执行安装WhatsApp的代码，或者跳转到Google Play Store
                        val url = getString(R.string.whatsapp_link)
                        // 创建intent打开链接
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse(url))
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                        // 取消操作
                    }
                val dialog = builder.create()
                dialog.show()

            }
        }
    }

    override fun downloadListener(
        isDownloadSuccess: Boolean, isUnzipSuccess: Boolean, newPath: String
    ) {
        if (isDownloadSuccess && isUnzipSuccess) {
            initImgData(newPath)
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                applicationContext, "Check network connection!", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initImgData(newPath: String) {
        val data: MutableList<File> = mutableListOf()
        val file = File(newPath)
        if (!file.exists()) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                applicationContext, "Check network connection!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        val fileList = file.listFiles()
        if (fileList == null) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                applicationContext, "Check network connection!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        for (listFile in fileList) {
            val name = listFile.getName()
            if (name == "tray.webp") {
                Glide.with(this@SetDetailsActivity).load(listFile)
                    .error(R.drawable.svg_img_error)
                    .into(binding.emoIcon)
            } else if (listFile.getName().endsWith(".webp")) {
                data.add(listFile)
            } else {
                val sp = this.getSharedPreferences("", Context.MODE_PRIVATE)
                sp.edit().putString(identifierName, listFile.absolutePath).apply()
            }
        }

        val stickerDetailsAdapter = StickerDetailsAdapter(
            this@SetDetailsActivity, data
        )
        binding.recyclerSticker.adapter = stickerDetailsAdapter
        binding.recyclerSticker.layoutManager = GridLayoutManager(this@SetDetailsActivity, 3)
        binding.progressBar.visibility = View.GONE
    }

}

class StickerDetailsAdapter(
    private val context: Context, private val data: List<File>

) : RecyclerView.Adapter<StickerDetailsAdapter.StickerViewHolder>() {

    inner class StickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stickerImg = itemView.findViewById<ImageView>(R.id.sticker_img)
        val stickerIndexTv = itemView.findViewById<TextView>(R.id.sticker_index_tv)
        val spaceView = itemView.findViewById<View>(R.id.spaceView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_details_img, parent, false)
        return StickerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {

        val preFile = data[position]
        Glide.with(context).load(preFile).transition(
            DrawableTransitionOptions.withCrossFade()
        )
            .error(R.drawable.svg_img_error)
            .into(holder.stickerImg)

        holder.stickerIndexTv.setText((position + 1).toString())

        if (position == data.size - 1) {
            holder.spaceView.visibility = View.VISIBLE
        } else {
            holder.spaceView.visibility = View.GONE
        }
    }

}
