package xin.z7workbench.bjutloginapp.network

import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import xin.z7workbench.bjutloginapp.Constants

interface RetrofitService {
    @FormUrlEncoded
    @POST(Constants.LGN_URL + Constants.LOGIN_TAIL)
    suspend fun login4(@Body body: RequestBody)

    @GET(Constants.LGN_URL)
    suspend fun sync4(): Flow<String>

    @GET(Constants.LGN_URL + Constants.QUIT_TAIL)
    suspend fun logout4()

    @FormUrlEncoded
    @POST(Constants.IPV6_URL + Constants.LOGIN_TAIL)
    suspend fun login6(@Body body: RequestBody)

    @GET(Constants.IPV6_URL)
    suspend fun sync6(): Flow<String>

    @GET(Constants.IPV6_URL + Constants.QUIT_TAIL)
    suspend fun logout6()

    companion object {
        val service by lazy {
            Retrofit.Builder()
                    .baseUrl("")
                    .client(OkHttpNetwork.client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create<RetrofitService>()
        }
    }
}