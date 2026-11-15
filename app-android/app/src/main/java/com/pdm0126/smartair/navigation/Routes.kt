package com.pdm0126.smartair.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes : NavKey {
  @Serializable
  data object Welcome : Routes()

  @Serializable
  data object Connection : Routes()

  @Serializable
  data object Home : Routes()

  @Serializable
  data object Control : Routes()

  @Serializable
  data object AutoRoute : Routes()

  @Serializable
  data class Report(val reportId: Int) : Routes()

  @Serializable
  data object Alert : Routes()

  @Serializable
  data object History : Routes()

  @Serializable
  data object Profile : Routes()

  @Serializable
  data object Login : Routes()

  @Serializable
  data object Register : Routes()
}
