package com.eury.touristai.workers

import android.text.TextUtils
import androidx.work.Worker
import com.eury.touristai.repository.PlacesRepository
import com.eury.touristai.repository.remote.requests.PlacesRequests
import com.eury.touristai.repository.remote.requests.VisionRequests
import com.eury.touristai.repository.remote.requests.WikiRequests
import com.eury.touristai.repository.remote.services.GoogleCloudServiceGenerator
import com.eury.touristai.repository.remote.services.PlacesServiceGenerator
import com.eury.touristai.repository.remote.services.WikipediaServiceGenerator

/**
 * Created by euryperez on 5/22/18.
 * Property of Instacarro.com
 */
class FetchWikiInfoWorker : Worker() {

    private val placesRepository = PlacesRepository(GoogleCloudServiceGenerator.createService(VisionRequests::class.java),
            PlacesServiceGenerator.createService(PlacesRequests::class.java),
            WikipediaServiceGenerator.createService(WikiRequests::class.java))

    override fun doWork(): WorkerResult {
        val placeId = inputData.getString(PLACE_ID_KEY, null)
        val placeName = inputData.getString(PLACE_NAME_KEY, null)

        if(!TextUtils.isEmpty(placeId) && !TextUtils.isEmpty(placeName)) {
            val response = placesRepository.getPlaceWikiInfoSync(placeId, placeName)
            if(response.isSuccessful) {
                placesRepository.processWikiResponse(response.body(), placeId)
                return WorkerResult.SUCCESS
            }
        }

        return WorkerResult.FAILURE
    }

    companion object {
        const val PLACE_ID_KEY = "PLACE_ID_KEY"
        const val PLACE_NAME_KEY = "PLACE_NAME_KEY"
    }
}