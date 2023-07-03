package com.dnimara.cinemalink.ui.moviescreen

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvailabilityReportDto(val movieId: String?,
                                 val reportType: ReportType,
                                 val available: Boolean)