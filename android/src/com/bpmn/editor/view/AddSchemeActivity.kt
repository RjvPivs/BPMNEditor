package com.bpmn.editor.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bpmn.editor.R
import com.bpmn.editor.data.DatabaseModule
import com.bpmn.editor.model.Scheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddSchemeActivity : AppCompatActivity() {
    private val repository = DatabaseModule.provideMongoRepository(DatabaseModule.provideRealm())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_scheme)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveScheme(view: View) {
        var text: EditText = findViewById(R.id.schemeInput)
        var button: ImageButton = findViewById(R.id.createScheme)
        if (text.text.isEmpty()) {
            Toast.makeText(applicationContext, "Введите название схемы", Toast.LENGTH_SHORT).show()
        } else {
            var scheme = Scheme()
            scheme.name = text.text.toString()
            // scheme.date = "11.03.2002"format().
            scheme.date = (LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            Thread {
                runOnUiThread {
                    runBlocking {
                        launch {
                            if (repository.getScheme(scheme.name) == null) {
                                repository.insertScheme(scheme)
                                val schemeActivity =
                                    Intent(this@AddSchemeActivity, SchemesActivity::class.java)
                                //infoActivity.putExtra("portfolio", portfolio.name)
                                startActivity(schemeActivity)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Такая схема уже существует!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }.start()
        }
    }
}