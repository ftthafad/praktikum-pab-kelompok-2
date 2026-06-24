package com.travelwaka.app.network

import com.travelwaka.app.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)

        if (com.travelwaka.app.BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("ngrok-skip-browser-warning", "true")
                    .build()
                chain.proceed(request)
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        val booleanDeserializer = com.google.gson.JsonDeserializer<Boolean> { json, _, _ ->
            if (json.isJsonPrimitive) {
                val primitive = json.asJsonPrimitive
                if (primitive.isBoolean) {
                    primitive.asBoolean
                } else if (primitive.isNumber) {
                    primitive.asInt != 0
                } else if (primitive.isString) {
                    val str = primitive.asString
                    str.lowercase() == "true" || str == "1"
                } else {
                    false
                }
            } else {
                false
            }
        }

        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(Boolean::class.java, booleanDeserializer)
            .registerTypeAdapter(Boolean::class.javaObjectType, booleanDeserializer)
            .create()

        return Retrofit.Builder()
            .baseUrl(com.travelwaka.app.BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}