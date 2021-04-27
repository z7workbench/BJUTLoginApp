package top.z7workbench.bjutloginapp.network

import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import top.z7workbench.bjutloginapp.Constants

interface Wired6Service {
    @GET("/")
    suspend fun sync(): String

    @FormUrlEncoded
    @POST(Constants.LOGIN_TAIL)
    suspend fun login(@Body body: RequestBody)

    @GET(Constants.QUIT_TAIL)
    suspend fun logout()

    companion object {
        val service by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.IPV6_URL)
                .client(NetworkGlobalObject.client)
//                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create<Wired6Service>()
        }
    }
}