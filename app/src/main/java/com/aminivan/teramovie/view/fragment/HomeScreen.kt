package com.aminivan.teramovie.view.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.aminivan.teramovie.R
import com.aminivan.teramovie.database.Movie
import com.aminivan.teramovie.databinding.FragmentHomeScreenBinding
import com.aminivan.teramovie.view.adapter.MovieAdapter
import com.aminivan.teramovie.viewmodel.MovieViewModel
import com.aminivan.teramovie.viewmodel.RoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@AndroidEntryPoint
class HomeScreen : Fragment() {
    private var NOTIFICATION_PERMISSION_REQUEST_CODE = 602
    lateinit var binding : FragmentHomeScreenBinding
    private val notificationScreen = NotificationScreen()
    private val vmMovie : MovieViewModel by viewModels()
    private val vmRoom : RoomViewModel by viewModels()
    lateinit var adapter: MovieAdapter
    private var isFirstFetch = true

    private var newDataAvail = false
    private var shouldRefreshData = false
    private var latestMovieData: List<Movie>? = emptyList()


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Display a notification
            if (!newDataAvail) {
                newDataAvail = true
                if (!notificationScreen.isVisible) {
                    shouldRefreshData = false
                    notificationScreen.show(parentFragmentManager, "NotificationScreenTag")
                } else {
                    shouldRefreshData = false // Set the flag to refresh data when notification is clicked
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MovieAdapter()
        fetchDataMovie()

        vmRoom.getAllMovie().observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: observer vmRoom data ${it}")
            if (it.size > 0){
                latestMovieData = it
                if(isFirstFetch){
                    adapter.setListMovie(latestMovieData!!)
                    isFirstFetch = false
                    binding.imgNoConnection.isVisible = false
                    binding.txtInternet.isVisible = false
                }
            } else {
                    binding.imgNoConnection.isVisible = true
                    binding.txtInternet.setText("Your Movie List is Empty... :(")
                    Toast.makeText(requireContext(), "Your Movie List is Empty... :(", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvMovie.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvMovie.setHasFixedSize(true)
        binding.rvMovie.adapter = adapter


    }

    private fun fetchDataMovie() {
        if (hasNotificationPermission()) {
            lifecycleScope.launch {
                coroutineScope {
                    launch {
                        while (true) {
                            delay(10000)
                            if (shouldRefreshData) {
                                shouldRefreshData = false
                            }
                            vmMovie.getDataMovie()
                        }
                    }
                }
            }
        } else {
            requestNotificationPermission()
        }

        notificationScreen.setOnItemClickListener(object : NotificationScreen.OnItemClickListener {
            override fun onItemClick() {
                newDataAvail = false
                shouldRefreshData = true
                adapter.setListMovie(latestMovieData!!)
            }
        })

    }

    private fun hasNotificationPermission(): Boolean {
        // Check if your app has notification permission
        // For example, you can check for Manifest.permission.NOTIFICATION_POLICY
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        // Request notification permission
        // For example, you can use ActivityCompat.requestPermissions
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch data
                fetchDataMovie()
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("data_updated")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }
}