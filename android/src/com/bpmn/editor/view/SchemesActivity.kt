package com.bpmn.editor.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bpmn.editor.adapter.SchemeAdapter
import com.bpmn.editor.data.DatabaseModule
import com.bpmn.editor.databinding.ActivitySchemesBinding
import com.bpmn.editor.model.Scheme
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bpmn.editor.R
import com.bpmn.editor.adapter.SwipeToDeleteCallback
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SchemesActivity : AppCompatActivity(), SchemeAdapter.Listener {
    private val repository = DatabaseModule.provideMongoRepository(DatabaseModule.provideRealm())
    private lateinit var binding: ActivitySchemesBinding
    private val adapter = SchemeAdapter(this)
    lateinit var dialogScheme: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchemesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        dialogScheme = Dialog(this)
        repository.getSchemes().asLiveData().observe(this) { it ->
            adapter.clear()
            it.forEach {
                adapter.addScheme(it)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() = with(binding) {
        rcScheme.layoutManager = GridLayoutManager(this@SchemesActivity, 1)
        rcScheme.adapter = adapter
        addScheme.setOnClickListener {
            val addActivity = Intent(this@SchemesActivity, AddSchemeActivity::class.java)
            //val infoActivity = Intent(this@SchemesActivity, AndroidLauncher::class.java)
            //infoActivity.putExtra("portfolio", intent.getStringExtra("portfolio"))
            startActivity(addActivity)
        }
        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                delete(position)
                rcScheme.adapter?.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rcScheme)
    }

    fun delete(position: Int) {
        var scheme = adapter.deleteAt(position)
        //runBlocking { launch { repository.deleteShare(share, portfolio) } }
        runBlocking {
            launch {
                scheme.actors.forEach { repository.deleteActor(scheme, it) }
                repository.deleteScheme(scheme.name)
            }
        }
    }

    override fun onClickMove(scheme: Scheme) {
        val schemeActivity = Intent(this@SchemesActivity, AndroidLauncher::class.java)
        schemeActivity.putExtra("scheme", scheme.name)
        startActivity(schemeActivity)
    }

    override fun onClickDel(scheme: Scheme) {
        showDialogEdit(scheme)
    }

    /**
     * Метод для изменения названия.
     */
    private fun showDialogEdit(scheme: Scheme) {
        dialogScheme.setContentView(R.layout.activity_edit_scheme)
        dialogScheme.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var saveButton: ImageButton = dialogScheme.findViewById(R.id.saveEditedPortfolio)
        var newName: EditText = dialogScheme.findViewById(R.id.editedName)
        saveButton.setOnClickListener {
            runBlocking {
                launch {
                    repository.updateSchemeName(scheme.name, newName.text.toString())
                }
                runOnUiThread { dialogScheme.dismiss() }
            }
        }
        dialogScheme.show()
    }
}