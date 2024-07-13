package com.example.weathersnow

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weathersnow.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Mumbai")
        Searchcity()
    }

    private fun Searchcity() {
        val searchView = binding.searchview
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(city, appid = "df42333f66e8bdd7ac3cd2e1d0fbc553", units = "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature = responseBody.main.temp.toString()
                    binding.temperature.text= "$temperature °C "
                    val humidity = responseBody.main.humidity.toString()
                    val max_temp = responseBody.main.temp_max.toString()
                    val min_temp = responseBody.main.temp_min.toString()
                    val wind_soeed= responseBody.wind.speed.toString()
                    val sunrise= responseBody.sys.sunrise.toLong()
                    val sunset= responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    binding.temperature.text= "$temperature °C "
                    binding.humidity.text="$humidity %"
                    binding.maxTemp.text="Max. Temp = $max_temp °C"
                    binding.minTemp.text="Min. Temp = $min_temp °C"
                    binding.windspeed.text="$wind_soeed m/s"
                    binding.condition.text="$condition"
                    binding.weather.text="$condition"
                    binding.sea.text="$sealevel hPa"
                    binding.cityname.text="$city"
                    binding.day.text= dayname(System.currentTimeMillis())
                    binding.date.text= date()
                    binding.sunrise.text= time(sunrise)
                    binding.sunset.text= time(sunset)


                    changebackgorund(condition)
                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changebackgorund(condition : String) {
        when(condition){
            "Haze","Sunny","Clear","Clear Sky" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_)
                binding.lottieAnimationView.setAnimation(R.raw.sunny_)
            }
            "Clouds","Mist","Foggy","Overcast","Partly Clouds","scattered clouds" ->{
                binding.root.setBackgroundResource(R.drawable.cloudy_back)
                binding.lottieAnimationView.setAnimation(R.raw.cloudy__)
            }
            "Light Rain","Moderate Rain","Heavy Rain","Shower Rain","Broken Clouds","Drizzle","Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rainy__)
                binding.lottieAnimationView.setAnimation(R.raw.haze_)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard","Snow" ->{
                binding.root.setBackgroundResource(R.drawable.snow__)
                binding.lottieAnimationView.setAnimation(R.raw.snowy_)
            }

        }
        binding.lottieAnimationView.playAnimation()
        }


    private fun date(): String {
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String {
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }


    fun dayname(timestamp: Long): String{
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
}