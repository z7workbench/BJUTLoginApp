package top.z7workbench.bjutloginapp.network

import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*
import top.z7workbench.bjutloginapp.Constants

interface Wired4Service {
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
                .baseUrl(Constants.LGN_URL)
                .client(NetworkGlobalObject.client)
//                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create<Wired4Service>()
        }
    }
}