package org.codepath.randompet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.petButton)
        setupButton(button)
        // fetchDogImage()
    }

    private fun fetchDogImage() {
        val client = AsyncHttpClient()

        client["https://dog.ceo/api/breeds/image/random", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val petImageURL = json.jsonObject.getString("message")
                val imageView: ImageView = findViewById(R.id.petImage)

                Log.d("Dog", "response successful: $json")
                Log.d("petImageURL", "pet image URL set: $petImageURL")

                Glide.with(this@MainActivity)
                    .load(petImageURL)
                    .fitCenter()
                    .into(imageView)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Dog Error", errorResponse)
            }
        }]
    }

    private fun fetchCatImage() {
        val client = AsyncHttpClient()

        client["https://api.thecatapi.com/v1/images/search", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val resultsJSON = json.jsonArray.getJSONObject(0)
                val petImageURL = resultsJSON.getString("url")
                val imageView: ImageView = findViewById(R.id.petImage)

                Log.d("Cat", "response successful: $json")
                Log.d("petImageURL", "pet image URL set: $petImageURL")

                Glide.with(this@MainActivity)
                    .load(petImageURL)
                    .fitCenter()
                    .into(imageView)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Cat Error", errorResponse)
            }
        }]
    }

    private fun setupButton(button: Button) {
        button.setOnClickListener {
            var choice = Random.nextInt(2)

            if (choice == 0) {
                fetchDogImage()
            }
            else {
                fetchCatImage()
            }
        }
    }
}