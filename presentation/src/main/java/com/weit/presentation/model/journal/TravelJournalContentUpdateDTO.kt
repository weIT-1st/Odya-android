package com.weit.presentation.model.journal

import android.os.Parcelable
import com.weit.domain.model.journal.TravelJournalContentsInfo
import com.weit.presentation.model.post.travellog.TravelPeriod
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class TravelJournalContentUpdateDTO (
    val travelJournalId: Long,
    val travelJournalContentId: Long,
    val travelJournalStart : LocalDate,
    val travelJournalEnd : LocalDate,
    val travelJournalContentDate: LocalDate,
    val content : String?,
    val placeName : String?,
    val placeID : String?,
    val latitude : List<Double>,
    val longitude : List<Double>,
    val updateContentImages: List<String>,
) : Parcelable
