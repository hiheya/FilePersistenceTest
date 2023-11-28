package work.icu007.filepersistencetest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import work.icu007.filepersistencetest.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        // 加载
        val inputText = load()
        if (inputText.isNotEmpty()){
            activityMainBinding.editText.setText(inputText)
            activityMainBinding.editText.setSelection(inputText.length)
            Toast.makeText(this, "Restoring succeeded", Toast.LENGTH_SHORT).show()
        }
        // 调用getSharedPreference 方法存储数据
        activityMainBinding.saveBtn.setOnClickListener {
            // 通过getSharedPreferences()方法指定SharedPreferences的文件名为data，并得到SharedPreferences.Editor对象
            // 文件在 /data/data/work.icu007.sharedpreferencestest/shared_prefs/data.xml中
            val editor = getSharedPreferences("data",Context.MODE_PRIVATE).edit()
            // 往data.xml 中写数据
            editor.putString("name","Tom")
            editor.putInt("age", 28)
            editor.putBoolean("married", false)
            // commit
            editor.apply()
        }
        activityMainBinding.restoreBtn.setOnClickListener {
            // 先通过getSharedPreferences()方法指定文件，得到SharedPreferences对象
            val prefs = getSharedPreferences("data",Context.MODE_PRIVATE)
            // 通过特定的'key'，拿到 'value'
            val name = prefs.getString("name","null")
            val age = prefs.getInt("age",0)
            val married = prefs.getBoolean("married",false)
            Log.d(TAG, "onCreate: my name is $name, $age years-old, married: $married")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val inputText = activityMainBinding.editText.text.toString()
        save(inputText)

    }
    private fun save(inputText: String){
        try {
            // 借助FileOutputStream构建出一个OutputStreamWriter对象，
            // 接着再使用OutputStreamWriter构建出一个BufferedWriter对象
            val outputStream = openFileOutput("data", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use {
                // 往里头写东西
                it.write(inputText)
                it.flush()
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    
    private fun load(): String{
        val content = StringBuilder()
        try {
            // 首先通过`openFileInput()`方法获取了一个`FileInputStream`对象，
            // 然后借助它又构建出了一个`InputStreamReader`对象，
            // 接着再使用`InputStreamReader`构建出一个`BufferedReader`对象
            val input = openFileInput("data")
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                // `forEachLine`函数，这也是Kotlin提供的一个内置扩展函数，
                // 它会将读到的每行内容都回调到Lambda表达式中，我们在Lambda表达式中完成拼接逻辑即可
                reader.forEachLine {
                    // 通过`BufferedReader`将文件中的数据一行行读取出来，并拼接到StringBuilder对象当中
                    content.append(it)
                }
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    return content.toString()
    }
    companion object{
        const val TAG = "MainActivity"
    }
}