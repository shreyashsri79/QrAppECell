import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://script.google.com/macros/s/AKfycbw_xm24m2y1yfoa33TYr0TLr13z8P49JqtC4SCHbEjxFJs3pgKEP-_towsbIzKMLTz6Mg/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)  // The base URL for the API
            .addConverterFactory(GsonConverterFactory.create())  // JSON converter
            .build()
            .create(ApiService::class.java)
    }
}
