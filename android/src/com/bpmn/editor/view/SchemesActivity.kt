package com.bpmn.editor.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpmn.editor.adapter.SchemeAdapter
import com.bpmn.editor.databinding.ActivitySchemesBinding
import com.bpmn.editor.model.Scheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SchemesActivity : AppCompatActivity(), SchemeAdapter.Listener {
    private lateinit var binding: ActivitySchemesBinding
    private val adapter = SchemeAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchemesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() = with(binding) {
        rcScheme.layoutManager = GridLayoutManager(this@SchemesActivity, 1)
        rcScheme.adapter = adapter
        addScheme.setOnClickListener {
            val infoActivity = Intent(this@SchemesActivity, AndroidLauncher::class.java)
            //infoActivity.putExtra("portfolio", intent.getStringExtra("portfolio"))
            startActivity(infoActivity)
        }
    }

    fun delete(position: Int) {
        var scheme = adapter.deleteAt(position)
        //runBlocking { launch { repository.deleteShare(share, portfolio) } }
    }

    override fun onClickMove(scheme: Scheme) {
        TODO("Not yet implemented")
    }

    override fun onClickDel(scheme: Scheme) {
        TODO("Not yet implemented")
    }
}