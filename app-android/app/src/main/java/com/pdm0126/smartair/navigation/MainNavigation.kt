
package com.pdm0126.smartair.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

import com.pdm0126.smartair.model.LATEST_REPORT_ID
import com.pdm0126.smartair.view.screens.Alert.AlertScreen
import com.pdm0126.smartair.view.screens.Auth.LoginScreen
import com.pdm0126.smartair.view.screens.Auth.RegisterScreen
import com.pdm0126.smartair.view.screens.AutoRoute.AutoRouteScreen
import com.pdm0126.smartair.view.screens.Connection.ConnectionScreen
import com.pdm0126.smartair.view.screens.Control.ControlScreen
import com.pdm0126.smartair.view.screens.History.HistoryScreen
import com.pdm0126.smartair.view.screens.Home.HomeScreen
import com.pdm0126.smartair.view.screens.Profile.ProfileScreen
import com.pdm0126.smartair.view.screens.Report.ReportScreen
import com.pdm0126.smartair.view.screens.Welcome.WelcomeScreen

@Composable
fun CarApp() {

  val backStack = rememberNavBackStack(Routes.Welcome)

  // Navegación entre las pestañas principales
  val goToTab: (Routes) -> Unit = { route ->

    if (backStack.lastOrNull() != route) {

      backStack.clear()
      backStack.add(Routes.Home)

      if (route != Routes.Home) {
        backStack.add(route)
      }
    }
  }

  NavDisplay(
    backStack = backStack,

    onBack = {
      backStack.removeLastOrNull()
    },

    entryProvider = entryProvider {

      // BIENVENIDA
      entry<Routes.Welcome> {
        WelcomeScreen(
          navigateToHome = {
            backStack.removeLastOrNull()
            backStack.add(Routes.Login)
          }
        )
      }

      // CONEXIÓN
      entry<Routes.Connection> {
        ConnectionScreen(
          navigateBack = {
            backStack.removeLastOrNull()
          }
        )
      }

      // INICIO
      entry<Routes.Home> {
        HomeScreen(
          onTabClick = goToTab,

          navigateToControl = {
            goToTab(Routes.Control)
          },

          navigateToRoute = {
            goToTab(Routes.AutoRoute)
          },

          navigateToConnection = {
            backStack.add(Routes.Connection)
          }
        )
      }

      // CONTROL MANUAL
      entry<Routes.Control> {
        ControlScreen(
          onTabClick = goToTab
        )
      }


      // RECORRIDO AUTOMÁTICO
      entry<Routes.AutoRoute> {
        AutoRouteScreen(
          onTabClick = goToTab,

          navigateToAlert = {
            backStack.add(Routes.Alert)
          }
        )
      }

      // REPORTE INDIVIDUAL
      entry<Routes.Report> { key ->

        ReportScreen(
          reportId = key.reportId,
          onTabClick = goToTab
        )
      }

      // ALERTA
      entry<Routes.Alert> {
        AlertScreen(
          navigateToReport = {
            backStack.add(
              Routes.Report(LATEST_REPORT_ID)
            )
          },

          navigateBack = {
            backStack.removeLastOrNull()
          }
        )
      }

      // HISTORIAL
      entry<Routes.History> {
        HistoryScreen(
          onTabClick = goToTab,

          navigateToReport = { reportId ->
            backStack.add(Routes.Report(reportId))
          }
        )
      }

      //PERFIL
      entry<Routes.Profile> {
        ProfileScreen(
          onTabClick = goToTab,
          navigateBack = {
            backStack.removeLastOrNull()
          },
          onLogout = {
            backStack.clear()
            backStack.add(Routes.Welcome)
          }
        )
      }

      //INICIO DE SESION
      entry<Routes.Login> {
        LoginScreen(
          onLoginSuccess = {
            backStack.clear()
            backStack.add(Routes.Home)
          },
          navigateToRegister = {
            backStack.add(Routes.Register)
          }
        )
      }

//REGISTRO
      entry<Routes.Register> {
        RegisterScreen(
          onRegisterSuccess = {
            backStack.clear()
            backStack.add(Routes.Home)
          },
          navigateBack = {
            backStack.removeLastOrNull()
          },
          navigateToLogin = {
            backStack.removeLastOrNull()
          }
        )
      }
    },

    // Animaciones al avanzar
    transitionSpec = {
      slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(500)
      ) togetherWith slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(500)
      )
    },

    // Animaciones al regresar
    popTransitionSpec = {
      slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(500)
      ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(500)
      )
    },

    // Animación al regresar con gesto
    predictivePopTransitionSpec = {
      slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(250)
      ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(250)
      )
    }
  )
}
