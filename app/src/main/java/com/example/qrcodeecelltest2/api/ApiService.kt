import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // GET request to fetch data from the spreadsheet
    @GET("exec")
    fun getData(): Call<Map<String, List<String>>>  // Will return JSON object with "data" as key

    // POST request to send data to the spreadsheet
    @POST("exec")
    fun postData(@Body data: PostData): Call<Map<String, String>>  // Will receive "status" as response
}
