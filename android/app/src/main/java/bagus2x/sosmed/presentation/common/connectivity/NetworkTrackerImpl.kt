package bagus2x.sosmed.presentation.common.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkTrackerImpl(context: Context) : NetworkTracker {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    override val flow: Flow<NetworkTracker.Status> = callbackFlow {
        val network = connectivityManager?.activeNetwork
        val activeNetwork = connectivityManager?.getNetworkCapabilities(network)
        if (activeNetwork != null) {
            val isConnected = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
            if (isConnected) {
                send(NetworkTracker.Available)
            } else {
                send(NetworkTracker.Unavailable)
            }
        } else {
            send(NetworkTracker.Unavailable)
        }


        val networkTrackerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(NetworkTracker.Unavailable)
            }

            override fun onAvailable(network: Network) {
                trySend(NetworkTracker.Available)
            }

            override fun onLost(network: Network) {
                trySend(NetworkTracker.Unavailable)
            }
        }

        if (connectivityManager != null) {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
            connectivityManager.registerNetworkCallback(request, networkTrackerCallback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(networkTrackerCallback)
            }
        }
    }
        .distinctUntilChanged()
}
