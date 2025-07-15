package org.codepath.randompet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var petList: MutableList<String>
    private lateinit var rvPets: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvPets = findViewById(R.id.pet_list)
        petList = mutableListOf()

        fetchPetImages()
        //val button = findViewById<Button>(R.id.petButton)
        //setupButton(button)
    }

    private fun fetchDogImages(onComplete: (List<String>) -> Unit) {
        val client = AsyncHttpClient()
        val dogList = mutableListOf<String>()

        client["https://dog.ceo/api/breeds/image/random/10", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val petImageURL = json.jsonObject.getString("message")
                val petImageArray = json.jsonObject.getJSONArray("message")
                // val imageView: ImageView = findViewById(R.id.pet_image)

                Log.d("Dog", "response successful: $json")
                Log.d("petImageURL", "pet image URL set: $petImageURL")

                for (i in 0 until petImageArray.length()) {
                    dogList.add(petImageArray.getString(i))
                }
                onComplete(dogList)

                /*
                Glide.with(this@MainActivity)
                    .load(petImageURL)
                    .fitCenter()
                    .into(imageView)
                 */

                /*
                val adapter = PetAdapter(petList)
                rvPets.adapter = adapter
                rvPets.layoutManager = LinearLayoutManager(this@MainActivity)
                rvPets.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
                */
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Dog Error", errorResponse)
                onComplete(emptyList())
            }
        }]
    }

    private fun fetchCatImages(onComplete: (List<String>) -> Unit) {
        val client = AsyncHttpClient()
        val catList = mutableListOf<String>()
        var completed = 0

        repeat(2) {
            client["https://api.thecatapi.com/v1/images/search?limit=5", object :
                JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Headers,
                    json: JsonHttpResponseHandler.JSON
                ) {
                    val resultsJSON = json.jsonArray.getJSONObject(0)
                    val petImageURL = resultsJSON.getString("url")
                    // val imageView: ImageView = findViewById(R.id.pet_image)

                    Log.d("Cat", "response successful: $json")
                    Log.d("petImageURL", "pet image URL set: $petImageURL")

                    val array = json.jsonArray
                    for (i in 0 until array.length()) {
                        val url = array.getJSONObject(i).getString("url")
                        catList.add(url)
                    }
                    completed++
                    if (completed == 2) {
                        onComplete(catList)
                    }

                    /*
                    Glide.with(this@MainActivity)
                        .load(petImageURL)
                        .fitCenter()
                        .into(imageView)
                     */
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
    }

    private fun mergePetLists(dogs: List<String>, cats: List<String>) {
        petList.clear()
        val size = minOf(dogs.size, cats.size)
        for (i in 0 until size) {
            petList.add(dogs[i])
            petList.add(cats[i])
        }
    }

    private fun fetchPetImages() {
        petList.clear()

        var dogList: List<String>? = null
        var catList: List<String>? = null

        fun checkAndMerge() {
            if (dogList != null && catList != null) {
                mergePetLists(dogList!!, catList!!)

                val adapter = PetAdapter(petList)
                rvPets.adapter = adapter
                rvPets.layoutManager = LinearLayoutManager(this)
                rvPets.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            }
        }

        fetchDogImages { result ->
            dogList = result
            checkAndMerge()
        }

        fetchCatImages { result ->
            catList = result
            checkAndMerge()
        }
    }


    private fun setupButton(button: Button) {
        button.setOnClickListener {
            var choice = Random.nextInt(2)

            if (choice == 0) {
                fetchDogImages{dogList -> petList = dogList.toMutableList()}
            }
            else {
                fetchCatImages{catList -> petList = catList.toMutableList()}
            }
        }
    }
}